package org.y9nba.app.service.impl.file;

import org.springframework.stereotype.Service;
import org.y9nba.app.constant.Action;
import org.y9nba.app.dto.auditlog.AuditLogCreateDto;
import org.y9nba.app.dao.entity.AuditLog;
import org.y9nba.app.dao.entity.File;
import org.y9nba.app.dao.entity.User;
import org.y9nba.app.dao.repository.AuditLogRepository;
import org.y9nba.app.service.face.file.AuditLogService;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository repository;

    public AuditLogServiceImpl(AuditLogRepository repository) {
        this.repository = repository;
    }

    @Override
    public AuditLog save(AuditLogCreateDto entity) {
        return repository.save(new AuditLog(entity));
    }

    @Override
    public void logDownload(User user, File file) {
        saveLog(user, file, Action.ACTION_DOWNLOAD);
    }

    @Override
    public void logCreate(User user, File file) {
        saveLog(user, file, Action.ACTION_CREATE);
    }

    @Override
    public void logUpdate(User user, File file) {
        saveLog(user, file, Action.ACTION_UPDATE);
    }

    @Override
    public void logRename(User user, File file) {
        saveLog(user, file, Action.ACTION_RENAME);
    }

    @Override
    public void logMove(User user, File file) {
        saveLog(user, file, Action.ACTION_MOVE);
    }

    @Override
    public void logCopy(User user, File file) {
        saveLog(user, file, Action.ACTION_COPY);
    }

    @Override
    public void logAddAccess(User user, File file) {
        saveLog(user, file, Action.ACTION_ADD_ACCESS);
    }

    @Override
    public void logRemoveAccess(User user, File file) {
        saveLog(user, file, Action.ACTION_DEL_ACCESS);
    }

    @Override
    public void logMakePublic(User user, File file) {
        saveLog(user, file, Action.ACTION_MAKE_PUBLIC);
    }

    @Override
    public void logMakePrivate(User user, File file) {
        saveLog(user, file, Action.ACTION_MAKE_PRIVATE);
    }


    @Override
    public void delete(AuditLog entity) {
        repository.delete(entity);
    }

    private void saveLog(User user, File file, Action action) {
        save(new AuditLogCreateDto(user, file, action));
    }
}
