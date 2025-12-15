package com.hivemind.datastream.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.common.functions.FilterFunction;

import java.util.regex.Pattern;

/**
 * Filters log events to keep only potentially anomalous/dangerous ones.
 * Discards harmless, routine, informational logs.
 */
public class LogFilter implements FilterFunction<String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Patterns that indicate SUSPICIOUS/DANGEROUS activity - KEEP these
    private static final Pattern[] SUSPICIOUS_PATTERNS = {
            // Authentication/Authorization failures
            Pattern.compile(
                    "(?i)(authentication\\s*fail|login\\s*fail|password\\s*fail|access\\s*denied|unauthorized|invalid\\s*(user|password|credential))"),

            // Attack patterns
            Pattern.compile(
                    "(?i)(brute\\s*force|port\\s*scan|sql\\s*inject|xss|overflow|exploit|attack|intrusion|penetration)"),

            // Malware indicators
            Pattern.compile("(?i)(malware|virus|trojan|ransomware|backdoor|rootkit|keylogger|spyware|botnet)"),

            // Privilege escalation
            Pattern.compile("(?i)(privilege\\s*escalat|root\\s*access|sudo|su\\s*-|chmod\\s*777|setuid)"),

            // Network threats
            Pattern.compile(
                    "(?i)(ddos|syn\\s*flood|suspicious\\s*connection|blocked|firewall\\s*deny|connection\\s*refused)"),

            // Security events
            Pattern.compile("(?i)(security\\s*alert|threat\\s*detected|anomaly|violation|breach|compromise)"),

            // Error indicators
            Pattern.compile("(?i)(critical|fatal|emergency|panic|segfault|core\\s*dump)")
    };

    // Patterns that indicate HARMLESS activity - DISCARD these (unless severity is
    // high)
    private static final Pattern[] HARMLESS_PATTERNS = {
            Pattern.compile("(?i)(health\\s*check|heartbeat|ping|alive|status\\s*ok|connection\\s*alive)"),
            Pattern.compile("(?i)(backup\\s*complet|scheduled\\s*task|cron\\s*job\\s*complet)"),
            Pattern.compile("(?i)(started\\s*successfully|service\\s*started|boot\\s*complet)"),
            Pattern.compile("(?i)(http.*200|request\\s*successful|operation\\s*complet)")
    };

    // Severity levels to always keep
    private static final String[] HIGH_SEVERITY = { "CRITICAL", "ERROR", "WARN", "WARNING", "HIGH", "FATAL",
            "EMERGENCY" };

    @Override
    public boolean filter(String value) throws Exception {
        if (value == null || value.isEmpty()) {
            return false; // Discard null/empty
        }

        try {
            JsonNode event = objectMapper.readTree(value);

            // Extract fields
            String severity = getFieldValue(event, "severity");
            String logLine = getFieldValue(event, "logLine");
            String eventType = getFieldValue(event, "eventType");

            // Rule 1: Always keep high severity events
            if (isHighSeverity(severity)) {
                System.out.println("🚨 KEPT [High Severity]: " + severity);
                return true;
            }

            // Rule 2: Check for suspicious patterns in log content
            String contentToCheck = logLine.isEmpty() ? value : logLine;
            if (containsSuspiciousPattern(contentToCheck)) {
                System.out.println("⚠️ KEPT [Suspicious Pattern]: " + truncate(contentToCheck, 60));
                return true;
            }

            // Rule 3: Discard if matches harmless patterns
            if (containsHarmlessPattern(contentToCheck)) {
                System.out.println("✅ DISCARDED [Harmless]: " + truncate(contentToCheck, 60));
                return false;
            }

            // Rule 4: For INFO severity with no suspicious content, discard
            if ("INFO".equalsIgnoreCase(severity)) {
                System.out.println("ℹ️ DISCARDED [INFO level]: " + truncate(contentToCheck, 60));
                return false;
            }

            // Rule 5: Keep anything else (unknown patterns need human review)
            System.out.println("❓ KEPT [Unknown - needs review]: " + truncate(contentToCheck, 60));
            return true;

        } catch (Exception e) {
            // If we can't parse, keep it for manual review
            System.err.println("⚠️ Parse error, keeping event: " + e.getMessage());
            return true;
        }
    }

    private String getFieldValue(JsonNode node, String field) {
        return node.has(field) ? node.get(field).asText() : "";
    }

    private boolean isHighSeverity(String severity) {
        if (severity == null || severity.isEmpty()) {
            return false;
        }
        for (String high : HIGH_SEVERITY) {
            if (high.equalsIgnoreCase(severity)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsSuspiciousPattern(String content) {
        if (content == null || content.isEmpty()) {
            return false;
        }
        for (Pattern pattern : SUSPICIOUS_PATTERNS) {
            if (pattern.matcher(content).find()) {
                return true;
            }
        }
        return false;
    }

    private boolean containsHarmlessPattern(String content) {
        if (content == null || content.isEmpty()) {
            return false;
        }
        for (Pattern pattern : HARMLESS_PATTERNS) {
            if (pattern.matcher(content).find()) {
                return true;
            }
        }
        return false;
    }

    private String truncate(String text, int maxLength) {
        if (text == null)
            return "";
        return text.length() > maxLength ? text.substring(0, maxLength) + "..." : text;
    }
}
