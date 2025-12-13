package Caravane.publisher;

import Caravane.events.LogEntryEvent;
import org.apache.commons.io.input.Tailer;
import org.apache.commons.io.input.TailerListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

/**
 * Monitors system log files and publishes LogEntryEvent for each new log line
 */
@Component
public class LogTailerPublisher {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Value("${log.files:/var/log/syslog,/var/log/auth.log}")
    private String logFilesConfig;

    private ExecutorService executorService;

    // Patterns for severity detection
    private static final Pattern ERROR_PATTERN = Pattern.compile("(?i)(error|failed|failure|fatal)");
    private static final Pattern WARN_PATTERN = Pattern.compile("(?i)(warn|warning)");
    private static final Pattern CRITICAL_PATTERN = Pattern.compile("(?i)(critical|crit|panic|emerg)");

    @PostConstruct
    public void init() {
        String[] logFiles = logFilesConfig.split(",");
        executorService = Executors.newFixedThreadPool(logFiles.length);

        for (String logFilePath : logFiles) {
            File logFile = new File(logFilePath.trim());
            if (logFile.exists() && logFile.canRead()) {
                startTailing(logFile);
            } else {
                System.err.println("⚠️  Cannot read log file: " + logFilePath);
            }
        }
    }

    private void startTailing(File logFile) {
        executorService.submit(() -> {
            try {
                Tailer.builder()
                        .setFile(logFile)
                        .setTailerListener(new TailerListenerAdapter() {
                            @Override
                            public void handle(String line) {
                                String severity = detectSeverity(line);
                                LogEntryEvent event = new LogEntryEvent(
                                        LogTailerPublisher.this,
                                        logFile.getAbsolutePath(),
                                        line,
                                        severity);
                                eventPublisher.publishEvent(event);
                            }
                        })
                        .setDelayDuration(java.time.Duration.ofSeconds(1))
                        .setTailFromEnd(true)
                        .get()
                        .run();
            } catch (Exception e) {
                System.err.println("❌ Error tailing log file " + logFile.getAbsolutePath() + ": " + e.getMessage());
            }
        });
        System.out.println("📋 Started monitoring: " + logFile.getAbsolutePath());
    }

    private String detectSeverity(String logLine) {
        if (CRITICAL_PATTERN.matcher(logLine).find()) {
            return "CRITICAL";
        } else if (ERROR_PATTERN.matcher(logLine).find()) {
            return "ERROR";
        } else if (WARN_PATTERN.matcher(logLine).find()) {
            return "WARN";
        } else {
            return "INFO";
        }
    }
}
