package org.y9nba.app.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.y9nba.app.exception.AbstractException;

import java.time.LocalDateTime;

@Getter
public class ErrorResponse extends Response {

    @Schema(description = "Код статуса возникшей ошибки", example = "404")
    private final int statusCode;

    public ErrorResponse(
            @Schema(
                    description = "Описание ошибки, сообщение об ошибке",
                    example = "Not found"
            )
            String message,
            int statusCode
    ) {
        super(message);
        this.statusCode = statusCode;
    }

    public ErrorResponse(AbstractException e) {
        super(e.getMessage());
        this.statusCode = e.getStatusCode().value();
    }
}
