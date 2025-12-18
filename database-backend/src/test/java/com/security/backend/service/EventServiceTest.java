package com.security.backend.service;

import com.security.backend.model.SecurityEvent;
import com.security.backend.repository.SecurityEventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.junit.jupiter.api.Assertions.*;
import org.mockito.MockitoAnnotations;

class EventServiceTest {

    @Mock
    private SecurityEventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveEvent() {
        SecurityEvent event = new SecurityEvent();
        event.setEventId(UUID.randomUUID());
        event.setDeviceId("WS-001");
        event.setEventType("FILE_ACCESS");

        when(eventRepository.save(any(SecurityEvent.class))).thenReturn(event);

        SecurityEvent savedEvent = eventService.saveEvent(event);

        assertNotNull(savedEvent);
        assertEquals("WS-001", savedEvent.getDeviceId());
        verify(eventRepository, times(1)).save(event);
    }

    @Test
    void testGetAllEvents() {
        when(eventRepository.findAll()).thenReturn(Arrays.asList(new SecurityEvent(), new SecurityEvent()));

        List<SecurityEvent> result = eventService.getAllEvents();

        assertEquals(2, result.size());
        verify(eventRepository, times(1)).findAll();
    }

    @Test
    void testGetEventsByDevice() {
        when(eventRepository.findByDeviceId("IOT-1")).thenReturn(Arrays.asList(new SecurityEvent()));

        List<SecurityEvent> result = eventService.getEventsByDevice("IOT-1");

        assertEquals(1, result.size());
        verify(eventRepository, times(1)).findByDeviceId("IOT-1");
    }

    @Test
    void testDeleteEvent() {
        UUID id = UUID.randomUUID();
        doNothing().when(eventRepository).deleteById(id);

        eventService.deleteEvent(id);

        verify(eventRepository, times(1)).deleteById(id);
    }
}
