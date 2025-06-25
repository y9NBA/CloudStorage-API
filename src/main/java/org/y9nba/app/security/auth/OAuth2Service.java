package org.y9nba.app.security.auth;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.y9nba.app.dao.entity.Session;
import org.y9nba.app.dao.entity.User;
import org.y9nba.app.dto.auth.TokenResponseDto;
import org.y9nba.app.dto.user.UserCreateDto;
import org.y9nba.app.exception.web.auth.AccountLockedException;
import org.y9nba.app.exception.web.user.search.NotFoundUserByEmailException;
import org.y9nba.app.security.jwt.JwtService;
import org.y9nba.app.service.impl.email.ConfirmServiceImpl;
import org.y9nba.app.service.impl.provider.ProviderServiceImpl;
import org.y9nba.app.service.impl.token.session.SessionServiceImpl;
import org.y9nba.app.service.impl.user.UserServiceImpl;
import org.y9nba.app.util.PasswordUtil;
import org.y9nba.app.util.StringUtil;

import java.util.Map;

@Service
@Slf4j
public class OAuth2Service {

    private final UserServiceImpl userService;
    private final ConfirmServiceImpl confirmService;
    private final JwtService jwtService;
    private final SessionServiceImpl sessionService;

    private final AuthenticationManager authenticationManager;

    private final StringUtil stringUtil;
    private final PasswordUtil passwordUtil;
    private final ProviderServiceImpl providerService;

    public OAuth2Service(UserServiceImpl userService,
                         ConfirmServiceImpl confirmService,
                         JwtService jwtService,
                         SessionServiceImpl sessionService,
                         AuthenticationManager authenticationManager,
                         StringUtil stringUtil, PasswordUtil passwordUtil,
                         ProviderServiceImpl providerService) {
        this.userService = userService;
        this.confirmService = confirmService;
        this.jwtService = jwtService;
        this.sessionService = sessionService;
        this.authenticationManager = authenticationManager;
        this.stringUtil = stringUtil;
        this.passwordUtil = passwordUtil;
        this.providerService = providerService;
    }

    public TokenResponseDto authenticate(OAuth2User oauth2User, String provider) {
        try {

        } catch (IllegalArgumentException e) {}

        return
    }

    public TokenResponseDto authenticateWithGoogle(OAuth2User oAuth2User, HttpServletRequest request) {
        log.info("OAuth2User google: {}", oAuth2User);

        Map<String, Object> attributes = oAuth2User.getAttributes();
        User user;
        try {
            user = userService.getByEmail(attributes.get("email").toString());
        } catch (NotFoundUserByEmailException e) {
            String username = attributes.get("given_name").toString();
            String email = attributes.get("email").toString();
            String password = passwordUtil.generatePasswordWithSpecial(10L);

            UserCreateDto userCreateDto = new UserCreateDto(
                    username,
                    email,
                    password,
                    true
            );

            user = userService.createUser(userCreateDto);
        }

        return authenticateWithOAuth2(user, request);
    }

    public TokenResponseDto authenticateWithGithub(OAuth2AuthenticationToken oAuth2AuthenticationToken, HttpServletRequest request) {
        OAuth2User oAuth2User = oAuth2AuthenticationToken.getPrincipal();

        log.info("OAuth2User github: {}", oAuth2User);
        log.info("OAuth2User email: {}", (String) oAuth2User.getAttribute("email"));

        Map<String, Object> attributes = oAuth2User.getAttributes();
        User user;

        try {
            user = userService.getByEmail(attributes.get("email").toString());
        } catch (NotFoundUserByEmailException e) {
            String username = attributes.get("login").toString();
            String email = attributes.get("email").toString();
            String password = passwordUtil.generatePasswordWithSpecial(10L);

            UserCreateDto userCreateDto = new UserCreateDto(
                    username,
                    email,
                    password,
                    true
            );

            user = userService.createUser(userCreateDto);
        }

        Session session = sessionService.createSession(user, request);

        String accessToken = jwtService.generateAccessToken(user, session.getId(), session.getVersion());
        String refreshToken = jwtService.generateRefreshToken(user, session.getId(), session.getVersion());

        return new TokenResponseDto(accessToken, refreshToken);

    }

    private TokenResponseDto authenticateWithOAuth2(User user, HttpServletRequest request) {
        if (user.isBanned()) {
            throw new AccountLockedException();
        }

        Session session = sessionService.createSession(user, request);

        String accessToken = jwtService.generateAccessToken(user, session.getId(), session.getVersion());
        String refreshToken = jwtService.generateRefreshToken(user, session.getId(), session.getVersion());

        return new TokenResponseDto(accessToken, refreshToken);
    }
}
