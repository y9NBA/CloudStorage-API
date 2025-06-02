package org.y9nba.app.service.impl;

import org.springframework.stereotype.Service;
import org.y9nba.app.constant.OneTimeTokenType;
import org.y9nba.app.dto.onetimetoken.OneTimeTokenCreateDto;
import org.y9nba.app.exception.web.user.OneTimeTokenNotValidException;
import org.y9nba.app.dao.entity.OneTimeToken;
import org.y9nba.app.dao.entity.User;
import org.y9nba.app.dao.repository.OneTimeTokenRepository;
import org.y9nba.app.security.JwtService;
import org.y9nba.app.service.face.OneTimeTokenService;

import java.util.Set;
import java.util.UUID;

@Service
public class OneTimeTokenServiceImpl implements OneTimeTokenService {

    private final OneTimeTokenRepository repository;
    private final JwtService jwtService;

    private final Long expiryTime = 3600000L;    // 1 час
    private final Long expiryTimeRollback = 86400000L;    // 24 часа

    public OneTimeTokenServiceImpl(OneTimeTokenRepository repository, JwtService jwtService) {
        this.repository = repository;
        this.jwtService = jwtService;
    }

    @Override
    public UUID createActivationToken(User user) {
        OneTimeTokenCreateDto dto = new OneTimeTokenCreateDto(
                user,
                generateToken(user),
                OneTimeTokenType.ACTIVATION
        );

        return save(dto).getId();
    }

    @Override
    public UUID createResetPasswordToken(User user) {
        OneTimeTokenCreateDto dto = new OneTimeTokenCreateDto(
                user,
                generateToken(user),
                OneTimeTokenType.RESET_PASSWORD
        );

        return save(dto).getId();
    }

    @Override
    public UUID createRollbackPasswordToken(User user) {
        OneTimeTokenCreateDto dto = new OneTimeTokenCreateDto(
                user,
                generateTokenWithHashPassword(user),
                OneTimeTokenType.ROLLBACK_PASSWORD
        );

        return save(dto).getId();
    }

    @Override
    public UUID createRollbackEmailToken(User user, String oldEmail) {
        OneTimeTokenCreateDto dto = new OneTimeTokenCreateDto(
                user,
                generateTokenWithEmail(user, oldEmail, true),
                OneTimeTokenType.ROLLBACK_EMAIL
        );

        return save(dto).getId();
    }

    @Override
    public UUID createUpdateEmailToken(User user, String newEmail) {
        OneTimeTokenCreateDto dto = new OneTimeTokenCreateDto(
                user,
                generateTokenWithEmail(user, newEmail, false),
                OneTimeTokenType.UPDATE_EMAIL
        );

        return save(dto).getId();
    }

    @Override
    public String findTokenById(UUID oneTimeTokenId) {
        return repository
                .findById(oneTimeTokenId)
                .orElseThrow(OneTimeTokenNotValidException::new)
                .getToken();
    }

    @Override
    public void checkOneTimeToken(String token, OneTimeTokenType type) {
        if (!jwtService.isValidOneTime(token, type)) {
            throw new OneTimeTokenNotValidException();
        }
    }

    @Override
    public void revokeOneTimeToken(String token) {
        repository.findByToken(token).ifPresent(t -> {
            t.setUsed(true);
            repository.save(t);
        });
    }

    @Override
    public void revokeAllOneTimeTokenWithType(Long userId, OneTimeTokenType type) {
        Set<OneTimeToken> tokenModels = repository
                .findAllByUser_IdAndType(userId, type);

        tokenModels.forEach(t -> t.setUsed(true));

        repository.saveAll(tokenModels);
    }

    @Override
    public String extractEmailFromToken(String token) {
        return jwtService.extractClaim(token, "email");
    }

    @Override
    public String extractHashPasswordFromToken(String token) {
        return jwtService.extractClaim(token, "hashPassword");
    }

    @Override
    public Long extractUserIdFromToken(String token) {
        return Long.valueOf(jwtService.extractUserId(token));
    }

    private OneTimeToken save(OneTimeTokenCreateDto dto) {
        return repository.save(new OneTimeToken(dto));
    }

    private String generateToken(User user) {
        return jwtService.getOneTimeTokenBuilder(user, expiryTime).compact();
    }

    private String generateTokenWithEmail(User user, String email, boolean rollback) {
        return jwtService
                .getOneTimeTokenBuilder(user, rollback ? expiryTimeRollback : expiryTime)
                .claim("email", email)
                .compact();
    }

    private String generateTokenWithHashPassword(User user) {
        return jwtService
                .getOneTimeTokenBuilder(user, expiryTimeRollback)
                .claim("hashPassword", user.getPassword())
                .compact();
    }
}
