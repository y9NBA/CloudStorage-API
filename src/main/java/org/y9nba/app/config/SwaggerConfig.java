package org.y9nba.app.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

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
public class SwaggerConfig {

}
