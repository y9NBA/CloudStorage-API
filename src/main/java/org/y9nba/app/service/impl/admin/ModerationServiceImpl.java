package org.y9nba.app.service.impl.admin;

import org.springframework.stereotype.Service;
import org.y9nba.app.constant.Reason;
import org.y9nba.app.dao.entity.User;
import org.y9nba.app.dto.file.FilePresentDto;
import org.y9nba.app.service.face.admin.ModerationService;
import org.y9nba.app.service.impl.email.PublicFileInfoServiceImpl;

@Service
public class ModerationServiceImpl implements ModerationService {

    private final PublicFilesServiceImpl publicFilesService;
    private final WarningServiceImpl warningService;
    private final PublicFileInfoServiceImpl publicFileInfoService;

    public ModerationServiceImpl(PublicFilesServiceImpl publicFilesService, WarningServiceImpl warningService, PublicFileInfoServiceImpl publicFileInfoService) {
        this.publicFilesService = publicFilesService;
        this.warningService = warningService;
        this.publicFileInfoService = publicFileInfoService;
    }

    @Override
    public void revokePublicFileWithWarning(Long fileId, Long adminId) {
        publicFilesService.revokePublicFile(fileId);

        User author = publicFilesService.getAuthorOfPublicFileById(fileId);
        FilePresentDto file = new FilePresentDto(publicFilesService.getPublicFileById(fileId));

        warningService.createNewWarning(author.getId(), adminId, Reason.UNSATISFACTORY_PUBLIC_FILE);

        publicFileInfoService.sendWarningInfo(
                author.getEmail(),
                file.getFolderURL() + "/" + file.getFileName()
        );
    }

    @Override
    public void revokePublicFileWithNotification(Long fileId) {
        publicFilesService.revokePublicFile(fileId);

        User author = publicFilesService.getAuthorOfPublicFileById(fileId);
        FilePresentDto file = new FilePresentDto(publicFilesService.getPublicFileById(fileId));

        publicFileInfoService.sendNotificationInfo(
                author.getEmail(),
                file.getFolderURL() + "/" + file.getFileName()
        );
    }
}
