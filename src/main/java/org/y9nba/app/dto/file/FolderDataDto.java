package org.y9nba.app.dto.file;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.y9nba.app.dao.entity.File;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class FolderDataDto {
    private final String folderURL;
    private final int fileCount;
    private final Long totalSize;
    private final LocalDateTime lastUpdated;

    public FolderDataDto(String folderURL, Set<File> files) {
        this(
                folderURL,
                files.size(),
                files
                        .stream()
                        .mapToLong(File::getFileSize)
                        .sum(),
                files
                        .stream()
                        .map(File::getUpdatedAt)
                        .max(LocalDateTime::compareTo)
                        .orElse(null)
        );
    }
}
