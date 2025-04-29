package org.y9nba.app.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.y9nba.app.exception.web.AbstractException;

@Getter
public class ErrorResponse extends Response {

    @Schema(description = "Код статус возникшей ошибки", example = "404")
    private final int statusCode;

    public ErrorResponse(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public ErrorResponse(AbstractException e) {
        super(e.getMessage());
        this.statusCode = e.getStatusCode().value();
    }
}
