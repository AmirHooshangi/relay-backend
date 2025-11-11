package com.supermetrics.relay.api.service;

import com.supermetrics.relay.common.domain.DeviceType;
import com.supermetrics.relay.common.entity.DeviceEventAggregation;
import com.supermetrics.relay.api.repository.DeviceEventAggregationRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class AggregationService {
    
    private final DeviceEventAggregationRepository aggregationRepository;
    
    public AggregationService(DeviceEventAggregationRepository aggregationRepository) {
        this.aggregationRepository = aggregationRepository;
    }
    
    public AggregationResult getAggregationsByDevice(String deviceId, Instant startTime, Instant endTime) {
        List<DeviceEventAggregation> aggregations = aggregationRepository
            .findByDeviceIdAndTimeRange(deviceId, startTime, endTime);
        return aggregateResults(aggregations, startTime, endTime);
    }
    
    public AggregationResult getAggregationsByZone(String zone, Instant startTime, Instant endTime) {
        List<DeviceEventAggregation> aggregations = aggregationRepository
            .findByZoneAndTimeRange(zone, startTime, endTime);
        return aggregateResults(aggregations, startTime, endTime);
    }
    
    public AggregationResult getAggregationsByDeviceType(DeviceType deviceType, Instant startTime, Instant endTime) {
        List<DeviceEventAggregation> aggregations = aggregationRepository
            .findByDeviceTypeAndTimeRange(deviceType, startTime, endTime);
        return aggregateResults(aggregations, startTime, endTime);
    }
    
    public AggregationResult getAggregationsByZoneAndType(String zone, DeviceType deviceType, 
                                                          Instant startTime, Instant endTime) {
        List<DeviceEventAggregation> aggregations = aggregationRepository
            .findByZoneAndDeviceTypeAndTimeRange(zone, deviceType, startTime, endTime);
        return aggregateResults(aggregations, startTime, endTime);
    }
    
    private AggregationResult aggregateResults(List<DeviceEventAggregation> aggregations, 
                                               Instant startTime, Instant endTime) {
        if (aggregations.isEmpty()) {
            return new AggregationResult(
                null, null, null, startTime, endTime, null, null, null, null, 0L
            );
        }
        
        DeviceEventAggregation first = aggregations.get(0);
        final String firstDeviceId = first.getDeviceId();
        final String firstZone = first.getZone();
        DeviceType deviceType = first.getDeviceType();
        
        boolean allSameDeviceId = aggregations.stream().allMatch(a -> 
            (firstDeviceId == null && a.getDeviceId() == null) || 
            (firstDeviceId != null && firstDeviceId.equals(a.getDeviceId())));
        boolean allSameZone = aggregations.stream().allMatch(a -> 
            (firstZone == null && a.getZone() == null) || 
            (firstZone != null && firstZone.equals(a.getZone())));
        
        String deviceId = allSameDeviceId ? firstDeviceId : null;
        String zone = allSameZone ? firstZone : null;
        
        double weightedSum = 0.0;
        long totalCount = 0;
        double minValue = Double.MAX_VALUE;
        double maxValue = Double.MIN_VALUE;
        List<WeightedValue> weightedMedians = new ArrayList<>();
        
        for (DeviceEventAggregation agg : aggregations) {
            long count = agg.getCount();
            if (count > 0) {
                weightedSum += agg.getAvgValue() * count;
                totalCount += count;
                
                minValue = Math.min(minValue, agg.getMinValue());
                maxValue = Math.max(maxValue, agg.getMaxValue());
                
                if (agg.getMedianValue() != null) {
                    weightedMedians.add(new WeightedValue(agg.getMedianValue(), count));
                }
            }
        }
        
        Double avgValue = totalCount > 0 ? weightedSum / totalCount : null;
        Double minVal = minValue == Double.MAX_VALUE ? null : minValue;
        Double maxVal = maxValue == Double.MIN_VALUE ? null : maxValue;
        Double medianValue = calculateWeightedMedian(weightedMedians, totalCount);
        
        return new AggregationResult(deviceId, zone, deviceType, startTime, endTime,
            avgValue, minVal, maxVal, medianValue, totalCount);
    }
    
    private Double calculateWeightedMedian(List<WeightedValue> weightedValues, long totalCount) {
        if (weightedValues.isEmpty() || totalCount == 0) {
            return null;
        }
        
        weightedValues.sort(Comparator.comparing(w -> w.value));
        
        long targetWeight = totalCount / 2;
        long cumulativeWeight = 0;
        
        for (WeightedValue wv : weightedValues) {
            cumulativeWeight += wv.weight;
            if (cumulativeWeight >= targetWeight) {
                return wv.value;
            }
        }
        
        return weightedValues.get(weightedValues.size() - 1).value;
    }
    
    private static class WeightedValue {
        final double value;
        final long weight;
        
        WeightedValue(double value, long weight) {
            this.value = value;
            this.weight = weight;
        }
    }
    
    public record AggregationResult(
        String deviceId,
        String zone,
        DeviceType deviceType,
        Instant timeWindowStart,
        Instant timeWindowEnd,
        Double avgValue,
        Double minValue,
        Double maxValue,
        Double medianValue,
        Long count
    ) {}
}

