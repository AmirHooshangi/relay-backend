package com.supermetrics.relay.common.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

/**
 * Represents an event from an IoT device.
 * Immutable data carrier for device events in the event stream.
 */
public record DeviceEvent(
    @JsonProperty("deviceId") String deviceId,
    @JsonProperty("deviceType") DeviceType deviceType,
    @JsonProperty("zone") String zone,
    @JsonProperty("value") Double value,
    @JsonProperty("timestamp") Instant timestamp
) {
}

