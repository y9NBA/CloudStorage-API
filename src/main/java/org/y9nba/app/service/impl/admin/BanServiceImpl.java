package org.y9nba.app.service.impl.admin;

import org.springframework.stereotype.Service;
import org.y9nba.app.dao.entity.User;
import org.y9nba.app.exception.web.admin.BanUserAlreadyException;
import org.y9nba.app.exception.web.admin.UnBanUserAlreadyException;
import org.y9nba.app.service.face.admin.BanService;
import org.y9nba.app.service.impl.email.LockedAccountInfoServiceImpl;
import org.y9nba.app.service.impl.token.session.SessionServiceImpl;
import org.y9nba.app.service.impl.user.UserSearchServiceImpl;
import org.y9nba.app.service.impl.user.UserServiceImpl;

@Service
public class BanServiceImpl implements BanService {

    private final UserServiceImpl userService;
    private final UserSearchServiceImpl userSearchService;
    private final SessionServiceImpl sessionService;
    private final LockedAccountInfoServiceImpl lockedAccountInfoService;

    public BanServiceImpl(UserServiceImpl userService, UserSearchServiceImpl userSearchService, SessionServiceImpl sessionService, LockedAccountInfoServiceImpl lockedAccountInfoService) {
        this.userService = userService;
        this.userSearchService = userSearchService;
        this.sessionService = sessionService;
        this.lockedAccountInfoService = lockedAccountInfoService;
    }

    @Override
    public void banUser(Long userId) {
        User user = userSearchService.getUserById(userId);

        if (user.isBanned()) {
            throw new BanUserAlreadyException();
        }

        user.setBanned(true);
        userService.save(user);

        sessionService.revokeAllSessionsByUserId(userId);

        lockedAccountInfoService.sendBanAccountInfo(
                user.getEmail(),
                user.getUsername()
        );
    }

    @Override
    public void unbanUser(Long userId) {
        User user = userSearchService.getUserById(userId);

        if (!user.isBanned()) {
            throw new UnBanUserAlreadyException();
        }

        user.setBanned(false);
        userService.save(user);

        lockedAccountInfoService.sendUnbanAccountInfo(
                user.getEmail(),
                user.getUsername()
        );
    }
}
