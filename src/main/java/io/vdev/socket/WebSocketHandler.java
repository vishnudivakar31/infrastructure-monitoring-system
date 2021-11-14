package io.vdev.socket;

import io.vdev.model.Stat;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import javax.enterprise.context.ApplicationScoped;

@Slf4j
@ApplicationScoped
public class WebSocketHandler {

    @Incoming("system-stats")
    public void getSystemStats(Stat stat) {
        log.info("system-stat: {}", stat);
    }
}
