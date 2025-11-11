package com.supermetrics.relay.processor.stream;

import com.supermetrics.relay.common.domain.DeviceType;
import java.util.Objects;

public record AggregationKey(
    String deviceId,
    String zone,
    DeviceType deviceType
) {
    public AggregationKey {
        Objects.requireNonNull(deviceId, "deviceId cannot be null");
        Objects.requireNonNull(zone, "zone cannot be null");
        Objects.requireNonNull(deviceType, "deviceType cannot be null");
    }
    
    public String toKafkaKey() {
        return String.format("%s|%s|%s", deviceId, zone, deviceType.name());
    }
    
    public static AggregationKey fromKafkaKey(String key) {
        String[] parts = key.split("\\|");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid aggregation key format: " + key);
        }
        return new AggregationKey(parts[0], parts[1], DeviceType.valueOf(parts[2]));
    }
}

