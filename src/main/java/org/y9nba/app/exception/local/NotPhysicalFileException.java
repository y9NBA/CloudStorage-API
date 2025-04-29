package org.y9nba.app.exception.local;

import java.io.IOException;

public class NotPhysicalFileException extends IOException {
    public NotPhysicalFileException(String fileName) {
        super(fileName);
    }
}
