package com.security.backend.service;

import com.security.backend.model.SecurityEvent;
import com.security.backend.model.Alert;
import com.security.backend.repository.AlertRepository;
import com.security.backend.repository.SecurityEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityEventService {

    private final SecurityEventRepository repository;
    private final AlertRepository alertRepository;

    public SecurityEvent processEvent(SecurityEvent event) {
        if ("HIGH".equalsIgnoreCase(event.getSeverity()) || "CRITICAL".equalsIgnoreCase(event.getSeverity())) {
            Alert alert = new Alert();
            alert.setType("SECURITY_ALERT");
            alert.setMessage("High severity event detected: " + event.getEventType());
            alert.setSeverity(event.getSeverity());
            alert.setStatus("NEW");
            alertRepository.save(alert);
        }
        return repository.save(event);
    }
}
