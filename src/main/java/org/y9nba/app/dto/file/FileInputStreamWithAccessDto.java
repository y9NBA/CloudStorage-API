package org.y9nba.app.dto.file;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.y9nba.app.constant.Access;

import java.io.InputStream;

@AllArgsConstructor
@Getter
public class FileInputStreamWithAccessDto {
    private InputStream inputStream;
    private Access access;
}
