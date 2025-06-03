package org.y9nba.app.dto.file;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.y9nba.app.dto.auditlog.AuditLogDto;
import org.y9nba.app.dto.fileaccess.FileAccessDto;
import org.y9nba.app.mapper.GeneralMapper;
import org.y9nba.app.dao.entity.File;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
public class FileDto extends FilePresentDto {
    private Set<FileAccessDto> fileAccesses;
    private Set<AuditLogDto> auditLogs;

    public FileDto(File model) {
        super(model);
        this.fileAccesses = GeneralMapper.toFileAccessDto(model.getFileAccesses());
        this.auditLogs = GeneralMapper.toAuditLogDto(model.getAuditLogs());
    }
}
