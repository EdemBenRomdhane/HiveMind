package Caravane.events;

import java.util.Objects;

public class FileChangedEvent {
    private final String filename;
    private final String chngetype;

    public FileChangedEvent(String filename, String chngetype) {
        this.filename = filename;
        this.chngetype = chngetype;
    }

    public String getFilename() {
        return filename;
    }

    public String getChngetype() {
        return chngetype;
    }

    @Override
    public String toString() {
        return "FileChangedEvent{filename='" + filename + "', chngetype='" + chngetype + "'}";
    }
}
