package com.supermetrics.relay.processor.stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supermetrics.relay.common.domain.DeviceEvent;
import com.supermetrics.relay.common.entity.DeviceEventAggregation;
import com.supermetrics.relay.processor.service.EventProcessingService;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;

import java.time.Duration;
import java.time.Instant;

import static org.apache.kafka.common.serialization.Serdes.String;

@Configuration
@EnableKafkaStreams
public class DeviceEventStreamProcessor {
    private static final Logger logger = LoggerFactory.getLogger(DeviceEventStreamProcessor.class);
    
    private final EventProcessingService eventProcessingService;
    private final ObjectMapper objectMapper;
    private final String inputTopic;
    private final AggregationKeySerde keySerde;
    private final AggregationValueSerde valueSerde;
    private final DeviceEventSerde deviceEventSerde;
    
    private static final Duration HOURLY_WINDOW = Duration.ofHours(1);
    
    @Autowired
    public DeviceEventStreamProcessor(
            EventProcessingService eventProcessingService,
            ObjectMapper objectMapper,
            @Value("${spring.kafka.topics.input}") String inputTopic) {
        this.eventProcessingService = eventProcessingService;
        this.objectMapper = objectMapper;
        this.inputTopic = inputTopic;
        this.keySerde = new AggregationKeySerde();
        this.valueSerde = new AggregationValueSerde(objectMapper);
        this.deviceEventSerde = new DeviceEventSerde(objectMapper);
        logger.info("DeviceEventStreamProcessor initialized with topic: {}", inputTopic);
    }
    
    @Bean
    public KStream<String, String> kStream(StreamsBuilder streamsBuilder) {
        logger.info("Building Kafka Streams topology with windowed aggregations for topic: {}", inputTopic);
        
        KStream<String, String> eventStream = streamsBuilder.stream(
            inputTopic,
            Consumed.with(String(), String())
        );
        
        KStream<String, DeviceEvent> parsedStream = eventStream
            .mapValues((key, jsonValue) -> {
                try {
                    return objectMapper.readValue(jsonValue, DeviceEvent.class);
                } catch (Exception e) {
                    logger.error("Error parsing event JSON: {}", jsonValue, e);
                    return null;
                }
            })
            .filter((key, event) -> event != null);
        
        Grouped<AggregationKey, DeviceEvent> grouped = Grouped.with(keySerde, deviceEventSerde);
        KGroupedStream<AggregationKey, DeviceEvent> groupedStream = parsedStream.groupBy(
            (key, event) -> new AggregationKey(
                event.deviceId(),
                event.zone(),
                event.deviceType()
            ),
            grouped
        );
        
        TimeWindows hourlyWindows = TimeWindows.ofSizeAndGrace(HOURLY_WINDOW, Duration.ofMinutes(5));
        KTable<Windowed<AggregationKey>, AggregationValue> hourlyAggregations = groupedStream
            .windowedBy(hourlyWindows)
            .aggregate(
                AggregationValue::new,
                (key, event, aggregate) -> {
                    aggregate.add(event.value());
                    return aggregate;
                },
                Materialized.with(keySerde, valueSerde)
            );
        
        hourlyAggregations
            .toStream()
            .foreach((windowedKey, aggregation) -> {
                try {
                    AggregationKey key = windowedKey.key();
                    Instant windowStart = Instant.ofEpochMilli(windowedKey.window().start());
                    Instant windowEnd = Instant.ofEpochMilli(windowedKey.window().end());
                    
                    eventProcessingService.persistAggregation(
                        key.deviceId(), key.zone(), key.deviceType(),
                        windowStart, windowEnd, DeviceEventAggregation.WindowType.HOURLY,
                        aggregation.getAverage(), aggregation.getMin(), aggregation.getMax(), 
                        aggregation.getMedian(), aggregation.getCount()
                    );
                    
                    logger.debug("Persisted hourly aggregation: key={}, count={}", key, aggregation.getCount());
                } catch (Exception e) {
                    logger.error("Error persisting hourly aggregation", e);
                }
            });
        
        logger.info("Kafka Streams topology built successfully with windowed aggregations");
        return eventStream;
    }
}

