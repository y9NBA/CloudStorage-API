package org.y9nba.app.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.y9nba.app.dto.auth.TokenResponseDto;
import org.y9nba.app.exception.web.auth.OAuth2GoogleNotUserException;
import org.y9nba.app.security.AuthenticationService;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final AuthenticationService authenticationService;

    public OAuth2LoginSuccessHandler(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        TokenResponseDto tokenResponse = authenticationService.authenticateWithGoogle(authentication, request);

        response.setContentType("application/json");
        response.getWriter().write(convertToJson(tokenResponse));
        response.getWriter().flush();
    }

    private String convertToJson(TokenResponseDto tokenResponse) {
        try {
            return new ObjectMapper().writeValueAsString(tokenResponse);
        } catch (Exception e) {
            throw new OAuth2GoogleNotUserException();
        }
    }
}
