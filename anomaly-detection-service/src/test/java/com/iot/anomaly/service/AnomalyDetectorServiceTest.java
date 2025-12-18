package com.iot.anomaly.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.anomaly.model.DeviceLog;
import com.iot.anomaly.model.DeviceStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AnomalyDetectorServiceTest {

    private AnomalyDetectorService detectorService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        detectorService = new AnomalyDetectorService();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testIsAnomalyWithDeviceLog_Valid() {
        DeviceLog log = new DeviceLog(java.util.UUID.randomUUID(), DeviceStatus.ONLINE, 25.0, java.time.Instant.now());
        assertFalse(detectorService.isAnomaly(log));
    }

    @Test
    void testIsAnomalyWithDeviceLog_HighTemp() {
        DeviceLog log = new DeviceLog(java.util.UUID.randomUUID(), DeviceStatus.ONLINE, 105.0, java.time.Instant.now());
        assertTrue(detectorService.isAnomaly(log));
        assertEquals("Temperature too high: 105.0", detectorService.getAnomalyDescription(log));
    }

    @Test
    void testIsAnomalyWithDeviceLog_LowTemp() {
        DeviceLog log = new DeviceLog(java.util.UUID.randomUUID(), DeviceStatus.ONLINE, -25.0, java.time.Instant.now());
        assertTrue(detectorService.isAnomaly(log));
        assertEquals("Temperature too low: -25.0", detectorService.getAnomalyDescription(log));
    }

    @Test
    void testIsAnomalyWithDeviceLog_OfflineStatus() {
        DeviceLog log = new DeviceLog(java.util.UUID.randomUUID(), DeviceStatus.OFFLINE, 25.0, java.time.Instant.now());
        assertTrue(detectorService.isAnomaly(log));
        assertEquals("Device is offline but sending data !!", detectorService.getAnomalyDescription(log));
    }

    @Test
    void testIsAnomalyWithJsonNode_WorkstationThreat() throws Exception {
        String json = "{\"deviceId\":\"WS-001\", \"changeType\":\"AUTHENTICATION FAILURE\", \"severity\":\"INFO\"}";
        JsonNode node = objectMapper.readTree(json);
        assertTrue(detectorService.isAnomaly(node));
        assertEquals("Threat Detected: AUTHENTICATION FAILURE", detectorService.getAnomalyDescription(node));
    }

    @Test
    void testIsAnomalyWithJsonNode_Normal() throws Exception {
        String json = "{\"deviceId\":\"WS-001\", \"changeType\":\"CREATED\", \"severity\":\"INFO\"}";
        JsonNode node = objectMapper.readTree(json);
        assertFalse(detectorService.isAnomaly(node));
    }

    @Test
    void testIsAnomalyWithJsonNode_HighSeverity() throws Exception {
        String json = "{\"deviceId\":\"WS-001\", \"changeType\":\"MODIFIED\", \"severity\":\"HIGH\"}";
        JsonNode node = objectMapper.readTree(json);
        assertTrue(detectorService.isAnomaly(node));
    }
}
