package io.vdev.encoder;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vdev.model.Alarm;
import lombok.SneakyThrows;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

@ApplicationScoped
public class AlarmEncoder implements Encoder.Text<Alarm> {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init(EndpointConfig endpointConfig) {
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    @Override
    public void destroy() {

    }

    @SneakyThrows
    @Override
    public String encode(Alarm alarm) throws EncodeException {
        return objectMapper.writeValueAsString(alarm);
    }
}
