package com.supermetrics.relay.processor.stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.io.IOException;

public class AggregationValueSerde implements Serde<AggregationValue> {
    
    private final ObjectMapper objectMapper;
    
    public AggregationValueSerde(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    @Override
    public Serializer<AggregationValue> serializer() {
        return new Serializer<AggregationValue>() {
            @Override
            public byte[] serialize(String topic, AggregationValue data) {
                if (data == null) {
                    return null;
                }
                try {
                    return objectMapper.writeValueAsBytes(data);
                } catch (Exception e) {
                    throw new RuntimeException("Error serializing AggregationValue", e);
                }
            }
        };
    }
    
    @Override
    public Deserializer<AggregationValue> deserializer() {
        return new Deserializer<AggregationValue>() {
            @Override
            public AggregationValue deserialize(String topic, byte[] data) {
                if (data == null) {
                    return null;
                }
                try {
                    return objectMapper.readValue(data, AggregationValue.class);
                } catch (IOException e) {
                    throw new RuntimeException("Error deserializing AggregationValue", e);
                }
            }
        };
    }
}

