package com.supermetrics.relay.api.controller;

import com.supermetrics.relay.common.domain.DeviceType;
import com.supermetrics.relay.api.service.AggregationService;
import com.supermetrics.relay.api.service.AggregationService.AggregationResult;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/events")
public class EventController {
    
    private final AggregationService aggregationService;
    
    public EventController(AggregationService aggregationService) {
        this.aggregationService = aggregationService;
    }
    
    @GetMapping("/device/{deviceId}")
    public ResponseEntity<AggregationResponse> getAggregationsByDevice(
            @PathVariable String deviceId,
            @RequestParam("startTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startTime,
            @RequestParam("endTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endTime) {
        
        AggregationResult result = aggregationService.getAggregationsByDevice(deviceId, startTime, endTime);
        if (result.count() == 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toResponse(result));
    }
    
    @GetMapping("/zone/{zone}")
    public ResponseEntity<AggregationResponse> getAggregationsByZone(
            @PathVariable String zone,
            @RequestParam("startTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startTime,
            @RequestParam("endTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endTime) {
        
        AggregationResult result = aggregationService.getAggregationsByZone(zone, startTime, endTime);
        if (result.count() == 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toResponse(result));
    }
    
    @GetMapping("/type/{deviceType}")
    public ResponseEntity<AggregationResponse> getAggregationsByDeviceType(
            @PathVariable DeviceType deviceType,
            @RequestParam("startTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startTime,
            @RequestParam("endTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endTime) {
        
        AggregationResult result = aggregationService.getAggregationsByDeviceType(deviceType, startTime, endTime);
        if (result.count() == 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toResponse(result));
    }
    
    @GetMapping("/zone/{zone}/type/{deviceType}")
    public ResponseEntity<AggregationResponse> getAggregationsByZoneAndType(
            @PathVariable String zone,
            @PathVariable DeviceType deviceType,
            @RequestParam("startTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startTime,
            @RequestParam("endTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endTime) {
        
        AggregationResult result = aggregationService.getAggregationsByZoneAndType(zone, deviceType, startTime, endTime);
        if (result.count() == 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(toResponse(result));
    }
    
    private AggregationResponse toResponse(AggregationResult result) {
        return new AggregationResponse(
            result.deviceId(),
            result.zone(),
            result.deviceType(),
            result.timeWindowStart(),
            result.timeWindowEnd(),
            result.avgValue(),
            result.minValue(),
            result.maxValue(),
            result.medianValue(),
            result.count()
        );
    }
    
    public record AggregationResponse(
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

