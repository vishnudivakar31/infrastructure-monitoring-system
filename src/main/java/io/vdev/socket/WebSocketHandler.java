package io.vdev.socket;

import io.vdev.encoder.StatEncoder;
import io.vdev.model.Stat;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@ServerEndpoint(value = "/system-stats", encoders = {StatEncoder.class})
@ApplicationScoped
public class WebSocketHandler {

    private Map<String, Session> sessionMap = new ConcurrentHashMap<>();

    @OnOpen
    public void onOpen(Session session) {
        log.info("a new user joined the session. {}", session.getId());
        sessionMap.put(session.getId(), session);
    }

    @OnClose
    public void onClose(Session session) {
        log.info("user left the session. {}", session.getId());
        sessionMap.remove(session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.error("error occurred for session id {} and error: {}", session.getId(), throwable);
        sessionMap.remove(session.getId());
    }

    @Incoming("system-stats")
    public void getSystemStats(Stat stat) {
        log.info("system-stat: {}", stat);
        broadcastSystemStat(stat);
    }

    private void broadcastSystemStat(Stat stat) {
        sessionMap.values().forEach(session -> {
            session.getAsyncRemote().sendObject(stat, result -> {
                if (result.getException() != null) {
                    log.error("session broadcast error -- session id: {} -- exception: {}", session.getId(), result.getException().getMessage());
                } else {
                    log.info("broadcast successful -- session id: {} -- status: {}", session.getId(), result.isOK());
                }
            });
        });
    }
}
