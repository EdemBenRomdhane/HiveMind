package com.security.backend.service;

import com.security.backend.model.SecurityEvent;
import com.security.backend.repository.SecurityEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class EventService {

    @Autowired
    private SecurityEventRepository eventRepository;

    public SecurityEvent saveEvent(SecurityEvent event) {
        return eventRepository.save(event);
    }

    public List<SecurityEvent> getAllEvents() {
        return eventRepository.findAll();
    }

    public Optional<SecurityEvent> getEventById(UUID id) {
        return eventRepository.findById(id);
    }

    public List<SecurityEvent> getEventsByDevice(String deviceId) {
        return eventRepository.findByDeviceId(deviceId);
    }

    public void deleteEvent(UUID id) {
        eventRepository.deleteById(id);
    }
}
