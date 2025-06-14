package org.y9nba.app.service.impl.admin;

import org.springframework.stereotype.Service;
import org.y9nba.app.constant.Reason;
import org.y9nba.app.dao.entity.User;
import org.y9nba.app.dao.entity.Warning;
import org.y9nba.app.dao.repository.WarningRepository;
import org.y9nba.app.exception.web.admin.UserNotHaveActiveWarningsException;
import org.y9nba.app.service.face.admin.WarningService;
import org.y9nba.app.service.impl.user.UserSearchServiceImpl;

import java.util.Set;

@Service
public class WarningServiceImpl implements WarningService {

    private final BanServiceImpl banService;
    private final WarningRepository warningRepository;
    private final UserSearchServiceImpl userSearchService;

    public WarningServiceImpl(BanServiceImpl banService, WarningRepository warningRepository, UserSearchServiceImpl userSearchService) {
        this.banService = banService;
        this.warningRepository = warningRepository;
        this.userSearchService = userSearchService;
    }

    @Override
    public Warning createNewWarning(Long userId, Long adminId, Reason reason) {
        User user = userSearchService.getUserById(userId);
        User admin = userSearchService.getAdminById(adminId);

        Warning warning = new Warning();

        warning.setUser(user);
        warning.setAdmin(admin);
        warning.setReason(reason);

        warning = warningRepository.save(warning);

        if (getAllActiveWarningsByUserId(userId).size() >= 3) {
            banService.banUser(userId);
        }

        return warning;
    }

    @Override
    public void revokeWarning(Long userId) {
        User user = userSearchService.getUserById(userId);

        Set<Warning> warnings = warningRepository.findAllByUser_IdAndActiveTrue(userId);

        if (warnings.isEmpty()) {
            throw new UserNotHaveActiveWarningsException(userId);
        }

        Warning warning = warnings.iterator().next();
        warning.setActive(false);
        warningRepository.save(warning);

        if (getAllActiveWarningsByUserId(userId).size() < 3 && user.isBanned()) {
            banService.unbanUser(userId);
        }
    }

    @Override
    public void revokeAllWarnings(Long userId) {
        User user = userSearchService.getUserById(userId);

        Set<Warning> warnings = warningRepository.findAllByUser_IdAndActiveTrue(userId);

        if (warnings.isEmpty()) {
            throw new UserNotHaveActiveWarningsException(userId);
        }

        warnings.forEach(warning -> {
                            warning.setActive(false);
                            warningRepository.save(warning);
                        }
                );

        if (user.isBanned()) {
            banService.unbanUser(userId);
        }
    }

    @Override
    public Set<Warning> getAllWarningsByUserId(Long userId) {
        userSearchService.getUserById(userId);
        return warningRepository.findAllByUser_Id(userId);
    }

    @Override
    public Set<Warning> getAllActiveWarningsByUserId(Long userId) {
        userSearchService.getUserById(userId);
        return warningRepository.findAllByUser_IdAndActiveTrue(userId);
    }
}
