package com.supermetrics.relay.processor.repository;

import com.supermetrics.relay.common.entity.DeviceEventAggregation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface DeviceEventAggregationRepository extends JpaRepository<DeviceEventAggregation, DeviceEventAggregation.AggregationId> {
    
    @Modifying
    @Query(value = """
        INSERT INTO device_event_aggregations 
            (device_id, zone, device_type, time_window_start, time_window_end, window_type, 
             avg_value, min_value, max_value, median_value, count)
        VALUES 
            (:deviceId, :zone, CAST(:deviceType AS text), :windowStart, :windowEnd, CAST(:windowType AS text),
             :avgValue, :minValue, :maxValue, :medianValue, :count)
        ON CONFLICT (device_id, zone, device_type, time_window_start, time_window_end, window_type)
        DO UPDATE SET
            avg_value = EXCLUDED.avg_value,
            min_value = EXCLUDED.min_value,
            max_value = EXCLUDED.max_value,
            median_value = EXCLUDED.median_value,
            count = EXCLUDED.count
        """, nativeQuery = true)
    void upsertAggregation(
        @Param("deviceId") String deviceId,
        @Param("zone") String zone,
        @Param("deviceType") String deviceType,
        @Param("windowStart") Instant windowStart,
        @Param("windowEnd") Instant windowEnd,
        @Param("windowType") String windowType,
        @Param("avgValue") double avgValue,
        @Param("minValue") double minValue,
        @Param("maxValue") double maxValue,
        @Param("medianValue") double medianValue,
        @Param("count") long count);
}

