package com.supermetrics.relay.simulator.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.supermetrics.relay.common.domain.DeviceEvent;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Kafka-based implementation of EventProducer.
 * Publishes device events to Kafka topic as JSON messages.
 */
public class KafkaEventProducer implements EventProducer {
    private static final Logger logger = LoggerFactory.getLogger(KafkaEventProducer.class);
    private static final String TOPIC_NAME = "iot-events";
    
    private final KafkaProducer<String, String> producer;
    private final ObjectMapper objectMapper;
    
    public KafkaEventProducer(String bootstrapServers) {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.producer = createProducer(bootstrapServers);
    }
    
    private KafkaProducer<String, String> createProducer(String bootstrapServers) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 3);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        
        return new KafkaProducer<>(props);
    }
    
    @Override
    public void publish(DeviceEvent event) {
        try {
            String jsonValue = objectMapper.writeValueAsString(event);
            ProducerRecord<String, String> record = new ProducerRecord<>(
                TOPIC_NAME,
                event.deviceId(),
                jsonValue
            );
            
            producer.send(record, (metadata, exception) -> {
                if (exception != null) {
                    logger.error("Failed to send event to Kafka: {}", event, exception);
                } else {
                    logger.debug("Successfully sent event to Kafka: topic={}, partition={}, offset={}, deviceId={}",
                        metadata.topic(), metadata.partition(), metadata.offset(), event.deviceId());
                }
            });
        } catch (Exception e) {
            logger.error("Error serializing or sending event to Kafka: {}", event, e);
        }
    }
    
    @PreDestroy
    public void close() {
        if (producer != null) {
            producer.flush();
            producer.close();
            logger.info("Kafka producer closed");
        }
    }
}

