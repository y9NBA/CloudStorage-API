package org.y9nba.app.service.impl.token.onetime;

import io.jsonwebtoken.JwtBuilder;
import org.springframework.stereotype.Service;
import org.y9nba.app.constant.OneTimeTokenType;
import org.y9nba.app.dto.onetimetoken.OneTimeTokenCreateDto;
import org.y9nba.app.exception.web.user.OneTimeTokenNotValidException;
import org.y9nba.app.dao.entity.OneTimeToken;
import org.y9nba.app.dao.entity.User;
import org.y9nba.app.dao.repository.OneTimeTokenRepository;
import org.y9nba.app.security.JwtService;
import org.y9nba.app.service.face.token.onetime.OneTimeTokenService;

import java.util.Set;
import java.util.UUID;

@Service
public class OneTimeTokenServiceImpl implements OneTimeTokenService {

    private final OneTimeTokenRepository repository;
    private final JwtService jwtService;

    public OneTimeTokenServiceImpl(OneTimeTokenRepository repository, JwtService jwtService) {
        this.repository = repository;
        this.jwtService = jwtService;
    }

    @Override
    public String createActivationToken(User user) {
        OneTimeTokenCreateDto dto = new OneTimeTokenCreateDto(
                user,
                OneTimeTokenType.ACTIVATION
        );

        UUID oneTimeTokenId = save(dto).getId();

        return generateToken(user, oneTimeTokenId, OneTimeTokenType.ACTIVATION.getExpireTime());
    }

    @Override
    public String createResetPasswordToken(User user) {
        OneTimeTokenCreateDto dto = new OneTimeTokenCreateDto(
                user,
                OneTimeTokenType.RESET_PASSWORD
        );

        UUID oneTimeTokenId = save(dto).getId();

        return generateToken(
                user,
                oneTimeTokenId,
                OneTimeTokenType.RESET_PASSWORD.getExpireTime()
        );
    }

    @Override
    public String createRollbackPasswordToken(User user) {
        OneTimeTokenCreateDto dto = new OneTimeTokenCreateDto(
                user,
                OneTimeTokenType.ROLLBACK_PASSWORD
        );

        UUID oneTimeTokenId = save(dto).getId();

        return generateTokenWithHashPassword(
                user,
                oneTimeTokenId,
                OneTimeTokenType.ROLLBACK_PASSWORD.getExpireTime()
        );
    }

    @Override
    public String createRollbackEmailToken(User user, String oldEmail) {
        OneTimeTokenCreateDto dto = new OneTimeTokenCreateDto(
                user,
                OneTimeTokenType.ROLLBACK_EMAIL
        );

        UUID oneTimeTokenId = save(dto).getId();

        return generateTokenWithEmail(
                user,
                oneTimeTokenId,
                OneTimeTokenType.ROLLBACK_EMAIL.getExpireTime(),
                oldEmail
        );
    }

    @Override
    public String createUpdateEmailToken(User user, String newEmail) {
        OneTimeTokenCreateDto dto = new OneTimeTokenCreateDto(
                user,
                OneTimeTokenType.UPDATE_EMAIL
        );

        UUID oneTimeTokenId = save(dto).getId();

        return generateTokenWithEmail(
                user,
                oneTimeTokenId,
                OneTimeTokenType.UPDATE_EMAIL.getExpireTime(),
                newEmail
        );
    }

    @Override
    public String createDeleteAccountToken(User user) {
        OneTimeTokenCreateDto dto = new OneTimeTokenCreateDto(
                user,
                OneTimeTokenType.DELETE_ACCOUNT
        );

        UUID oneTimeTokenId = save(dto).getId();

        return generateToken(
                user,
                oneTimeTokenId,
                OneTimeTokenType.DELETE_ACCOUNT.getExpireTime()
        );
    }

    @Override
    public void checkOneTimeToken(String token, OneTimeTokenType type) {
        if (!jwtService.isValidOneTime(token, type)) {
            throw new OneTimeTokenNotValidException();
        }
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
    public Long getUserIdByOneTimeToken(String token) {
        return jwtService.getUserIdByToken(token);
    }

    private OneTimeToken save(OneTimeTokenCreateDto dto) {
        return repository.save(new OneTimeToken(dto));
    }

    private String generateToken(User user, UUID onetimeTokenId, Long expiryTime) {
        return getOneTimeTokenBuilderWithUUIDEntry(user, onetimeTokenId, expiryTime)
                .compact();
    }

    private String generateTokenWithEmail(User user, UUID onetimeTokenId, Long expiryTime, String email) {
        return getOneTimeTokenBuilderWithUUIDEntry(user, onetimeTokenId, expiryTime)
                .claim("email", email)
                .compact();
    }

    private String generateTokenWithHashPassword(User user, UUID onetimeTokenId, Long expiryTime) {
        return getOneTimeTokenBuilderWithUUIDEntry(user, onetimeTokenId, expiryTime)
                .claim("hashPassword", user.getPassword())
                .compact();
    }

    private JwtBuilder getOneTimeTokenBuilderWithUUIDEntry(User user, UUID onetimeTokenId, Long expiry) {
        return jwtService
                .getOneTimeTokenBuilder(user, expiry)
                .claim("oneTimeTokenId", onetimeTokenId.toString());
    }
}
