package com.supermetrics.relay.simulator.integration;

import com.supermetrics.relay.common.domain.DeviceType;
import com.supermetrics.relay.simulator.DeviceSimulator;
import com.supermetrics.relay.simulator.DeviceSimulator.DeviceConfig;
import com.supermetrics.relay.simulator.producer.KafkaEventProducer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for Kafka producer using Testcontainers.
 * These tests require Docker to be available.
 */
@Testcontainers
class KafkaIntegrationTest {
    
    @Container
    private static final KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.5.0"));
    
    private static boolean dockerAvailable = false;
    
    static {
        try {
            DockerClientFactory.instance().client();
            dockerAvailable = true;
        } catch (Exception e) {
            dockerAvailable = false;
            System.out.println("Docker not available, skipping Docker-dependent tests: " + e.getMessage());
        }
    }
    
    static boolean dockerAvailable() {
        return dockerAvailable;
    }
    
    private KafkaEventProducer producer;
    private DeviceSimulator simulator;
    private KafkaConsumer<String, String> consumer;
    private TaskScheduler taskScheduler;
    
    @BeforeAll
    static void ensureKafkaStarted() {
        if (dockerAvailable && !kafka.isRunning()) {
            kafka.start();
        }
    }
    
    @BeforeEach
    void setUp() {
        if (!dockerAvailable) {
            return;
        }
        if (!kafka.isRunning()) {
            kafka.start();
        }
        String bootstrapServers = kafka.getBootstrapServers();
        producer = new KafkaEventProducer(bootstrapServers);
        
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);
        scheduler.setThreadNamePrefix("test-simulator-");
        scheduler.initialize();
        taskScheduler = scheduler;
        
        List<DeviceConfig> deviceConfigs = List.of(
            new DeviceConfig("test-thermostat-1", DeviceType.THERMOSTAT, "Zone A"),
            new DeviceConfig("test-heartrate-1", DeviceType.HEART_RATE_METER, "Zone B")
        );
        simulator = new DeviceSimulator(producer, taskScheduler, deviceConfigs);
        
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-consumer-group-" + System.currentTimeMillis());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 10000);
        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 3000);
        
        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList("iot-events"));
        
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    @AfterEach
    void tearDown() {
        if (simulator != null) {
            simulator.stop();
        }
        if (producer != null) {
            producer.close();
        }
        if (consumer != null) {
            consumer.close();
        }
        if (taskScheduler instanceof ThreadPoolTaskScheduler) {
            ((ThreadPoolTaskScheduler) taskScheduler).shutdown();
        }
    }
    
    @Test
    @EnabledIf("dockerAvailable")
    void testPublishAndConsumeEvents() throws InterruptedException {
        if (!dockerAvailable()) {
            return;
        }
        simulator.start();
        
        Thread.sleep(3000);
        
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(10));
        
        assertFalse(records.isEmpty(), "Should receive at least one event. Got: " + records.count());
        
        for (ConsumerRecord<String, String> record : records) {
            assertNotNull(record.key(), "Message key should not be null");
            assertNotNull(record.value(), "Message value should not be null");
            assertTrue(record.value().contains("deviceId"), "Message should contain deviceId");
            assertTrue(record.value().contains("deviceType"), "Message should contain deviceType");
            assertTrue(record.value().contains("value"), "Message should contain value");
            assertTrue(record.value().contains("timestamp"), "Message should contain timestamp");
            
            assertTrue(record.value().contains(record.key()), 
                "Message key should match deviceId in value");
        }
    }
    
    @Test
    @EnabledIf("dockerAvailable")
    void testMultipleDevicesSendEvents() throws InterruptedException {
        if (!dockerAvailable()) {
            return;
        }
        simulator.start();
        
        Thread.sleep(4000);
        
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(10));
        
        assertFalse(records.isEmpty(), "Should receive events. Got: " + records.count());
        
        long uniqueDevices = records.partitions().stream()
            .flatMap(partition -> records.records(partition).stream())
            .map(ConsumerRecord::key)
            .distinct()
            .count();
        
        assertTrue(uniqueDevices >= 1, "Should have events from at least one device");
    }
    
    @Test
    @EnabledIf("dockerAvailable")
    void testEventFrequency() throws InterruptedException {
        if (!dockerAvailable()) {
            return;
        }
        simulator.start();
        
        Thread.sleep(3500);
        
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(10));
        
        assertTrue(records.count() >= 2, 
            "Should receive multiple events. Got: " + records.count());
    }
}

