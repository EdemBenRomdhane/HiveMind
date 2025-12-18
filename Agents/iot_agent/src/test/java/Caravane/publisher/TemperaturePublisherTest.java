package Caravane.publisher;

import Caravane.events.TemperatureEvent;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.context.ApplicationEventPublisher;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class TemperaturePublisherTest {

    @Test
    void testStartReporting() throws InterruptedException {
        ApplicationEventPublisher mockPublisher = mock(ApplicationEventPublisher.class);
        TemperaturePublisher publisher = new TemperaturePublisher(mockPublisher);

        // Use a short sleep to allow the thread to run at least once
        publisher.startReporting();

        // Wait a bit for the thread to publish at least one event
        Thread.sleep(100);

        // Verify that publishEvent was called
        verify(mockPublisher, atLeastOnce()).publishEvent(any(TemperatureEvent.class));

        // Stop the publisher
        publisher.setRunning(false);
    }
}
