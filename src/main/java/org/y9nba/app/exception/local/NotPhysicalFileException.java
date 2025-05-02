package org.y9nba.app.exception.local;

import lombok.Getter;

import java.io.IOException;

@Getter
public class NotPhysicalFileException extends IOException {

    public NotPhysicalFileException(String fileName) {
        super(fileName);
    }
}
