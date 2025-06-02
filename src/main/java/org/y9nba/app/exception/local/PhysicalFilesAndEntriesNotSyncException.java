package org.y9nba.app.exception.local;

import lombok.Getter;
import org.y9nba.app.dto.file.FileCreateDto;
import org.y9nba.app.dao.entity.File;

import java.io.IOException;
import java.util.Set;

@Getter
public class PhysicalFilesAndEntriesNotSyncException extends IOException {
    private final Set<File> fileModelsWithoutPhysicalFile;
    private final Set<FileCreateDto> filesWithoutEntryInDB;

    public PhysicalFilesAndEntriesNotSyncException(Set<File> fileModelsWithoutPhysicalFile, Set<FileCreateDto> filesWithoutEntryInDB) {
        super("Physical files and entries are not synchronized");
        this.fileModelsWithoutPhysicalFile = fileModelsWithoutPhysicalFile;
        this.filesWithoutEntryInDB = filesWithoutEntryInDB;
    }
}
