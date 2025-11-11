package com.supermetrics.relay.simulator;

import com.supermetrics.relay.common.domain.DeviceEvent;
import com.supermetrics.relay.common.domain.DeviceType;
import com.supermetrics.relay.simulator.DeviceSimulator.DeviceConfig;
import com.supermetrics.relay.simulator.producer.EventProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

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
    void generatesEvents() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(2);
        producer.setLatch(latch);
        
        simulator.generateAndSendEvent();
        simulator.generateAndSendEvent();
        
        assertTrue(latch.await(1, TimeUnit.SECONDS));
        
        List<DeviceEvent> events = producer.getPublishedEvents();
        assertTrue(events.size() >= 2);
        
        DeviceEvent event = events.get(0);
        assertNotNull(event.deviceId());
        assertNotNull(event.value());
        assertNotNull(event.timestamp());
    }
    
    @Test
    void rotatesThroughDevices() {
        int deviceCount = simulator.getDeviceConfigs().size();
        
        for (int i = 0; i < deviceCount * 2; i++) {
            simulator.generateAndSendEvent();
        }
        
        List<DeviceEvent> events = producer.getPublishedEvents();
        assertEquals(deviceCount * 2, events.size());
    }
    
    @Test
    void hasDefaultDevices() {
        DeviceSimulator defaultSimulator = new DeviceSimulator(producer, taskScheduler);
        List<DeviceConfig> deviceConfigs = defaultSimulator.getDeviceConfigs();
        
        assertTrue(deviceConfigs.size() >= 3);
        
        boolean hasThermostat = deviceConfigs.stream()
            .anyMatch(d -> d.deviceType() == DeviceType.THERMOSTAT);
        boolean hasHeartRate = deviceConfigs.stream()
            .anyMatch(d -> d.deviceType() == DeviceType.HEART_RATE_METER);
        boolean hasCarFuel = deviceConfigs.stream()
            .anyMatch(d -> d.deviceType() == DeviceType.CAR_FUEL);
        
        assertTrue(hasThermostat);
        assertTrue(hasHeartRate);
        assertTrue(hasCarFuel);
    }
    
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
    }
}
