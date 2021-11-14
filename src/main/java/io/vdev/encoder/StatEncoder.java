package io.vdev.encoder;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vdev.model.Stat;
import lombok.SneakyThrows;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

@ApplicationScoped
public class StatEncoder implements Encoder.Text<Stat> {

    private ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    @Override
    public String encode(Stat stat) {
        return objectMapper.writeValueAsString(stat);
    }

    @Override
    public void init(EndpointConfig endpointConfig) {
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    @Override
    public void destroy() {

    }
}
