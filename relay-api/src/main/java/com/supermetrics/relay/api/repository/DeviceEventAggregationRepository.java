package com.supermetrics.relay.api.repository;

import com.supermetrics.relay.common.domain.DeviceType;
import com.supermetrics.relay.common.entity.DeviceEventAggregation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface DeviceEventAggregationRepository extends JpaRepository<DeviceEventAggregation, DeviceEventAggregation.AggregationId> {
    
    @Query("SELECT a FROM DeviceEventAggregation a WHERE " +
           "a.deviceId = :deviceId AND a.windowType = 'HOURLY' AND " +
           "a.timeWindowStart < :endTime AND a.timeWindowEnd > :startTime " +
           "ORDER BY a.timeWindowStart ASC")
    List<DeviceEventAggregation> findByDeviceIdAndTimeRange(
        @Param("deviceId") String deviceId,
        @Param("startTime") Instant startTime,
        @Param("endTime") Instant endTime);
    
    @Query("SELECT a FROM DeviceEventAggregation a WHERE " +
           "a.zone = :zone AND a.windowType = 'HOURLY' AND " +
           "a.timeWindowStart < :endTime AND a.timeWindowEnd > :startTime " +
           "ORDER BY a.timeWindowStart ASC")
    List<DeviceEventAggregation> findByZoneAndTimeRange(
        @Param("zone") String zone,
        @Param("startTime") Instant startTime,
        @Param("endTime") Instant endTime);
    
    @Query("SELECT a FROM DeviceEventAggregation a WHERE " +
           "a.deviceType = :deviceType AND a.windowType = 'HOURLY' AND " +
           "a.timeWindowStart < :endTime AND a.timeWindowEnd > :startTime " +
           "ORDER BY a.timeWindowStart ASC")
    List<DeviceEventAggregation> findByDeviceTypeAndTimeRange(
        @Param("deviceType") DeviceType deviceType,
        @Param("startTime") Instant startTime,
        @Param("endTime") Instant endTime);
    
    @Query("SELECT a FROM DeviceEventAggregation a WHERE " +
           "a.zone = :zone AND a.deviceType = :deviceType AND a.windowType = 'HOURLY' AND " +
           "a.timeWindowStart < :endTime AND a.timeWindowEnd > :startTime " +
           "ORDER BY a.timeWindowStart ASC")
    List<DeviceEventAggregation> findByZoneAndDeviceTypeAndTimeRange(
        @Param("zone") String zone,
        @Param("deviceType") DeviceType deviceType,
        @Param("startTime") Instant startTime,
        @Param("endTime") Instant endTime);
}

