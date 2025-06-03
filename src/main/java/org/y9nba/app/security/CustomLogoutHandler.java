package org.y9nba.app.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import org.y9nba.app.dto.response.Response;
import org.y9nba.app.exception.web.auth.UnAuthorizedException;
import org.y9nba.app.dao.entity.Session;
import org.y9nba.app.service.impl.token.session.SessionServiceImpl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class CustomLogoutHandler implements LogoutHandler {

    private final SessionServiceImpl sessionService;

    private final JwtService jwtService;

    public CustomLogoutHandler(SessionServiceImpl sessionService, JwtService jwtService) {
        this.sessionService = sessionService;
        this.jwtService = jwtService;
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

            Session session = sessionService.getSessionById(jwtService.getSessionIdByToken(token));

            if (session != null) {

                sessionService.revokeSession(session);

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
