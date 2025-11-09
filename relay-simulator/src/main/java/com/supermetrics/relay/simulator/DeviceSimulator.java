package com.supermetrics.relay.simulator;

import com.supermetrics.relay.common.domain.DeviceEvent;
import com.supermetrics.relay.common.domain.DeviceType;
import com.supermetrics.relay.simulator.producer.EventProducer;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;

/**
 * Simulates multiple IoT devices generating events.
 * Each device sends a new event every second using Spring's scheduler.
 */
@Component
public class DeviceSimulator {
    private static final Logger logger = LoggerFactory.getLogger(DeviceSimulator.class);
    
    public record DeviceConfig(String deviceId, DeviceType deviceType, String zone) {}
    
    private final EventProducer producer;
    private final List<DeviceConfig> deviceConfigs;
    private final Random random;
    private final TaskScheduler taskScheduler;
    private final List<ScheduledFuture<?>> scheduledTasks = new ArrayList<>();
    private int currentDeviceIndex = 0;
    
    public DeviceSimulator(EventProducer producer, TaskScheduler taskScheduler) {
        this.producer = producer;
        this.taskScheduler = taskScheduler;
        this.deviceConfigs = createDefaultDeviceConfigs();
        this.random = new Random();
    }
    
    public DeviceSimulator(EventProducer producer, TaskScheduler taskScheduler, List<DeviceConfig> deviceConfigs) {
        this.producer = producer;
        this.taskScheduler = taskScheduler;
        this.deviceConfigs = deviceConfigs;
        this.random = new Random();
    }
    
    @PostConstruct
    public void start() {
        logger.info("Starting device simulator with {} devices", deviceConfigs.size());
        
        for (DeviceConfig config : deviceConfigs) {
            ScheduledFuture<?> task = taskScheduler.scheduleAtFixedRate(
                () -> generateAndSendEvent(config),
                java.time.Duration.ofSeconds(1)
            );
            scheduledTasks.add(task);
        }
        
        logger.info("Device simulator started. Each device will send events every second");
    }
    
    private void generateAndSendEvent(DeviceConfig config) {
        try {
            Double value = generateRandomValue();
            DeviceEvent event = new DeviceEvent(
                config.deviceId(),
                config.deviceType(),
                config.zone(),
                value,
                Instant.now()
            );
            
            producer.publish(event);
            logger.debug("Generated and sent event: deviceId={}, value={}, type={}",
                config.deviceId(), value, config.deviceType());
        } catch (Exception e) {
            logger.error("Error generating event for device: {}", config.deviceId(), e);
        }
    }
    
    /**
     * Generates and sends an event for the next device in rotation.
     * This method cycles through all configured devices.
     */
    public void generateAndSendEvent() {
        if (deviceConfigs.isEmpty()) {
            logger.warn("No device configs available");
            return;
        }
        DeviceConfig config = deviceConfigs.get(currentDeviceIndex);
        generateAndSendEvent(config);
        currentDeviceIndex = (currentDeviceIndex + 1) % deviceConfigs.size();
    }
    
    /**
     * Stops all scheduled tasks.
     */
    public void stop() {
        logger.info("Stopping device simulator");
        for (ScheduledFuture<?> task : scheduledTasks) {
            if (task != null && !task.isCancelled()) {
                task.cancel(false);
            }
        }
        scheduledTasks.clear();
        logger.info("Device simulator stopped");
    }
    
    private Double generateRandomValue() {
        return random.nextDouble() * 100.0;
    }
    
    public List<DeviceConfig> getDeviceConfigs() {
        return new ArrayList<>(deviceConfigs);
    }
    
    private List<DeviceConfig> createDefaultDeviceConfigs() {
        List<DeviceConfig> configs = new ArrayList<>();
        configs.add(new DeviceConfig("thermostat-001", DeviceType.THERMOSTAT, "Zone A"));
        configs.add(new DeviceConfig("heart-rate-001", DeviceType.HEART_RATE_METER, "Zone B"));
        configs.add(new DeviceConfig("car-fuel-001", DeviceType.CAR_FUEL, "Zone C"));
        return configs;
    }
}

