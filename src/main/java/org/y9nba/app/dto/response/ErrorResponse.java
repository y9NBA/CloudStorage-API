package org.y9nba.app.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ErrorResponse extends Response {

    @Schema(description = "Точная дата и время возникновения ошибки", example = "123456")
    private final LocalDateTime timeStamp = LocalDateTime.now();

    public ErrorResponse(String message) {
        super(message);
    }
}
