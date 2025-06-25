package org.y9nba.app.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.y9nba.app.dto.auth.TokenResponseDto;
import org.y9nba.app.dto.response.ErrorResponse;
import org.y9nba.app.exception.web.AbstractException;
import org.y9nba.app.exception.web.auth.oauth2.OAuth2AuthenticationTokenNotValidException;
import org.y9nba.app.exception.web.auth.oauth2.OAuth2Exception;
import org.y9nba.app.security.auth.AuthenticationService;
import org.y9nba.app.security.auth.OAuth2Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final OAuth2Service oauth2Service;

    public OAuth2LoginSuccessHandler(OAuth2Service oauth2Service) {
        this.oauth2Service = oauth2Service;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, AbstractException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/json");

        try {
            if (authentication instanceof OAuth2AuthenticationToken oauth2AuthenticationToken) {
                OAuth2User oauth2User = oauth2AuthenticationToken.getPrincipal();
                String provider = oauth2AuthenticationToken.getAuthorizedClientRegistrationId();

                if (oauth2User != null) {
                    log.info("Provider: {}", provider);
                    TokenResponseDto tokenResponse = oauth2Service.authenticate(oauth2User, provider);

                    flushResponse(response, tokenResponse);

                    return;
                }
            }

            throw new OAuth2AuthenticationTokenNotValidException();

        } catch (AbstractException e) {
            flushResponse(
                    response,
                    new ErrorResponse(e)
            );
        }
    }

    // Без него никак, здесь advice controller не работает, так как здесь всё вылетает выше
    private void flushResponse(HttpServletResponse response, Object object) throws IOException {
        response.getWriter().write(
                new ObjectMapper().writeValueAsString(object)
        );
        response.getWriter().flush();
    }
}
