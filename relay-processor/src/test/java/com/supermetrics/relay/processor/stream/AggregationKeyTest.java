package com.supermetrics.relay.processor.stream;

import com.supermetrics.relay.common.domain.DeviceType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AggregationKeyTest {

    @Test
    void createKey() {
        AggregationKey key = new AggregationKey(
            "device-123",
            "zone-1",
            DeviceType.THERMOSTAT
        );

        assertEquals("device-123", key.deviceId());
        assertEquals("zone-1", key.zone());
        assertEquals(DeviceType.THERMOSTAT, key.deviceType());
    }

    @Test
    void toKafkaKey() {
        AggregationKey key = new AggregationKey(
            "device-123",
            "zone-1",
            DeviceType.THERMOSTAT
        );

        String kafkaKey = key.toKafkaKey();
        assertEquals("device-123|zone-1|THERMOSTAT", kafkaKey);
    }

    @Test
    void fromKafkaKey() {
        String kafkaKey = "device-123|zone-1|THERMOSTAT";
        AggregationKey key = AggregationKey.fromKafkaKey(kafkaKey);

        assertEquals("device-123", key.deviceId());
        assertEquals("zone-1", key.zone());
        assertEquals(DeviceType.THERMOSTAT, key.deviceType());
    }

    @Test
    void roundTrip() {
        AggregationKey original = new AggregationKey(
            "device-123",
            "zone-1",
            DeviceType.THERMOSTAT
        );

        String kafkaKey = original.toKafkaKey();
        AggregationKey restored = AggregationKey.fromKafkaKey(kafkaKey);

        assertEquals(original.deviceId(), restored.deviceId());
        assertEquals(original.zone(), restored.zone());
        assertEquals(original.deviceType(), restored.deviceType());
    }

    @Test
    void rejectsNullValues() {
        assertThrows(NullPointerException.class, () -> {
            new AggregationKey(null, "zone-1", DeviceType.THERMOSTAT);
        });
    }
}
