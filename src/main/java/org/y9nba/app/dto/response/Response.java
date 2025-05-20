package org.y9nba.app.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Response {

    @Schema(description = "Какое то сообщение", example = "Some string")
    private String message;

    public Response(String message) {
        this.message = message;
    }

    public String asJSON() {
        if (message != null) {
            return "{\"message\":\"" + message + "\"}";
        } else {
            return "{}";
        }
    }
}
