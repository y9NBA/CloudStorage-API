package org.y9nba.app.service.face.admin;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.y9nba.app.dao.entity.File;
import org.y9nba.app.dao.entity.User;

import java.util.Set;

public interface PublicFilesService {
    Set<File> getPublicFilesByUserId(Long userId);
    Set<File> getAllPublicFiles();
    File getPublicFileById(Long fileId);
    ResponseEntity<InputStreamResource> viewPublicFile(Long fileId);
    User getAuthorOfPublicFileById(Long fileId);
    void revokePublicFile(Long fileId);
}
