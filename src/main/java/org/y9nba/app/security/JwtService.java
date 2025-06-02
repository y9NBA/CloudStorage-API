package org.y9nba.app.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.y9nba.app.constant.OneTimeTokenType;
import org.y9nba.app.exception.web.auth.UnAuthorizedException;
import org.y9nba.app.dao.entity.User;
import org.y9nba.app.dao.repository.OneTimeTokenRepository;
import org.y9nba.app.dao.repository.SessionRepository;
import org.y9nba.app.service.impl.token.SessionServiceImpl;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${token.signing.key}")
    private String secretKey;

    @Value("${token.signing.access_token}")
    private long accessTokenExpiration;

    @Value("${token.signing.refresh_token}")
    private long refreshTokenExpiration;

    private final SessionServiceImpl sessionService;
    private final SessionRepository sessionRepository;
    private final OneTimeTokenRepository oneTimeTokenRepository;

    public JwtService(SessionServiceImpl sessionService, SessionRepository sessionRepository, OneTimeTokenRepository oneTimeTokenRepository) {
        this.sessionService = sessionService;
        this.sessionRepository = sessionRepository;
        this.oneTimeTokenRepository = oneTimeTokenRepository;
    }

    public boolean isValid(String token, User user) {
        return isValidToken(token, user)
                && isAccessTokenExpired(token);
    }

    public boolean isValidRefresh(String token, User user) {
        return isValidToken(token, user)
                && isAccessTokenExpired(token);
    }

    private boolean isValidToken(String token, User user) {
        Long userId = Long.parseLong(extractUserId(token));
        UUID sessionId = getSessionIdByToken(token);

        boolean isValidToken = sessionRepository
                .findById(sessionId)
                .map(
                        s -> !s.isLoggedOut()
                ).orElse(false);

        return isValidToken && userId.equals(user.getId());
    }

    public boolean isValidOneTime(String token, OneTimeTokenType tokenType) {
        Long userId = Long.valueOf(extractUserId(token));
        UUID oneTimeTokenId = getOneTimeTokenIdByToken(token);

        boolean isValidOneTimeToken = oneTimeTokenRepository
                .findByIdAndUser_IdAndType(
                        oneTimeTokenId,
                        userId,
                        tokenType
                )
                .map(t -> !t.isUsed())
                .orElse(false);

        return isAccessTokenExpired(token)
                && isValidOneTimeToken;
    }

    public void revokeAllSession(Long userId) {
        sessionService.revokeAllSessionsByUserId(userId);
    }

    private boolean isAccessTokenExpired(String token) {
        return !extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractUserId(String token) {
        return extractClaim(token, Claims::getId);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractSessionId(String token) {
        return extractClaim(token, "sessionId");
    }

    public String extractOneTimeTokenId(String token) {
        return extractClaim(token, "oneTimeTokenId");
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    public String extractClaim(String token, String key) {
        Claims claims = extractAllClaims(token);
        return claims.get(key, String.class);
    }

    private Claims extractAllClaims(String token) {

        JwtParserBuilder parser = Jwts.parserBuilder();

        parser.setSigningKey(getSigningKey());

        return parser.build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateAccessToken(User user, UUID sessionId) {

        return generateToken(user, accessTokenExpiration, sessionId);
    }

    public String generateRefreshToken(User user, UUID sessionId) {

        return generateToken(user, refreshTokenExpiration, sessionId);
    }

    public JwtBuilder getOneTimeTokenBuilder(User user, long expiryTime) {

        return getJwtBuilderByUser(user, expiryTime);
    }

    private String generateToken(User user, long expiryTime, UUID sessionId) {
        JwtBuilder builder = getJwtBuilderByUser(user, expiryTime);

        builder.claim("sessionId", sessionId);

        return builder.compact();
    }

    private JwtBuilder getJwtBuilderByUser(User user, long expiryTime) {
        return Jwts.builder()
                .setId(String.valueOf(user.getId()))
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiryTime))
                .signWith(getSigningKey());
    }

    private SecretKey getSigningKey() {

        byte[] keyBytes = Decoders.BASE64URL.decode(secretKey);

        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String getTokenByRequest(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new UnAuthorizedException();
        }

        return authorizationHeader.substring(7);
    }

    public String getUsernameByAuthRequest(HttpServletRequest request) {
        String token = getTokenByRequest(request);
        return extractUsername(token);
    }

    public UUID getSessionIdByToken(String token) {
        return UUID.fromString(extractSessionId(token));
    }

    public UUID getOneTimeTokenIdByToken(String token) {
        return UUID.fromString(extractOneTimeTokenId(token));
    }
}