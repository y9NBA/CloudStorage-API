package org.y9nba.app.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.y9nba.app.exception.web.AbstractException;

import java.util.Set;

@Getter
public class ErrorResponseExt extends ErrorResponse {

    @Schema(description = "Информация об ошибках")
    private final Set<String> errors;

    public ErrorResponseExt(String message, Set<String> errors, int statusCode) {
        super(message, statusCode);
        this.errors = errors;
    }

    public ErrorResponseExt(AbstractException e) {
        super(e);
        this.errors = null;
    }
}
