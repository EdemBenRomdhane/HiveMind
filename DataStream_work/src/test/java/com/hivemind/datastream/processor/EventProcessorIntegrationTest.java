package com.hivemind.datastream.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EventProcessorIntegrationTest {

    private final EventProcessor processor = new EventProcessor();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testProcessFullCycle_Workstation() throws Exception {
        String input = "{\"eventType\":\"FILE_ACCESS\", \"deviceId\":\"WS-01\", \"severity\":\"LOW\", \"username\":\"alice\", \"filename\":\"secret.txt\", \"changeType\":\"READ\"}";
        String output = processor.map(input);

        JsonNode node = objectMapper.readTree(output);
        assertEquals("FILE_ACCESS", node.get("eventType").asText());
        assertEquals("WS-01", node.get("deviceId").asText());
        assertEquals("alice", node.get("username").asText());
        assertTrue(node.has("processedTimestamp"));
    }

    @Test
    void testProcessFullCycle_Syslog() throws Exception {
        String input = "Dec 18 10:00:00 myhost myprocess[123]: critical error occurred";
        String output = processor.map(input);

        JsonNode node = objectMapper.readTree(output);
        assertEquals("SYS_LOG", node.get("eventType").asText());
        assertEquals("myhost", node.get("deviceId").asText());
        assertEquals("myprocess[123]", node.get("username").asText());
        assertEquals("critical error occurred", node.get("changeType").asText());
    }

    @Test
    void testProcessFullCycle_IoT() throws Exception {
        String input = "{\"eventType\":\"TEMPERATURE_REPORT\", \"deviceId\":\"IOT-01\", \"temperature\":28.5, \"unit\":\"CELSIUS\"}";
        String output = processor.map(input);

        JsonNode node = objectMapper.readTree(output);
        assertEquals("TEMPERATURE_REPORT", node.get("eventType").asText());
        assertEquals(28.5, node.get("temperature").asDouble());
    }
}
