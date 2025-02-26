package org.y9nba.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.PingHealthIndicator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.y9nba.app.dto.response.Response;

import java.util.Random;

@Tag(
        name = "Общий контроллер",
        description = "Позволяет проверить доступность сервера"
)
@RestController
@RequestMapping("/general")
public class GeneralController {

    @Operation(
            summary = "Проверка состояния сервера",
            description = "Возвращает текущее состояние сервера"
    )
    @GetMapping("/health")
    public Health getHealthStatus() {
        PingHealthIndicator indicator = new PingHealthIndicator();
        return indicator.getHealth(true);
    }

    @Operation(
            summary = "Проверка доступности сервера",
            description = "Вовзращает вещественное число от 0 до 1"
    )
    @GetMapping("/ping")
    public Response getPing() {
        return new Response(String.valueOf(new Random().nextDouble()));
    }
}
