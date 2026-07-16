package com.company.ems.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI / Swagger metadata. Once the application is running, the interactive
 * documentation is available at /swagger-ui.html and the raw spec at /v3/api-docs.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI emsOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Employee Management System API")
                        .description("Sample corporate-style REST API for managing departments and employees.")
                        .version("v1.0.0")
                        .contact(new Contact().name("Platform Engineering").email("platform-eng@example.com"))
                        .license(new License().name("Internal Use Only")));
    }
}
