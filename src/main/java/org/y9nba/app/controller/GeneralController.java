package org.y9nba.app.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.actuate.health.PingHealthIndicator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.y9nba.app.dto.message.MessageDto;
import org.y9nba.app.dto.message.SimpleMessageDto;

import java.util.Random;

@Tag(
        name = "Общий контроллер",
        description = "Позволяет проверить доступность сервера"
)
@RestController
@RequestMapping("/api/v1")
public class GeneralController {


    @Operation(
            summary = "Проверка доступности сервера",
            description = "Вовзращает вещественное число от 0 до 1"
    )
    @GetMapping("/ping")
    public ResponseEntity<?> getPing() {
        return ResponseEntity.ok(new SimpleMessageDto<>(new Random().nextDouble()));
    }

    @Operation(
            summary = "Проверка состояния сервера",
            description = "Возвращает текущее состояние сервера"
    )
    @GetMapping("/health")
    public ResponseEntity<?> getHealthStatus() {
        PingHealthIndicator indicator = new PingHealthIndicator();
        return ResponseEntity.ok(indicator.getHealth(true));
    }
}
