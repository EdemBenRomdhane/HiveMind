package Caravane.subscriber;

import Caravane.events.TemperatureEvent;
import Caravane.service.KafkaProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KafkaForwarderServiceTest {

    @Mock
    private KafkaProducer kafkaProducer;

    @InjectMocks
    private KafkaForwarderService forwarderService;

    @Test
    void testHandleTemperatureReport() {
        TemperatureEvent event = new TemperatureEvent(25.5, "CELSIUS", System.currentTimeMillis());
        forwarderService.handleTemperatureReport(event);

        verify(kafkaProducer).send(eq("device-events-iot"), anyString());
    }
}
