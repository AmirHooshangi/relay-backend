package com.supermetrics.relay.processor.service;

import com.supermetrics.relay.common.domain.DeviceType;
import com.supermetrics.relay.common.entity.DeviceEventAggregation;
import com.supermetrics.relay.processor.repository.DeviceEventAggregationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventProcessingServiceTest {

    @Mock
    private DeviceEventAggregationRepository aggregationRepository;

    @InjectMocks
    private EventProcessingService eventProcessingService;

    private Instant windowStart;
    private Instant windowEnd;

    @BeforeEach
    void setUp() {
        windowStart = Instant.parse("2024-01-01T00:00:00Z");
        windowEnd = Instant.parse("2024-01-01T01:00:00Z");
    }

    @Test
    void persistAggregation() {
        eventProcessingService.persistAggregation(
            "device-123", "zone-1", DeviceType.THERMOSTAT, windowStart, windowEnd,
            DeviceEventAggregation.WindowType.HOURLY,
            25.5, 20.0, 30.0, 25.0, 100L
        );

        verify(aggregationRepository, times(1)).upsertAggregation(
            eq("device-123"),
            eq("zone-1"),
            eq(DeviceType.THERMOSTAT.name()),
            eq(windowStart),
            eq(windowEnd),
            eq(DeviceEventAggregation.WindowType.HOURLY.name()),
            eq(25.5),
            eq(20.0),
            eq(30.0),
            eq(25.0),
            eq(100L)
        );
    }

    @Test
    void handlesRepositoryErrors() {
        doThrow(new RuntimeException("Database error"))
            .when(aggregationRepository).upsertAggregation(
                anyString(), anyString(), anyString(), any(), any(), anyString(),
                anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyLong()
            );

        eventProcessingService.persistAggregation(
            "device-123", "zone-1", DeviceType.THERMOSTAT, windowStart, windowEnd,
            DeviceEventAggregation.WindowType.HOURLY,
            10.0, 5.0, 15.0, 10.0, 50L
        );

        verify(aggregationRepository, times(1)).upsertAggregation(
            anyString(), anyString(), anyString(), any(), any(), anyString(),
            anyDouble(), anyDouble(), anyDouble(), anyDouble(), anyLong()
        );
    }
}
