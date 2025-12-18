package com.hivemind.datastream.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EventProcessorTest {

    private final EventProcessor processor = new EventProcessor();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testMap_JsonStrategy() throws Exception {
        String input = "{\"eventType\":\"TEMPERATURE_REPORT\",\"deviceId\":\"IOT-AGENT\",\"severity\":\"INFO\",\"temperature\":105.5,\"unit\":\"CELSIUS\",\"timestamp\":1734531936000}";
        String output = processor.map(input);
        JsonNode node = objectMapper.readTree(output);

        assertEquals("TEMPERATURE_REPORT", node.get("eventType").asText());
        assertEquals("IOT-AGENT", node.get("deviceId").asText());
        assertEquals(105.5, node.get("temperature").asDouble());
        assertTrue(node.has("processedTimestamp"));
    }

    @Test
    void testMap_SyslogStrategy() throws Exception {
        // Example Syslog: "ديسمبر 14 18:47:35 maya sddm-helper[4176]: [PAM]
        // authenticate: Authentication failure"
        String input = "ديسمبر 14 18:47:35 maya sddm-helper[4176]: Authentication failure";
        String output = processor.map(input);
        JsonNode node = objectMapper.readTree(output);

        assertEquals("SYS_LOG", node.get("eventType").asText());
        assertEquals("maya", node.get("deviceId").asText());
        assertEquals("sddm-helper[4176]", node.get("username").asText());
        assertEquals("Authentication failure", node.get("changeType").asText());
    }

    @Test
    void testMap_RawStrategy() throws Exception {
        String input = "random unformatted log line";
        String output = processor.map(input);
        JsonNode node = objectMapper.readTree(output);

        assertEquals("RAW_LOG", node.get("eventType").asText());
        assertEquals("UNKNOWN_HOST", node.get("deviceId").asText());
        assertEquals("random unformatted log line", node.get("changeType").asText());
    }
}
