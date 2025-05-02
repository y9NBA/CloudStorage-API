package org.y9nba.app.exception.local;

import lombok.Getter;
import org.y9nba.app.dto.file.FileCreateDto;

import java.io.IOException;

@Getter
public class PhysicalFileOnNewUrlAlreadyException extends IOException {
    private FileCreateDto fileCreateDto;

    public PhysicalFileOnNewUrlAlreadyException(String message) {
        super(message);
    }

    public PhysicalFileOnNewUrlAlreadyException(String message, FileCreateDto fileCreateDto) {
        super(message);
        this.fileCreateDto = fileCreateDto;
    }
}
