package org.y9nba.app.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.y9nba.app.constant.Role;
import org.y9nba.app.dto.auth.TokenResponseDto;
import org.y9nba.app.dto.auth.LoginRequestDto;
import org.y9nba.app.dto.auth.RegistrationRequestDto;
import org.y9nba.app.dto.user.UserCreateDto;
import org.y9nba.app.exception.web.UnAuthorizedException;
import org.y9nba.app.model.TokenModel;
import org.y9nba.app.model.UserModel;
import org.y9nba.app.repository.TokenRepository;
import org.y9nba.app.service.impl.UserServiceImpl;
import org.y9nba.app.util.PasswordUtil;

import java.util.Set;

@Service
public class AuthenticationService {

    private final UserServiceImpl userService;

    private final JwtService jwtService;

    private final PasswordUtil passwordUtil;

    private final AuthenticationManager authenticationManager;

    private final TokenRepository tokenRepository;


    public AuthenticationService(UserServiceImpl userService,
                                 JwtService jwtService,
                                 PasswordUtil passwordUtil,
                                 AuthenticationManager authenticationManager,
                                 TokenRepository tokenRepository) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.passwordUtil = passwordUtil;
        this.authenticationManager = authenticationManager;
        this.tokenRepository = tokenRepository;
    }

    public void register(RegistrationRequestDto request) {

        UserCreateDto userCreateDto = new UserCreateDto(
                request.getUsername(),
                request.getEmail(),
                passwordUtil.encode(request.getPassword())
        );

        userService.saveWithOneRole(userCreateDto, Role.ROLE_USER);
    }

    private void revokeAllToken(UserModel user) {

        Set<TokenModel> validTokens = tokenRepository.findAllByUser(user);

        if(!validTokens.isEmpty()) {
            validTokens.forEach(
                    t -> t.setLoggedOut(true)
            );
        }

        tokenRepository.saveAll(validTokens);
    }

    private void saveUserToken(String accessToken, String refreshToken, UserModel user) {

        TokenModel token = new TokenModel();

        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshToken);
        token.setLoggedOut(false);
        token.setUser(user);

        tokenRepository.save(token);
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

        revokeAllToken(user);

        saveUserToken(accessToken, refreshToken, user);

        return new TokenResponseDto(accessToken, refreshToken);
    }

    public TokenResponseDto refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String token = jwtService.getTokenByRequest(request);
        String username = jwtService.getUsernameByAuthRequest(request);

        UserModel user = userService.getByUsername(username);

        if (jwtService.isValidRefresh(token, user)) {

            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);

            revokeAllToken(user);

            saveUserToken(accessToken, refreshToken, user);

            return new TokenResponseDto(accessToken, refreshToken);

        }

        throw new UnAuthorizedException();
    }
}
