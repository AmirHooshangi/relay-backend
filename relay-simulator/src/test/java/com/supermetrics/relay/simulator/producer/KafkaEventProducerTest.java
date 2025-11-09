package com.supermetrics.relay.simulator.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.supermetrics.relay.common.domain.DeviceEvent;
import com.supermetrics.relay.common.domain.DeviceType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for KafkaEventProducer serialization logic.
 */
class KafkaEventProducerTest {
    
    private ObjectMapper objectMapper;
    
    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }
    
    @Test
    void testEventSerialization() throws Exception {
        DeviceEvent event = new DeviceEvent(
            "test-device-1",
            DeviceType.THERMOSTAT,
            "Zone A",
            25.5,
            Instant.parse("2024-01-01T12:00:00Z")
        );
        
        String json = objectMapper.writeValueAsString(event);
        
        assertNotNull(json);
        assertTrue(json.contains("test-device-1"));
        assertTrue(json.contains("25.5"));
        assertTrue(json.contains("deviceId"));
        assertTrue(json.contains("deviceType"));
        assertTrue(json.contains("value"));
        assertTrue(json.contains("timestamp"));
        
        DeviceEvent deserialized = objectMapper.readValue(json, DeviceEvent.class);
        assertEquals(event.deviceId(), deserialized.deviceId());
        assertEquals(event.deviceType(), deserialized.deviceType());
        assertEquals(event.value(), deserialized.value());
        assertEquals(event.timestamp(), deserialized.timestamp());
    }
    
    @Test
    void testEventValidation() {
        DeviceEvent event = new DeviceEvent(
            "test-device-1",
            DeviceType.THERMOSTAT,
            "Zone A",
            25.5,
            Instant.now()
        );
        
        assertNotNull(event.deviceId());
        assertNotNull(event.deviceType());
        assertNotNull(event.value());
        assertNotNull(event.timestamp());
        assertEquals("test-device-1", event.deviceId());
        assertEquals(DeviceType.THERMOSTAT, event.deviceType());
        assertEquals(25.5, event.value());
    }
}

