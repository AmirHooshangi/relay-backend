package com.supermetrics.relay.simulator.producer;

import com.supermetrics.relay.common.domain.DeviceEvent;

/**
 * Publishes device events to Kafka.
 */
@FunctionalInterface
public interface EventProducer {
    void publish(DeviceEvent event);
}

