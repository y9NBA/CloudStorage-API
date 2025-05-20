package org.y9nba.app.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.y9nba.app.dto.response.Response;
import org.y9nba.app.exception.web.auth.UnAuthorizedException;
import org.y9nba.app.model.TokenModel;
import org.y9nba.app.repository.TokenRepository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class CustomLogoutHandler implements LogoutHandler {

    private final TokenRepository tokenRepository;

    public CustomLogoutHandler(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication) {

        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/json");

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnAuthorizedException();
        } else {
            String token = authHeader.substring(7);

            TokenModel tokenEntity = tokenRepository.findByAccessToken(token).orElse(null);

            if (tokenEntity != null && !tokenEntity.isLoggedOut()) {
                tokenEntity.setLoggedOut(true);
                tokenRepository.save(tokenEntity);

                try {
                    response.getWriter().printf(new Response("Вы успешно вышли из системы").asJSON());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                throw new UnAuthorizedException();
            }
        }
    }
}
