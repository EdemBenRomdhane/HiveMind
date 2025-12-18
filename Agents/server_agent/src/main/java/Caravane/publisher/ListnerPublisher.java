package Caravane.publisher;

import Caravane.events.FileChangedEvent;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.nio.file.*;

@Component
public class ListnerPublisher {

    @Autowired
    private ApplicationEventPublisher ap;

    @Value("${watch.path}")
    private Path pathToWatch;

    @PostConstruct
    public void init() {
        startwatching();
    }

    public void startwatching() {
        new Thread(() -> {
            try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
                pathToWatch.register(watchService,
                        StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_DELETE,
                        StandardWatchEventKinds.ENTRY_MODIFY);

                while (true) {
                    WatchKey key = watchService.take();
                    for (WatchEvent<?> event : key.pollEvents()) {
                        String filename = event.context().toString();
                        String typechange;
                        if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE)
                            typechange = "Created";
                        else if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE)
                            typechange = "Deleted";
                        else if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY)
                            typechange = "Modified";
                        else
                            typechange = "Unknown";

                        ap.publishEvent(new FileChangedEvent(filename, typechange));
                    }
                    key.reset();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
