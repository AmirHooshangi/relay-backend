package com.supermetrics.relay.simulator;

import com.supermetrics.relay.common.domain.DeviceEvent;
import com.supermetrics.relay.common.domain.DeviceType;
import com.supermetrics.relay.simulator.DeviceSimulator.DeviceConfig;
import com.supermetrics.relay.simulator.producer.EventProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DeviceSimulator.
 */
class DeviceSimulatorTest {
    
    private TestEventProducer producer;
    private ThreadPoolTaskScheduler taskScheduler;
    private DeviceSimulator simulator;
    
    @BeforeEach
    void setUp() {
        producer = new TestEventProducer();
        taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(1);
        taskScheduler.initialize();
        
        List<DeviceConfig> deviceConfigs = List.of(
            new DeviceConfig("test-device-1", DeviceType.THERMOSTAT, "Zone A"),
            new DeviceConfig("test-device-2", DeviceType.HEART_RATE_METER, "Zone B")
        );
        simulator = new DeviceSimulator(producer, taskScheduler, deviceConfigs);
    }
    
    @Test
    void testGetDeviceConfigs() {
        List<DeviceConfig> deviceConfigs = simulator.getDeviceConfigs();
        assertEquals(2, deviceConfigs.size());
        assertEquals("test-device-1", deviceConfigs.get(0).deviceId());
        assertEquals("test-device-2", deviceConfigs.get(1).deviceId());
    }
    
    @Test
    void testEventGeneration() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(2);
        producer.setLatch(latch);
        
        simulator.generateAndSendEvent();
        simulator.generateAndSendEvent();
        
        assertTrue(latch.await(1, TimeUnit.SECONDS), "Should generate events");
        
        List<DeviceEvent> events = producer.getPublishedEvents();
        assertTrue(events.size() >= 2, "Should have at least 2 events");
        
        DeviceEvent event = events.get(0);
        assertNotNull(event.deviceId());
        assertNotNull(event.value());
        assertNotNull(event.timestamp());
        assertTrue(event.timestamp().isBefore(Instant.now().plusSeconds(1)));
    }
    
    @Test
    void testMultipleDevicesRotation() {
        int deviceCount = simulator.getDeviceConfigs().size();
        
        for (int i = 0; i < deviceCount * 2; i++) {
            simulator.generateAndSendEvent();
        }
        
        List<DeviceEvent> events = producer.getPublishedEvents();
        assertEquals(deviceCount * 2, events.size());
        
        DeviceEvent firstEvent = events.get(0);
        DeviceEvent secondEvent = events.get(1);
        
        assertNotNull(firstEvent.deviceId());
        assertNotNull(secondEvent.deviceId());
    }
    
    @Test
    void testDefaultDevices() {
        DeviceSimulator defaultSimulator = new DeviceSimulator(producer, taskScheduler);
        List<DeviceConfig> deviceConfigs = defaultSimulator.getDeviceConfigs();
        
        assertTrue(deviceConfigs.size() >= 3, "Should have at least 3 default devices");
        
        boolean hasThermostat = deviceConfigs.stream()
            .anyMatch(d -> d.deviceType() == DeviceType.THERMOSTAT);
        boolean hasHeartRate = deviceConfigs.stream()
            .anyMatch(d -> d.deviceType() == DeviceType.HEART_RATE_METER);
        boolean hasCarFuel = deviceConfigs.stream()
            .anyMatch(d -> d.deviceType() == DeviceType.CAR_FUEL);
        
        assertTrue(hasThermostat, "Should have a thermostat device");
        assertTrue(hasHeartRate, "Should have a heart rate meter device");
        assertTrue(hasCarFuel, "Should have a car fuel device");
    }
    
    @Test
    void testValueRange() {
        simulator.generateAndSendEvent();
        
        List<DeviceEvent> events = producer.getPublishedEvents();
        assertFalse(events.isEmpty(), "Should have at least one event");
        
        DeviceEvent event = events.get(0);
        
        assertNotNull(event.deviceId());
        assertNotNull(event.deviceType());
        assertNotNull(event.zone());
        assertNotNull(event.value());
        assertTrue(event.value() >= 0.0, "Value should be non-negative");
    }
    
    /**
     * Test implementation of EventProducer that captures published events.
     */
    private static class TestEventProducer implements EventProducer {
        private final List<DeviceEvent> publishedEvents = new ArrayList<>();
        private CountDownLatch latch;
        
        @Override
        public void publish(DeviceEvent event) {
            publishedEvents.add(event);
            if (latch != null) {
                latch.countDown();
            }
        }
        
        public List<DeviceEvent> getPublishedEvents() {
            return new ArrayList<>(publishedEvents);
        }
        
        public void setLatch(CountDownLatch latch) {
            this.latch = latch;
        }
        
        public void clear() {
            publishedEvents.clear();
        }
    }
}

