package org.y9nba.app.service.impl;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.y9nba.app.constant.Action;
import org.y9nba.app.dto.auditlog.AuditLogCreateDto;
import org.y9nba.app.dto.auditlog.AuditLogDto;
import org.y9nba.app.model.AuditLogModel;
import org.y9nba.app.model.FileModel;
import org.y9nba.app.model.UserModel;
import org.y9nba.app.repository.AuditLogRepository;
import org.y9nba.app.service.AuditLogService;

import java.util.Set;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository repository;

    public AuditLogServiceImpl(AuditLogRepository repository) {
        this.repository = repository;
    }

    @Override
    public AuditLogModel save(AuditLogCreateDto entity) {
        return repository.save(new AuditLogModel(entity));
    }

    @Override
    public void logDownload(UserModel user, FileModel file) {
        save(new AuditLogCreateDto(user, file, Action.ACTION_DOWNLOAD));
    }

    @Override
    public void logCreate(UserModel user, FileModel file) {
        save(new AuditLogCreateDto(user, file, Action.ACTION_CREATE));
    }

    @Override
    public void logUpdate(UserModel user, FileModel file) {
        save(new AuditLogCreateDto(user, file, Action.ACTION_UPDATE));
    }

    @Override
    public void logRename(UserModel user, FileModel file) {
        save(new AuditLogCreateDto(user, file, Action.ACTION_RENAME));
    }

    @Override
    public void logMove(UserModel user, FileModel file) {
        save(new AuditLogCreateDto(user, file, Action.ACTION_MOVE));
    }

    @Override
    public void logCopy(UserModel user, FileModel file) {
        save(new AuditLogCreateDto(user, file, Action.ACTION_COPY));
    }

    @Override
    public void logAddAccess(UserModel user, FileModel file) {
        save(new AuditLogCreateDto(user, file, Action.ACTION_ADD_ACCESS));
    }

    @Override
    public void logRemoveAccess(UserModel user, FileModel file) {
        save(new AuditLogCreateDto(user, file, Action.ACTION_DEL_ACCESS));
    }


    @Override
    public void delete(AuditLogModel entity) {

    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public AuditLogModel findById(Long id) {
        return repository
                .findById(id)
                .orElseThrow(
                        () -> new HttpClientErrorException(HttpStatus.BAD_REQUEST)
                );
    }

    @Override
    public boolean existsById(Long id) {
        return false;
    }

    @Override
    public Set<AuditLogDto> findByUser(Long userId) {
        return null;
    }
}
