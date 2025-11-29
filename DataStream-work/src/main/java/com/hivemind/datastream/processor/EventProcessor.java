package com.hivemind.datastream.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.common.functions.MapFunction;

public class EventProcessor implements MapFunction<String, String> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String map(String value) throws Exception {
        try {
            JsonNode node = objectMapper.readTree(value);
            String eventType = node.has("eventType") ? node.get("eventType").asText() : "UNKNOWN";
            String deviceId = node.has("deviceId") ? node.get("deviceId").asText() : "UNKNOWN";
            String severity = node.has("severity") ? node.get("severity").asText() : "UNKNOWN";

            // Simple processing: Log high severity events
            if ("HIGH".equals(severity) || "CRITICAL".equals(severity)) {
                return String.format("⚠️ ALERT: High severity event detected! [Type: %s, Device: %s, Severity: %s]",
                        eventType, deviceId, severity);
            }

            return String.format("ℹ️ Processed event: [Type: %s, Device: %s]", eventType, deviceId);
        } catch (Exception e) {
            return "❌ Error processing event: " + e.getMessage();
        }
    }
}
