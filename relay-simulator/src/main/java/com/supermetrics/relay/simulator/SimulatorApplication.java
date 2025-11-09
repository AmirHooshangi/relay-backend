package com.supermetrics.relay.simulator;

import com.supermetrics.relay.simulator.producer.KafkaEventProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * Main application for IoT Device Simulator.
 * Simulates multiple IoT devices sending events to Kafka every second.
 */
@SpringBootApplication
@EnableScheduling
public class SimulatorApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(SimulatorApplication.class, args);
    }
    
    @Bean
    public KafkaEventProducer kafkaEventProducer(@Value("${kafka.bootstrap-servers}") String bootstrapServers) {
        return new KafkaEventProducer(bootstrapServers);
    }
    
    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(10);
        scheduler.setThreadNamePrefix("device-simulator-");
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(5);
        scheduler.initialize();
        return scheduler;
    }
    
    @Bean
    public DeviceSimulator deviceSimulator(KafkaEventProducer producer, ThreadPoolTaskScheduler taskScheduler) {
        return new DeviceSimulator(producer, taskScheduler);
    }
}

