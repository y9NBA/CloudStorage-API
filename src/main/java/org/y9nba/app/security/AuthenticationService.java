package org.y9nba.app.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.y9nba.app.dto.auth.TokenResponseDto;
import org.y9nba.app.dto.auth.LoginRequestDto;
import org.y9nba.app.dto.auth.RegistrationRequestDto;
import org.y9nba.app.dto.user.UserCreateDto;
import org.y9nba.app.exception.web.auth.OAuth2GoogleNotUserException;
import org.y9nba.app.exception.web.auth.UnAuthorizedException;
import org.y9nba.app.model.UserModel;
import org.y9nba.app.service.impl.ConfirmServiceImpl;
import org.y9nba.app.service.impl.UserServiceImpl;
import org.y9nba.app.util.PasswordUtil;

import java.util.Map;
import java.util.UUID;

@Service
public class AuthenticationService {

    private final UserServiceImpl userService;

    private final ConfirmServiceImpl confirmService;

    private final JwtService jwtService;

    private final PasswordUtil passwordUtil;

    private final AuthenticationManager authenticationManager;


    public AuthenticationService(UserServiceImpl userService, ConfirmServiceImpl confirmService,
                                 JwtService jwtService,
                                 PasswordUtil passwordUtil,
                                 AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.confirmService = confirmService;
        this.jwtService = jwtService;
        this.passwordUtil = passwordUtil;
        this.authenticationManager = authenticationManager;
    }

    public String register(RegistrationRequestDto request) {

        UserCreateDto userCreateDto = new UserCreateDto(
                request.getUsername(),
                request.getEmail(),
                passwordUtil.encode(request.getPassword())
        );

        UserModel user = userService.createUser(userCreateDto);

        return confirmService.sendActivateAccountConfirmation(user);
    }

    public TokenResponseDto authenticate(LoginRequestDto request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UserModel user = userService.getByUsername(request.getUsername());

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        jwtService.revokeAllToken(user);

        jwtService.saveUserToken(accessToken, refreshToken, user);

        return new TokenResponseDto(accessToken, refreshToken);
    }

    public TokenResponseDto refreshToken(HttpServletRequest request) {
        String token = jwtService.getTokenByRequest(request);
        String username = jwtService.getUsernameByAuthRequest(request);

        UserModel user = userService.getByUsername(username);

        if (jwtService.isValidRefresh(token, user)) {

            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            jwtService.revokeAllToken(user);

            jwtService.saveUserToken(accessToken, refreshToken, user);

            return new TokenResponseDto(accessToken, refreshToken);

        }

        throw new UnAuthorizedException();
    }

    public TokenResponseDto authenticateWithGoogle(Authentication authentication) {
        if (authentication instanceof OAuth2AuthenticationToken oAuth2AuthenticationToken) {
            OAuth2User oAuth2User = oAuth2AuthenticationToken.getPrincipal();

            if (oAuth2User != null) {
                Map<String, Object> attributes = oAuth2User.getAttributes();
                UserModel user = userService.getByEmail(attributes.get("email").toString());

                if (user == null) {
                    UserCreateDto userCreateDto = new UserCreateDto(
                            attributes.get("name").toString(),
                            attributes.get("email").toString(),
                            passwordUtil.encode(UUID.randomUUID().toString()),
                            true
                    );

                    user = userService.createUser(userCreateDto);
                }

                String accessToken = jwtService.generateAccessToken(user);
                String refreshToken = jwtService.generateRefreshToken(user);

                jwtService.revokeAllToken(user);

                jwtService.saveUserToken(accessToken, refreshToken, user);

                return new TokenResponseDto(accessToken, refreshToken);
            }
        }

        throw new OAuth2GoogleNotUserException();
    }
}
