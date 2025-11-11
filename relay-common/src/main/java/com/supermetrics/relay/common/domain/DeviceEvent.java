package com.supermetrics.relay.common.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;

public record DeviceEvent(
    @JsonProperty("deviceId") String deviceId,
    @JsonProperty("deviceType") DeviceType deviceType,
    @JsonProperty("zone") String zone,
    @JsonProperty("value") Double value,
    @JsonProperty("timestamp") Instant timestamp
) {
}

