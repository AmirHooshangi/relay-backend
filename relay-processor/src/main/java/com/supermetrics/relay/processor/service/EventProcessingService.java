package com.supermetrics.relay.processor.service;

import com.supermetrics.relay.common.entity.DeviceEventAggregation;
import com.supermetrics.relay.processor.repository.DeviceEventAggregationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class EventProcessingService {
    private static final Logger logger = LoggerFactory.getLogger(EventProcessingService.class);
    
    private final DeviceEventAggregationRepository aggregationRepository;
    
    public EventProcessingService(DeviceEventAggregationRepository aggregationRepository) {
        this.aggregationRepository = aggregationRepository;
    }
    
    @Transactional
    public void persistAggregation(
            String deviceId,
            String zone,
            com.supermetrics.relay.common.domain.DeviceType deviceType,
            Instant windowStart,
            Instant windowEnd,
            DeviceEventAggregation.WindowType windowType,
            double avgValue,
            double minValue,
            double maxValue,
            double medianValue,
            long count) {
        try {
            aggregationRepository.upsertAggregation(
                deviceId, zone, deviceType.name(), windowStart, windowEnd, 
                windowType.name(), avgValue, minValue, maxValue, medianValue, count);
            
            logger.debug("Upserted aggregation: deviceId={}, window={}, count={}", 
                deviceId, windowType, count);
        } catch (Exception e) {
            logger.error("Error persisting aggregation: deviceId={}, windowType={}", 
                deviceId, windowType, e);
        }
    }
}

