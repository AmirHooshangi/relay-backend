package com.supermetrics.relay.processor.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serde;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KafkaStreamsConfigTest {

    private KafkaStreamsConfig config;

    @BeforeEach
    void setUp() {
        config = new KafkaStreamsConfig();
    }

    @Test
    void objectMapperHandlesInstant() throws Exception {
        ObjectMapper objectMapper = config.objectMapper();

        java.time.Instant instant = java.time.Instant.parse("2024-01-01T12:00:00Z");
        String json = objectMapper.writeValueAsString(instant);
        java.time.Instant deserialized = objectMapper.readValue(json, java.time.Instant.class);

        assertEquals(instant, deserialized);
    }

    @Test
    void stringSerdeWorks() {
        Serde<String> stringSerde = config.stringSerde();
        String original = "test-string";

        byte[] serialized = stringSerde.serializer().serialize("test-topic", original);
        String deserialized = stringSerde.deserializer().deserialize("test-topic", serialized);

        assertEquals(original, deserialized);
    }
}
