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
import org.y9nba.app.model.TokenModel;
import org.y9nba.app.model.UserModel;
import org.y9nba.app.repository.OneTimeTokenRepository;
import org.y9nba.app.repository.TokenRepository;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Set;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${token.signing.key}")
    private String secretKey;

    @Value("${token.signing.access_token}")
    private long accessTokenExpiration;

    @Value("${token.signing.refresh_token}")
    private long refreshTokenExpiration;

    private final TokenRepository tokenRepository;
    private final OneTimeTokenRepository oneTimeTokenRepository;

    public JwtService(TokenRepository tokenRepository, OneTimeTokenRepository oneTimeTokenRepository) {
        this.tokenRepository = tokenRepository;
        this.oneTimeTokenRepository = oneTimeTokenRepository;
    }


    public boolean isValid(String token, UserModel user) {

        String userIdAsString = extractUserId(token);

        boolean isValidToken = tokenRepository.findByAccessToken(token)
                .map(t -> !t.isLoggedOut()).orElse(false);

        return userIdAsString.equals(user.getId().toString())
                && isAccessTokenExpired(token)
                && isValidToken;
    }

    public boolean isValidRefresh(String token, UserModel user) {

        String userIdAsString = extractUserId(token);

        boolean isValidRefreshToken = tokenRepository.findByRefreshToken(token)
                .map(t -> !t.isLoggedOut()).orElse(false);

        return userIdAsString.equals(user.getId().toString())
                && isAccessTokenExpired(token)
                && isValidRefreshToken;
    }

    public boolean isValidOneTime(String token, OneTimeTokenType tokenType) {

        Long userId = Long.valueOf(extractUserId(token));

        boolean isValidOneTimeToken = oneTimeTokenRepository
                .findByUser_IdAndTypeAndToken(
                        userId,
                        tokenType,
                        token
                )
                .map(t -> !t.isUsed())
                .orElse(false);

        return isAccessTokenExpired(token)
                && isValidOneTimeToken;
    }

    public void revokeAllToken(UserModel user) {

        Set<TokenModel> validTokens = tokenRepository.findAllByUser(user);

        if (!validTokens.isEmpty()) {
            validTokens.forEach(
                    t -> t.setLoggedOut(true)
            );
        }

        tokenRepository.saveAll(validTokens);
    }

    public void saveUserToken(String accessToken, String refreshToken, UserModel user) {

        TokenModel token = new TokenModel();

        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshToken);
        token.setLoggedOut(false);
        token.setUser(user);

        tokenRepository.save(token);
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

    public String generateAccessToken(UserModel user) {

        return generateToken(user, accessTokenExpiration);
    }

    public String generateRefreshToken(UserModel user) {

        return generateToken(user, refreshTokenExpiration);
    }

    public JwtBuilder getOneTimeTokenBuilder(UserModel user, long expiryTime) {

        return getJwtBuilderByUser(user, expiryTime);
    }

    private String generateToken(UserModel user, long expiryTime) {
        JwtBuilder builder = getJwtBuilderByUser(user, expiryTime);

        return builder.compact();
    }

    private JwtBuilder getJwtBuilderByUser(UserModel user, long expiryTime) {
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
}