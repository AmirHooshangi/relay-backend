package com.supermetrics.relay.api.controller;

import com.supermetrics.relay.common.domain.DeviceType;
import com.supermetrics.relay.common.entity.DeviceEventAggregation;
import com.supermetrics.relay.api.repository.DeviceEventAggregationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = com.supermetrics.relay.api.RelayApiApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@org.junit.jupiter.api.Disabled("Integration tests disabled - H2 doesn't support @GeneratedValue with composite IDs. Use PostgreSQL/Testcontainers for full integration testing.")
class EventControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DeviceEventAggregationRepository repository;

    private Instant startTime;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        startTime = Instant.parse("2024-01-01T00:00:00Z");
    }

    @Test
    void getAggregationsByDevice_WithRealData_ReturnsCorrectAggregations() throws Exception {
        DeviceEventAggregation agg1 = createAggregation("device-123", DeviceType.THERMOSTAT, "zone-1",
            startTime, startTime.plusSeconds(3600), 20.0, 10.0, 30.0, 20.0, 100L);
        DeviceEventAggregation agg2 = createAggregation("device-123", DeviceType.THERMOSTAT, "zone-1",
            startTime.plusSeconds(3600), startTime.plusSeconds(7200), 25.0, 15.0, 35.0, 25.0, 100L);

        repository.save(agg1);
        repository.save(agg2);
        repository.flush();

        mockMvc.perform(get("/api/events/device/device-123")
                .param("startTime", "2024-01-01T00:00:00Z")
                .param("endTime", "2024-01-01T23:59:59Z")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.deviceId").value("device-123"))
            .andExpect(jsonPath("$.zone").value("zone-1"))
            .andExpect(jsonPath("$.deviceType").value("THERMOSTAT"))
            .andExpect(jsonPath("$.avgValue").value(22.5)) // (20*100 + 25*100) / 200
            .andExpect(jsonPath("$.minValue").value(10.0))
            .andExpect(jsonPath("$.maxValue").value(35.0))
            .andExpect(jsonPath("$.count").value(200L));
    }

    @Test
    void getAggregationsByZone_WithRealData_ReturnsCorrectAggregations() throws Exception {
        DeviceEventAggregation agg1 = createAggregation("device-1", DeviceType.THERMOSTAT, "zone-1",
            startTime, startTime.plusSeconds(3600), 15.0, 10.0, 20.0, 15.0, 50L);
        DeviceEventAggregation agg2 = createAggregation("device-2", DeviceType.HEART_RATE_METER, "zone-1",
            startTime, startTime.plusSeconds(3600), 75.0, 60.0, 90.0, 75.0, 50L);

        repository.save(agg1);
        repository.save(agg2);
        repository.flush();

        mockMvc.perform(get("/api/events/zone/zone-1")
                .param("startTime", "2024-01-01T00:00:00Z")
                .param("endTime", "2024-01-01T23:59:59Z")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.zone").value("zone-1"))
            .andExpect(jsonPath("$.avgValue").value(45.0)) // (15*50 + 75*50) / 100
            .andExpect(jsonPath("$.minValue").value(10.0))
            .andExpect(jsonPath("$.maxValue").value(90.0))
            .andExpect(jsonPath("$.count").value(100L));
    }

    @Test
    void getAggregationsByDeviceType_WithRealData_ReturnsCorrectAggregations() throws Exception {
        DeviceEventAggregation agg1 = createAggregation("device-1", DeviceType.HEART_RATE_METER, "zone-1",
            startTime, startTime.plusSeconds(3600), 70.0, 60.0, 80.0, 70.0, 100L);
        DeviceEventAggregation agg2 = createAggregation("device-2", DeviceType.HEART_RATE_METER, "zone-2",
            startTime, startTime.plusSeconds(3600), 80.0, 70.0, 90.0, 80.0, 100L);

        repository.save(agg1);
        repository.save(agg2);
        repository.flush();

        mockMvc.perform(get("/api/events/type/HEART_RATE_METER")
                .param("startTime", "2024-01-01T00:00:00Z")
                .param("endTime", "2024-01-01T23:59:59Z")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.deviceType").value("HEART_RATE_METER"))
            .andExpect(jsonPath("$.avgValue").value(75.0)) // (70*100 + 80*100) / 200
            .andExpect(jsonPath("$.minValue").value(60.0))
            .andExpect(jsonPath("$.maxValue").value(90.0))
            .andExpect(jsonPath("$.count").value(200L));
    }

    @Test
    void getAggregationsByZoneAndType_WithRealData_ReturnsCorrectAggregations() throws Exception {
        DeviceEventAggregation agg1 = createAggregation("device-1", DeviceType.CAR_FUEL, "zone-2",
            startTime, startTime.plusSeconds(3600), 40.0, 20.0, 60.0, 40.0, 75L);
        DeviceEventAggregation agg2 = createAggregation("device-2", DeviceType.CAR_FUEL, "zone-2",
            startTime, startTime.plusSeconds(3600), 50.0, 30.0, 70.0, 50.0, 75L);

        repository.save(agg1);
        repository.save(agg2);
        repository.flush();

        mockMvc.perform(get("/api/events/zone/zone-2/type/CAR_FUEL")
                .param("startTime", "2024-01-01T00:00:00Z")
                .param("endTime", "2024-01-01T23:59:59Z")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.zone").value("zone-2"))
            .andExpect(jsonPath("$.deviceType").value("CAR_FUEL"))
            .andExpect(jsonPath("$.avgValue").value(45.0)) // (40*75 + 50*75) / 150
            .andExpect(jsonPath("$.minValue").value(20.0))
            .andExpect(jsonPath("$.maxValue").value(70.0))
            .andExpect(jsonPath("$.count").value(150L));
    }

    @Test
    void getAggregationsByDevice_WithNoMatchingData_Returns404() throws Exception {
        mockMvc.perform(get("/api/events/device/non-existent-device")
                .param("startTime", "2024-01-01T00:00:00Z")
                .param("endTime", "2024-01-01T23:59:59Z")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    void getAggregationsByDevice_WithTimeRangeOutsideData_Returns404() throws Exception {
        DeviceEventAggregation agg = createAggregation("device-123", DeviceType.THERMOSTAT, "zone-1",
            startTime, startTime.plusSeconds(3600), 20.0, 10.0, 30.0, 20.0, 100L);

        repository.save(agg);
        repository.flush();

        Instant futureStart = Instant.parse("2025-01-01T00:00:00Z");
        Instant futureEnd = Instant.parse("2025-01-01T23:59:59Z");

        mockMvc.perform(get("/api/events/device/device-123")
                .param("startTime", futureStart.toString())
                .param("endTime", futureEnd.toString())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    private DeviceEventAggregation createAggregation(String deviceId, DeviceType deviceType, String zone,
                                                      Instant timeWindowStart, Instant timeWindowEnd,
                                                      Double avgValue, Double minValue, Double maxValue,
                                                      Double medianValue, Long count) {
        DeviceEventAggregation agg = new DeviceEventAggregation();
        // Don't set ID - let Hibernate generate it (will be null initially, then generated on save)
        agg.setDeviceId(deviceId);
        agg.setDeviceType(deviceType);
        agg.setZone(zone);
        agg.setTimeWindowStart(timeWindowStart);
        agg.setTimeWindowEnd(timeWindowEnd);
        agg.setWindowType(DeviceEventAggregation.WindowType.HOURLY);
        agg.setAvgValue(avgValue);
        agg.setMinValue(minValue);
        agg.setMaxValue(maxValue);
        agg.setMedianValue(medianValue);
        agg.setCount(count);
        return agg;
    }
}

