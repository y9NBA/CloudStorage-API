package org.y9nba.app.dto.file;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class FolderDataDto {
    private final String folderURL;
    private final int fileCount;
    private final Long totalSize;
    private final LocalDateTime lastUpdated;
}
