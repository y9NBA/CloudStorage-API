package org.y9nba.app.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Cloud Storage API",
                description = "API облачного хранилища",
                version = "0.0.3",
                contact = @Contact(
                        name = "y9nba",
                        url = "https://github.com/y9NBA"
                )
        )
)
@SecuritySchemes({
        @SecurityScheme(
                name = "Bearer Authentication",
                type = SecuritySchemeType.HTTP,
                bearerFormat = "JWT",
                scheme = "Bearer"
        ),
        @SecurityScheme(
                name = "Refresh Token",
                type = SecuritySchemeType.HTTP,
                bearerFormat = "JWT",
                scheme = "Bearer"
        )
})
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .addSecurityItem(new SecurityRequirement()
                        .addList("Bearer Authentication")
                        .addList("Refresh Token"));
    }
}