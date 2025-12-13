package com.hivemind.datastream.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.flink.api.common.functions.MapFunction;

public class EventProcessor implements MapFunction<String, String> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String map(String value) throws Exception {
        try {
            JsonNode rawNode = objectMapper.readTree(value);
            ObjectNode processedEvent = objectMapper.createObjectNode();

            // Extract fields (with defaults)
            String eventType = rawNode.has("eventType") ? rawNode.get("eventType").asText() : "UNKNOWN";
            String deviceId = rawNode.has("deviceId") ? rawNode.get("deviceId").asText() : "UNKNOWN";
            String severity = rawNode.has("severity") ? rawNode.get("severity").asText() : "UNKNOWN";
            String username = rawNode.has("username") ? rawNode.get("username").asText() : "N/A";
            String filename = rawNode.has("filename") ? rawNode.get("filename").asText() : "N/A";
            String changeType = rawNode.has("changeType") ? rawNode.get("changeType").asText() : "N/A";

            // Populate processed event
            processedEvent.put("eventType", eventType);
            processedEvent.put("deviceId", deviceId);
            processedEvent.put("severity", severity);
            processedEvent.put("username", username);
            processedEvent.put("filename", filename);
            processedEvent.put("changeType", changeType);
            processedEvent.put("processedTimestamp", System.currentTimeMillis());

            // Keep original timestamp if present
            if (rawNode.has("timestamp")) {
                processedEvent.put("eventTimestamp", rawNode.get("timestamp").asLong());
            }

            // Log HIGH severity for debugging console (still useful)
            if ("HIGH".equals(severity) || "CRITICAL".equals(severity)) {
                System.out.println("⚠️ ALERT: High severity event detected! Device: " + deviceId);
            }

            return objectMapper.writeValueAsString(processedEvent);
        } catch (Exception e) {
            System.err.println("❌ Error processing event: " + e.getMessage());
            return null; // Handle nulls in filter if needed, or return error object
        }
    }
}
