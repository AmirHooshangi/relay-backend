package com.supermetrics.relay.simulator.producer;

import com.supermetrics.relay.common.domain.DeviceReading;

/**
 * Publishes device readings to Kafka.
 */
public interface ReadingProducer {
    void publish(DeviceReading reading);
}

