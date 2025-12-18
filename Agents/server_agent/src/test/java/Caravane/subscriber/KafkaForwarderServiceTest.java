package Caravane.subscriber;

import Caravane.events.FileChangedEvent;
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
    private KafkaProducer kp;

    @InjectMocks
    private KafkaForwarderService forwarderService;

    @Test
    void testHandleFileChanged() {
        FileChangedEvent event = new FileChangedEvent("server_log.txt", "MODIFIED");
        forwarderService.handlefilechanged(event);

        verify(kp).send(eq("device-events-server"), anyString());
    }
}
