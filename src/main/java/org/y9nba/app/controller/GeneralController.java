package org.y9nba.app.controller;

import org.springframework.boot.actuate.health.PingHealthIndicator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@RequestMapping("/api/v1")
public class GeneralController {

    @GetMapping("/ping")
    public ResponseEntity<?> getPing() {
        Random random = new Random();
        return ResponseEntity.ok(random.nextDouble());
    }

    @GetMapping("/health")
    public ResponseEntity<?> getHealthStatus() {
        PingHealthIndicator indicator = new PingHealthIndicator();

        return ResponseEntity.ok(indicator.getHealth(true));
    }
}
