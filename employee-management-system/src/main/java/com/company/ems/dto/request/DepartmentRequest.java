package com.company.ems.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Payload used to create or update a department.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentRequest {

    @NotBlank(message = "name is required")
    @Size(max = 100, message = "name must not exceed 100 characters")
    private String name;

    @NotBlank(message = "code is required")
    @Size(max = 20, message = "code must not exceed 20 characters")
    @Pattern(regexp = "^[A-Z0-9_-]+$", message = "code must contain only uppercase letters, digits, hyphens or underscores")
    private String code;

    @Size(max = 255, message = "description must not exceed 255 characters")
    private String description;
}
