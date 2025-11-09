package com.supermetrics.relay.common.domain;

import java.time.Instant;

/**
 * Represents a reading from an IoT device.
 */
public class DeviceReading {
    private String deviceId;
    private Double value;
    private Instant timestamp;
    
}

