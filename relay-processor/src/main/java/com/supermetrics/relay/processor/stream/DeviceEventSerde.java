package com.supermetrics.relay.processor.stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supermetrics.relay.common.domain.DeviceEvent;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.io.IOException;

public class DeviceEventSerde implements Serde<DeviceEvent> {
    
    private final ObjectMapper objectMapper;
    
    public DeviceEventSerde(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    @Override
    public Serializer<DeviceEvent> serializer() {
        return new Serializer<DeviceEvent>() {
            @Override
            public byte[] serialize(String topic, DeviceEvent data) {
                if (data == null) {
                    return null;
                }
                try {
                    return objectMapper.writeValueAsBytes(data);
                } catch (Exception e) {
                    throw new RuntimeException("Error serializing DeviceEvent", e);
                }
            }
        };
    }
    
    @Override
    public Deserializer<DeviceEvent> deserializer() {
        return new Deserializer<DeviceEvent>() {
            @Override
            public DeviceEvent deserialize(String topic, byte[] data) {
                if (data == null) {
                    return null;
                }
                try {
                    return objectMapper.readValue(data, DeviceEvent.class);
                } catch (IOException e) {
                    throw new RuntimeException("Error deserializing DeviceEvent", e);
                }
            }
        };
    }
}

