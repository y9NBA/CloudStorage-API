package org.y9nba.app.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.y9nba.app.dao.entity.Session;
import org.y9nba.app.dto.auth.TokenResponseDto;
import org.y9nba.app.dto.auth.LoginRequestDto;
import org.y9nba.app.dto.auth.RegistrationRequestDto;
import org.y9nba.app.dto.user.UserCreateDto;
import org.y9nba.app.exception.web.user.info.email.NotValidEmailException;
import org.y9nba.app.exception.web.auth.OAuth2GoogleNotUserException;
import org.y9nba.app.exception.web.auth.UnAuthorizedException;
import org.y9nba.app.dao.entity.User;
import org.y9nba.app.service.impl.email.ConfirmServiceImpl;
import org.y9nba.app.service.impl.token.session.SessionServiceImpl;
import org.y9nba.app.service.impl.user.UserServiceImpl;
import org.y9nba.app.util.StringUtil;

import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
public class AuthenticationService {

    private final UserServiceImpl userService;
    private final ConfirmServiceImpl confirmService;
    private final JwtService jwtService;
    private final SessionServiceImpl sessionService;

    private final AuthenticationManager authenticationManager;

    private final StringUtil stringUtil;

    public AuthenticationService(UserServiceImpl userService,
                                 ConfirmServiceImpl confirmService,
                                 JwtService jwtService,
                                 SessionServiceImpl sessionService,
                                 AuthenticationManager authenticationManager, StringUtil stringUtil) {
        this.userService = userService;
        this.confirmService = confirmService;
        this.jwtService = jwtService;
        this.sessionService = sessionService;
        this.authenticationManager = authenticationManager;
        this.stringUtil = stringUtil;
    }

    public String register(RegistrationRequestDto registrationRequestDto) {

        UserCreateDto userCreateDto = new UserCreateDto(
                registrationRequestDto.getUsername(),
                registrationRequestDto.getEmail(),
                registrationRequestDto.getPassword()
        );

        User user = userService.createUser(userCreateDto);

        return confirmService.sendActivateAccountConfirmation(user);
    }

    public TokenResponseDto authenticate(LoginRequestDto loginRequestDto, HttpServletRequest request) {

        User user;

        if (stringUtil.isValidEmail(loginRequestDto.getLogin())) {
            user = userService.getByEmail(loginRequestDto.getLogin());
        } else {
            user = userService.getByUsername(loginRequestDto.getLogin());
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(),
                        loginRequestDto.getPassword()
                )
        );

        Session session = sessionService.getSessionByUserIdAndRequest(user.getId(), request);

        if (session != null) {
            sessionService.revokeSession(session);
        }

        session = sessionService.createSession(user, request);

        String accessToken = jwtService.generateAccessToken(user, session.getId(), session.getVersion());
        String refreshToken = jwtService.generateRefreshToken(user, session.getId(), session.getVersion());

        return new TokenResponseDto(accessToken, refreshToken);
    }

    public TokenResponseDto refreshToken(HttpServletRequest request) {
        String token = jwtService.getTokenByRequest(request);
        String username = jwtService.getUsernameByAuthRequest(request);

        User user = userService.getByUsername(username);

        if (jwtService.isValidRefresh(token, user)) {

            UUID sessionId = jwtService.getSessionIdByToken(token);

            Long version = sessionService.refreshSession(sessionId);

            String accessToken = jwtService.generateAccessToken(user, sessionId, version);
            String refreshToken = jwtService.generateRefreshToken(user, sessionId, version);

            return new TokenResponseDto(accessToken, refreshToken);
        }

        throw new UnAuthorizedException();
    }

    public TokenResponseDto authenticateWithGoogle(Authentication authentication, HttpServletRequest request) {
        if (authentication instanceof OAuth2AuthenticationToken oAuth2AuthenticationToken) {
            OAuth2User oAuth2User = oAuth2AuthenticationToken.getPrincipal();

            if (oAuth2User != null) {
                Map<String, Object> attributes = oAuth2User.getAttributes();
                User user = userService.getByEmail(attributes.get("email").toString());

                if (user == null) {
                    String username = attributes.get("name").toString();
                    String email = attributes.get("email").toString();
                    String password = UUID.randomUUID().toString();

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
        }

        throw new OAuth2GoogleNotUserException();
    }
}

