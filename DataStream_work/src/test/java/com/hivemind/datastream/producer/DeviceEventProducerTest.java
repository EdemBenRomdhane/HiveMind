package com.hivemind.datastream.producer;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.Future;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DeviceEventProducerTest {

    @Mock
    private Producer<String, String> mockProducer;

    private DeviceEventProducer deviceEventProducer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        deviceEventProducer = new DeviceEventProducer("localhost:9092", mockProducer);
    }

    @Test
    void testGenerateWorkstationEvents() {
        // Mock the send method to return a mock future
        when(mockProducer.send(any(ProducerRecord.class), any())).thenReturn(mock(Future.class));

        deviceEventProducer.generateWorkstationEvents(5, 0);

        // Verify that send was called 5 times
        verify(mockProducer, times(5)).send(any(ProducerRecord.class), any());
    }

    @Test
    void testGenerateIoTEvents() {
        when(mockProducer.send(any(ProducerRecord.class), any())).thenReturn(mock(Future.class));

        deviceEventProducer.generateIoTEvents(5, 0);

        verify(mockProducer, times(5)).send(any(ProducerRecord.class), any());
    }

    @Test
    void testClose() {
        deviceEventProducer.close();
        verify(mockProducer, times(1)).close();
    }
}
