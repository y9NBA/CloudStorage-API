package org.y9nba.app.exception.local;

import lombok.Getter;
import org.y9nba.app.dto.file.FileCreateDto;
import org.y9nba.app.model.FileModel;

import java.io.IOException;
import java.util.Set;

@Getter
public class PhysicalFilesAndEntriesNotSyncException extends IOException {
    private final Set<FileModel> fileModelsWithoutPhysicalFile;
    private final Set<FileCreateDto> filesWithoutEntryInDB;

    public PhysicalFilesAndEntriesNotSyncException(Set<FileModel> fileModelsWithoutPhysicalFile, Set<FileCreateDto> filesWithoutEntryInDB) {
        super("Physical files and entries are not synchronized");
        this.fileModelsWithoutPhysicalFile = fileModelsWithoutPhysicalFile;
        this.filesWithoutEntryInDB = filesWithoutEntryInDB;
    }
}
