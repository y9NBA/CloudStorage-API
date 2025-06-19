package org.y9nba.app.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.y9nba.app.dto.response.ErrorResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) {

        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/json");
        response.setStatus(HttpStatus.FORBIDDEN.value());

        try {
            response.getWriter().write(
                    new ObjectMapper().writeValueAsString(
                            new ErrorResponse(
                                    "Недостаточно прав",
                                    HttpStatus.FORBIDDEN.value()
                            )
                    )
            );
            response.getWriter().flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}