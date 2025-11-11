package com.supermetrics.relay.api.controller;

import com.supermetrics.relay.common.domain.DeviceType;
import com.supermetrics.relay.api.service.AggregationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.supermetrics.relay.api.TestApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = TestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AggregationService aggregationService;

    @Test
    void getAggregationsByDevice_WhenResultsExist_Returns200WithData() throws Exception {
        Instant startTime = Instant.parse("2024-01-01T00:00:00Z");
        Instant endTime = Instant.parse("2024-01-01T23:59:59Z");

        AggregationService.AggregationResult result = new AggregationService.AggregationResult(
            "device-123",
            "zone-1",
            DeviceType.THERMOSTAT,
            startTime,
            endTime,
            22.5,
            10.0,
            35.0,
            22.0,
            150L
        );

        when(aggregationService.getAggregationsByDevice(eq("device-123"), any(), any()))
            .thenReturn(result);

        mockMvc.perform(get("/api/events/device/device-123")
                .param("startTime", "2024-01-01T00:00:00Z")
                .param("endTime", "2024-01-01T23:59:59Z")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.deviceId").value("device-123"))
            .andExpect(jsonPath("$.zone").value("zone-1"))
            .andExpect(jsonPath("$.deviceType").value("THERMOSTAT"))
            .andExpect(jsonPath("$.avgValue").value(22.5))
            .andExpect(jsonPath("$.minValue").value(10.0))
            .andExpect(jsonPath("$.maxValue").value(35.0))
            .andExpect(jsonPath("$.medianValue").value(22.0))
            .andExpect(jsonPath("$.count").value(150L));
    }

    @Test
    void getAggregationsByDevice_WhenNoResults_Returns404() throws Exception {
        Instant startTime = Instant.parse("2024-01-01T00:00:00Z");
        Instant endTime = Instant.parse("2024-01-01T23:59:59Z");

        AggregationService.AggregationResult emptyResult = new AggregationService.AggregationResult(
            null, null, null, startTime, endTime, null, null, null, null, 0L
        );

        when(aggregationService.getAggregationsByDevice(eq("device-999"), any(), any()))
            .thenReturn(emptyResult);

        mockMvc.perform(get("/api/events/device/device-999")
                .param("startTime", "2024-01-01T00:00:00Z")
                .param("endTime", "2024-01-01T23:59:59Z")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    void getAggregationsByZone_WhenResultsExist_Returns200WithData() throws Exception {
        Instant startTime = Instant.parse("2024-01-01T00:00:00Z");
        Instant endTime = Instant.parse("2024-01-01T23:59:59Z");

        AggregationService.AggregationResult result = new AggregationService.AggregationResult(
            null,
            "zone-1",
            null,
            startTime,
            endTime,
            18.5,
            5.0,
            30.0,
            18.0,
            500L
        );

        when(aggregationService.getAggregationsByZone(eq("zone-1"), any(), any()))
            .thenReturn(result);

        mockMvc.perform(get("/api/events/zone/zone-1")
                .param("startTime", "2024-01-01T00:00:00Z")
                .param("endTime", "2024-01-01T23:59:59Z")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.zone").value("zone-1"))
            .andExpect(jsonPath("$.avgValue").value(18.5))
            .andExpect(jsonPath("$.count").value(500L));
    }

    @Test
    void getAggregationsByZone_WhenNoResults_Returns404() throws Exception {
        Instant startTime = Instant.parse("2024-01-01T00:00:00Z");
        Instant endTime = Instant.parse("2024-01-01T23:59:59Z");

        AggregationService.AggregationResult emptyResult = new AggregationService.AggregationResult(
            null, null, null, startTime, endTime, null, null, null, null, 0L
        );

        when(aggregationService.getAggregationsByZone(eq("zone-999"), any(), any()))
            .thenReturn(emptyResult);

        mockMvc.perform(get("/api/events/zone/zone-999")
                .param("startTime", "2024-01-01T00:00:00Z")
                .param("endTime", "2024-01-01T23:59:59Z")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    void getAggregationsByDeviceType_WhenResultsExist_Returns200WithData() throws Exception {
        Instant startTime = Instant.parse("2024-01-01T00:00:00Z");
        Instant endTime = Instant.parse("2024-01-01T23:59:59Z");

        AggregationService.AggregationResult result = new AggregationService.AggregationResult(
            null,
            null,
            DeviceType.HEART_RATE_METER,
            startTime,
            endTime,
            72.5,
            60.0,
            90.0,
            72.0,
            1000L
        );

        when(aggregationService.getAggregationsByDeviceType(eq(DeviceType.HEART_RATE_METER), any(), any()))
            .thenReturn(result);

        mockMvc.perform(get("/api/events/type/HEART_RATE_METER")
                .param("startTime", "2024-01-01T00:00:00Z")
                .param("endTime", "2024-01-01T23:59:59Z")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.deviceType").value("HEART_RATE_METER"))
            .andExpect(jsonPath("$.avgValue").value(72.5))
            .andExpect(jsonPath("$.count").value(1000L));
    }

    @Test
    void getAggregationsByDeviceType_WhenNoResults_Returns404() throws Exception {
        Instant startTime = Instant.parse("2024-01-01T00:00:00Z");
        Instant endTime = Instant.parse("2024-01-01T23:59:59Z");

        AggregationService.AggregationResult emptyResult = new AggregationService.AggregationResult(
            null, null, null, startTime, endTime, null, null, null, null, 0L
        );

        when(aggregationService.getAggregationsByDeviceType(eq(DeviceType.CAR_FUEL), any(), any()))
            .thenReturn(emptyResult);

        mockMvc.perform(get("/api/events/type/CAR_FUEL")
                .param("startTime", "2024-01-01T00:00:00Z")
                .param("endTime", "2024-01-01T23:59:59Z")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    void getAggregationsByZoneAndType_WhenResultsExist_Returns200WithData() throws Exception {
        Instant startTime = Instant.parse("2024-01-01T00:00:00Z");
        Instant endTime = Instant.parse("2024-01-01T23:59:59Z");

        AggregationService.AggregationResult result = new AggregationService.AggregationResult(
            null,
            "zone-2",
            DeviceType.CAR_FUEL,
            startTime,
            endTime,
            45.0,
            20.0,
            80.0,
            45.0,
            250L
        );

        when(aggregationService.getAggregationsByZoneAndType(eq("zone-2"), eq(DeviceType.CAR_FUEL), any(), any()))
            .thenReturn(result);

        mockMvc.perform(get("/api/events/zone/zone-2/type/CAR_FUEL")
                .param("startTime", "2024-01-01T00:00:00Z")
                .param("endTime", "2024-01-01T23:59:59Z")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.zone").value("zone-2"))
            .andExpect(jsonPath("$.deviceType").value("CAR_FUEL"))
            .andExpect(jsonPath("$.avgValue").value(45.0))
            .andExpect(jsonPath("$.count").value(250L));
    }

    @Test
    void getAggregationsByZoneAndType_WhenNoResults_Returns404() throws Exception {
        Instant startTime = Instant.parse("2024-01-01T00:00:00Z");
        Instant endTime = Instant.parse("2024-01-01T23:59:59Z");

        AggregationService.AggregationResult emptyResult = new AggregationService.AggregationResult(
            null, null, null, startTime, endTime, null, null, null, null, 0L
        );

        when(aggregationService.getAggregationsByZoneAndType(eq("zone-999"), eq(DeviceType.THERMOSTAT), any(), any()))
            .thenReturn(emptyResult);

        mockMvc.perform(get("/api/events/zone/zone-999/type/THERMOSTAT")
                .param("startTime", "2024-01-01T00:00:00Z")
                .param("endTime", "2024-01-01T23:59:59Z")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }

    @Test
    void getAggregationsByDevice_WithInvalidDateFormat_Returns400() throws Exception {
        mockMvc.perform(get("/api/events/device/device-123")
                .param("startTime", "invalid-date")
                .param("endTime", "2024-01-01T23:59:59Z")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    void getAggregationsByDevice_WithMissingParameters_Returns400() throws Exception {
        mockMvc.perform(get("/api/events/device/device-123")
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }
}
