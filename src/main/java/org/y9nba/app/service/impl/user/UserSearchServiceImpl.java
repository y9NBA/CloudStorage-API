package org.y9nba.app.service.impl.user;

import org.springframework.stereotype.Service;
import org.y9nba.app.constant.Role;
import org.y9nba.app.dao.entity.User;
import org.y9nba.app.dao.repository.UserRepository;
import org.y9nba.app.exception.web.admin.NotFoundAdminByIdException;
import org.y9nba.app.exception.web.user.search.NotFoundUserByIdException;
import org.y9nba.app.service.face.user.UserSearchService;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserSearchServiceImpl implements UserSearchService {

    private final UserRepository repository;

    public UserSearchServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public User getUserById(Long id) {
        return repository
                .findAllByRole(Role.ROLE_USER)
                .stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElseThrow(
                        () -> new NotFoundUserByIdException(id)
                );
    }

    @Override
    public User getAdminById(Long id) {
        return repository
                .findAllByRole(Role.ROLE_USER)
                .stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElseThrow(
                        () -> new NotFoundAdminByIdException(id)
                );
    }

    @Override
    public Set<User> getAllUsers(String username, String email, UUID bucketName, Long userId) {
        return getAllWithFilters(Role.ROLE_USER, username, email, bucketName, userId, false);
    }

    @Override
    public Set<User> getAllActiveUsers(String username, String email, UUID bucketName, Long id) {
        return getAllWithFilters(Role.ROLE_USER, username, email, bucketName, id, false)
                .stream()
                .filter(User::isEnabled)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<User> getAllBannedUsers(String username, String email, UUID bucketName, Long id) {
        return getAllWithFilters(Role.ROLE_USER, username, email, bucketName, id, true);
    }

    @Override
    public Set<User> getAllAdmins(String username, String email, UUID bucketName, Long userId) {
        return getAllWithFilters(Role.ROLE_ADMIN, username, email, bucketName, userId, false);
    }

    private Set<User> getAllWithFilters(Role role, String username, String email, UUID bucketName, Long authorizedUserId, boolean isBanned) {
        Set<User> models = new HashSet<>(repository.findAll());

        if (role != null) {
            models = repository.findAllByRole(role);
        }

        Stream<User> stream = models.stream();

        stream = stream
                .filter(
                        u -> !u.getId().equals(authorizedUserId)
                )
                .filter(User::isEnabled);

        if (isBanned) {
            stream = stream
                    .filter(User::isBanned);
        } else {
            stream = stream
                    .filter(
                            u -> !u.isBanned()
                    );
        }

        if (username != null) {
            stream = stream
                    .filter(
                            u -> u.getUsername()
                                    .toLowerCase()
                                    .contains(username.toLowerCase())
                    );
        }

        if (email != null) {
            stream = stream
                    .filter(
                            u -> u.getEmail()
                                    .toLowerCase()
                                    .contains(email.toLowerCase())
                    );
        }

        if (bucketName != null) {
            stream = stream
                    .filter(
                            u -> u.getBucketName().equals(bucketName.toString())
                    );
        }

        return stream.collect(Collectors.toSet());
    }

}
