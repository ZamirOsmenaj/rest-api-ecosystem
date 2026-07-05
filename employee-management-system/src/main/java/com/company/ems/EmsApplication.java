package com.company.ems;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of the Employee Management System (EMS) service.
 *
 * This is a small, self-contained reference project showing a typical
 * layered Spring Boot REST API: controller -> service -> repository -> entity,
 * with DTOs, centralized exception handling, a uniform API response envelope,
 * request logging and OpenAPI documentation.
 */
@SpringBootApplication
public class EmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmsApplication.class, args);
    }
}
