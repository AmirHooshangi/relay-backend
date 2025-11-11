package com.supermetrics.relay.common.entity;

import com.supermetrics.relay.common.domain.DeviceType;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "device_event_aggregations", indexes = {
    @Index(name = "idx_agg_time_window", columnList = "time_window_start, time_window_end"),
    @Index(name = "idx_agg_zone_type", columnList = "zone, device_type"),
    @Index(name = "idx_agg_device_id", columnList = "device_id")
})
@IdClass(DeviceEventAggregation.AggregationId.class)
public class DeviceEventAggregation {
    
    public static class AggregationId implements java.io.Serializable {
        private Long id;
        private Instant timeWindowStart;
        
        public AggregationId() {}
        
        public AggregationId(Long id, Instant timeWindowStart) {
            this.id = id;
            this.timeWindowStart = timeWindowStart;
        }
        
        public Long getId() {
            return id;
        }
        
        public void setId(Long id) {
            this.id = id;
        }
        
        public Instant getTimeWindowStart() {
            return timeWindowStart;
        }
        
        public void setTimeWindowStart(Instant timeWindowStart) {
            this.timeWindowStart = timeWindowStart;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AggregationId that = (AggregationId) o;
            return java.util.Objects.equals(id, that.id) &&
                   java.util.Objects.equals(timeWindowStart, that.timeWindowStart);
        }
        
        @Override
        public int hashCode() {
            return java.util.Objects.hash(id, timeWindowStart);
        }
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "device_id", length = 255)
    private String deviceId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "device_type", length = 50)
    private DeviceType deviceType;
    
    @Column(name = "zone", length = 100)
    private String zone;
    
    @Id
    @Column(name = "time_window_start", nullable = false)
    private Instant timeWindowStart;
    
    @Column(name = "time_window_end", nullable = false)
    private Instant timeWindowEnd;
    
    @Column(name = "window_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private WindowType windowType;
    
    @Column(name = "avg_value", nullable = false)
    private Double avgValue;
    
    @Column(name = "min_value", nullable = false)
    private Double minValue;
    
    @Column(name = "max_value", nullable = false)
    private Double maxValue;
    
    @Column(name = "median_value")
    private Double medianValue;
    
    @Column(name = "count", nullable = false)
    private Long count;
    
    public enum WindowType {
        HOURLY
    }
    
    public DeviceEventAggregation() {
    }
    
    public DeviceEventAggregation(String deviceId, DeviceType deviceType, String zone,
                                  Instant timeWindowStart, Instant timeWindowEnd,
                                  WindowType windowType, Double avgValue,
                                  Double minValue, Double maxValue, Double medianValue, Long count) {
        this.deviceId = deviceId;
        this.deviceType = deviceType;
        this.zone = zone;
        this.timeWindowStart = timeWindowStart;
        this.timeWindowEnd = timeWindowEnd;
        this.windowType = windowType;
        this.avgValue = avgValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.medianValue = medianValue;
        this.count = count;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    
    public DeviceType getDeviceType() {
        return deviceType;
    }
    
    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }
    
    public String getZone() {
        return zone;
    }
    
    public void setZone(String zone) {
        this.zone = zone;
    }
    
    public Instant getTimeWindowStart() {
        return timeWindowStart;
    }
    
    public void setTimeWindowStart(Instant timeWindowStart) {
        this.timeWindowStart = timeWindowStart;
    }
    
    public Instant getTimeWindowEnd() {
        return timeWindowEnd;
    }
    
    public void setTimeWindowEnd(Instant timeWindowEnd) {
        this.timeWindowEnd = timeWindowEnd;
    }
    
    public WindowType getWindowType() {
        return windowType;
    }
    
    public void setWindowType(WindowType windowType) {
        this.windowType = windowType;
    }
    
    public Double getAvgValue() {
        return avgValue;
    }
    
    public void setAvgValue(Double avgValue) {
        this.avgValue = avgValue;
    }
    
    public Double getMinValue() {
        return minValue;
    }
    
    public void setMinValue(Double minValue) {
        this.minValue = minValue;
    }
    
    public Double getMaxValue() {
        return maxValue;
    }
    
    public void setMaxValue(Double maxValue) {
        this.maxValue = maxValue;
    }
    
    public Long getCount() {
        return count;
    }
    
    public void setCount(Long count) {
        this.count = count;
    }
    
    public Double getMedianValue() {
        return medianValue;
    }
    
    public void setMedianValue(Double medianValue) {
        this.medianValue = medianValue;
    }
}

