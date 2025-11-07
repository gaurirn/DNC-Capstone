package com.training.dunningcuring.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List; // <-- Make sure this is imported

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        // Define the security scheme (Bearer Token)
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                // Add the security scheme to the components
                .components(
                        new Components()
                                .addSecuritySchemes(securitySchemeName,
                                        new SecurityScheme()
                                                .name(securitySchemeName)
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                )
                )
                // Set the global security requirement

                .security(List.of(new SecurityRequirement().addList(securitySchemeName)))

                // Add basic API info
                .info(new Info()
                        .title("Dunning & Curing Management System (DCMS) API")
                        .version("v1.0")
                        .description("API for the DCMS project.")
                );
    }
}