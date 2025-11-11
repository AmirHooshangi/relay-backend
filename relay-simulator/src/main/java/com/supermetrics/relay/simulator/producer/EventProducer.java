package com.supermetrics.relay.simulator.producer;

import com.supermetrics.relay.common.domain.DeviceEvent;

@FunctionalInterface
public interface EventProducer {
    void publish(DeviceEvent event);
}

