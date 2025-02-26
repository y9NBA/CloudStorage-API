package org.y9nba.app.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class Response {

    @Schema(description = "Какое то сообщение", example = "Some string")
    private final String message;

    public Response(String message) {
        this.message = message;
    }

}
