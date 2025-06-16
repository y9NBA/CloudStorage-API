package org.y9nba.app.exception.local;

import lombok.Getter;
import org.y9nba.app.dto.file.FileCreateDto;

import java.io.IOException;

@Getter
public class PhysicalFileOnUrlAlreadyException extends IOException {
    private final FileCreateDto fileCreateDto;

    public PhysicalFileOnUrlAlreadyException(String message, FileCreateDto fileCreateDto) {
        super(message);
        this.fileCreateDto = fileCreateDto;
    }
}
