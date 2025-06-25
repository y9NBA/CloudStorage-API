package org.y9nba.app.controller.auth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/oauth2")
public class OAuth2Controller {

    @GetMapping("/authorization/google")
    public void google() {    // Для обозначения в Swagger
    }

    @GetMapping("/authorization/github")
    public void github() {    // Для обозначения в Swagger
    }
}
