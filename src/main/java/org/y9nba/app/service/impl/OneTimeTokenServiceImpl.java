package org.y9nba.app.service.impl;

import org.springframework.stereotype.Service;
import org.y9nba.app.constant.OneTimeTokenType;
import org.y9nba.app.dto.onetimetoken.OneTimeTokenCreateDto;
import org.y9nba.app.exception.web.user.OneTimeTokenNotValidException;
import org.y9nba.app.model.OneTimeTokenModel;
import org.y9nba.app.model.UserModel;
import org.y9nba.app.repository.OneTimeTokenRepository;
import org.y9nba.app.security.JwtService;
import org.y9nba.app.service.OneTimeTokenService;

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
    public UUID createActivationToken(UserModel userModel) {
        OneTimeTokenCreateDto dto = new OneTimeTokenCreateDto(
                userModel,
                generateToken(userModel),
                OneTimeTokenType.ACTIVATION
        );

        return save(dto).getId();
    }

    @Override
    public UUID createResetPasswordToken(UserModel userModel) {
        OneTimeTokenCreateDto dto = new OneTimeTokenCreateDto(
                userModel,
                generateToken(userModel),
                OneTimeTokenType.RESET_PASSWORD
        );

        return save(dto).getId();
    }

    @Override
    public UUID createRollbackPasswordToken(UserModel userModel) {
        OneTimeTokenCreateDto dto = new OneTimeTokenCreateDto(
                userModel,
                generateTokenWithHashPassword(userModel),
                OneTimeTokenType.ROLLBACK_PASSWORD
        );

        return save(dto).getId();
    }

    @Override
    public UUID createRollbackEmailToken(UserModel userModel, String oldEmail) {
        OneTimeTokenCreateDto dto = new OneTimeTokenCreateDto(
                userModel,
                generateTokenWithEmail(userModel, oldEmail, true),
                OneTimeTokenType.ROLLBACK_EMAIL
        );

        return save(dto).getId();
    }

    @Override
    public UUID createUpdateEmailToken(UserModel userModel, String newEmail) {
        OneTimeTokenCreateDto dto = new OneTimeTokenCreateDto(
                userModel,
                generateTokenWithEmail(userModel, newEmail, false),
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
        Set<OneTimeTokenModel> tokenModels = repository
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

    private OneTimeTokenModel save(OneTimeTokenCreateDto dto) {
        return repository.save(new OneTimeTokenModel(dto));
    }

    private String generateToken(UserModel userModel) {
        return jwtService.getOneTimeTokenBuilder(userModel, expiryTime).compact();
    }

    private String generateTokenWithEmail(UserModel userModel, String email, boolean rollback) {
        return jwtService
                .getOneTimeTokenBuilder(userModel, rollback ? expiryTimeRollback : expiryTime)
                .claim("email", email)
                .compact();
    }

    private String generateTokenWithHashPassword(UserModel userModel) {
        return jwtService
                .getOneTimeTokenBuilder(userModel, expiryTimeRollback)
                .claim("hashPassword", userModel.getPassword())
                .compact();
    }
}
