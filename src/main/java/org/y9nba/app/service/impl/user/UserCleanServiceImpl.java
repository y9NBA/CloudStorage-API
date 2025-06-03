package org.y9nba.app.service.impl.user;

import org.springframework.stereotype.Service;
import org.y9nba.app.constant.OneTimeTokenType;
import org.y9nba.app.constant.Role;
import org.y9nba.app.dao.entity.OneTimeToken;
import org.y9nba.app.dao.entity.User;
import org.y9nba.app.dao.repository.UserRepository;
import org.y9nba.app.service.face.user.UserCleanService;

import java.util.Set;

@Service
public class UserCleanServiceImpl implements UserCleanService {

    private final UserRepository userRepository;

    public UserCleanServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void deleteUnactivatedUsers() {
        Set<User> users = userRepository.findAllByRole(Role.ROLE_USER);

        users
                .stream()
                .filter(this::isUserUnactivated)
                .forEach(userRepository::delete);

    }

    private boolean isUserUnactivated(User user) {
        Set<OneTimeToken> oneTimeTokens = user.getOneTimeTokens();
        return oneTimeTokens.stream()
                .filter(token -> token.getType() == OneTimeTokenType.ACTIVATION)
                .anyMatch(token -> !token.isUsed() && !token.isExpired());
    }
}
