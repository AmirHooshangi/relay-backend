package com.supermetrics.relay.processor.stream;

import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;

import java.nio.charset.StandardCharsets;

public class AggregationKeySerde implements Serde<AggregationKey> {
    
    @Override
    public Serializer<AggregationKey> serializer() {
        return new Serializer<AggregationKey>() {
            @Override
            public byte[] serialize(String topic, AggregationKey data) {
                if (data == null) {
                    return null;
                }
                return data.toKafkaKey().getBytes(StandardCharsets.UTF_8);
            }
        };
    }
    
    @Override
    public Deserializer<AggregationKey> deserializer() {
        return new Deserializer<AggregationKey>() {
            @Override
            public AggregationKey deserialize(String topic, byte[] data) {
                if (data == null) {
                    return null;
                }
                String key = new String(data, StandardCharsets.UTF_8);
                return AggregationKey.fromKafkaKey(key);
            }
        };
    }
}

