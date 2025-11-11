package com.supermetrics.relay.api.service;

import com.supermetrics.relay.common.domain.DeviceType;
import com.supermetrics.relay.common.entity.DeviceEventAggregation;
import com.supermetrics.relay.api.repository.DeviceEventAggregationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AggregationServiceTest {

    @Mock
    private DeviceEventAggregationRepository aggregationRepository;

    @InjectMocks
    private AggregationService aggregationService;

    private Instant startTime;
    private Instant endTime;

    @BeforeEach
    void setUp() {
        startTime = Instant.parse("2024-01-01T00:00:00Z");
        endTime = Instant.parse("2024-01-01T23:59:59Z");
    }

    @Test
    void getAggregationsByDevice() {
        DeviceEventAggregation agg = createAggregation("device-123", DeviceType.THERMOSTAT, "zone-1",
            10.0, 5.0, 15.0, 10.0, 100L);

        when(aggregationRepository.findByDeviceIdAndTimeRange(eq("device-123"), any(), any()))
            .thenReturn(List.of(agg));

        AggregationService.AggregationResult result = aggregationService
            .getAggregationsByDevice("device-123", startTime, endTime);

        assertThat(result.deviceId()).isEqualTo("device-123");
        assertThat(result.avgValue()).isEqualTo(10.0);
        assertThat(result.count()).isEqualTo(100L);
    }

    @Test
    void getAggregationsByZone() {
        DeviceEventAggregation agg = createAggregation("device-123", DeviceType.THERMOSTAT, "zone-1",
            10.0, 5.0, 15.0, 10.0, 100L);

        when(aggregationRepository.findByZoneAndTimeRange(eq("zone-1"), any(), any()))
            .thenReturn(List.of(agg));

        AggregationService.AggregationResult result = aggregationService
            .getAggregationsByZone("zone-1", startTime, endTime);

        assertThat(result.zone()).isEqualTo("zone-1");
        assertThat(result.avgValue()).isEqualTo(10.0);
    }

    @Test
    void calculatesWeightedAverage() {
        DeviceEventAggregation agg1 = createAggregation("device-123", DeviceType.THERMOSTAT, "zone-1",
            10.0, 5.0, 15.0, 10.0, 50L);
        DeviceEventAggregation agg2 = createAggregation("device-123", DeviceType.THERMOSTAT, "zone-1",
            20.0, 15.0, 25.0, 20.0, 50L);

        when(aggregationRepository.findByDeviceIdAndTimeRange(eq("device-123"), any(), any()))
            .thenReturn(List.of(agg1, agg2));

        AggregationService.AggregationResult result = aggregationService
            .getAggregationsByDevice("device-123", startTime, endTime);

        assertThat(result.avgValue()).isEqualTo(15.0);
        assertThat(result.count()).isEqualTo(100L);
    }

    private DeviceEventAggregation createAggregation(String deviceId, DeviceType deviceType, String zone,
                                                      Double avgValue, Double minValue, Double maxValue,
                                                      Double medianValue, Long count) {
        DeviceEventAggregation agg = new DeviceEventAggregation();
        agg.setDeviceId(deviceId);
        agg.setDeviceType(deviceType);
        agg.setZone(zone);
        agg.setTimeWindowStart(startTime);
        agg.setTimeWindowEnd(startTime.plusSeconds(3600));
        agg.setWindowType(DeviceEventAggregation.WindowType.HOURLY);
        agg.setAvgValue(avgValue);
        agg.setMinValue(minValue);
        agg.setMaxValue(maxValue);
        agg.setMedianValue(medianValue);
        agg.setCount(count);
        return agg;
    }
}
