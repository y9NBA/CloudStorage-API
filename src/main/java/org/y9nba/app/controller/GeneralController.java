package org.y9nba.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.PingHealthIndicator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.y9nba.app.dto.response.Response;

import java.util.Random;

@Tag(
        name = "General controller",
        description = "Содержит методы для проверки доступности и состояния сервера"
)
@RestController
@RequestMapping("/general")
public class GeneralController {

    @Operation(
            summary = "Проверка состояния сервера",
            description = "Возвращает текущее состояние сервера"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Сервер работает",
                    content = @Content(
                            schema = @Schema(implementation = Health.class),
                            examples = @ExampleObject(value = "{\"status\": \"value\"}")
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @GetMapping("/health")
    public Health getHealthStatus() {
        PingHealthIndicator indicator = new PingHealthIndicator();
        return indicator.getHealth(true);
    }

    @Operation(
            summary = "Проверка доступности сервера",
            description = "Возвращает вещественное число от 0 до 1, указывающее на доступность сервера"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Сервер доступен",
                    content = @Content(
                            schema = @Schema(implementation = Response.class),
                            examples = @ExampleObject(value = "{\"message\": \"0.12345678\"}")
                    )
            ),
            @ApiResponse(responseCode = "500", description = "Ошибка сервера")
    })
    @GetMapping("/ping")
    public Response getPing() {
        return new Response(String.valueOf(new Random().nextDouble()));
    }
}
