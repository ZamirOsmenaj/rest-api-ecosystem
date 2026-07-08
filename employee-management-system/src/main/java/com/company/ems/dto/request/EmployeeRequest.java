package com.company.ems.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Payload used to create or fully update an employee.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeRequest {

    @NotBlank(message = "firstName is required")
    @Size(max = 50, message = "firstName must not exceed 50 characters")
    private String firstName;

    @NotBlank(message = "lastName is required")
    @Size(max = 50, message = "lastName must not exceed 50 characters")
    private String lastName;

    @NotBlank(message = "email is required")
    @Email(message = "email must be a valid email address")
    @Size(max = 150, message = "email must not exceed 150 characters")
    private String email;

    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "phoneNumber must contain 7-15 digits, optionally prefixed with '+'")
    private String phoneNumber;

    @NotBlank(message = "designation is required")
    @Size(max = 100, message = "designation must not exceed 100 characters")
    private String designation;

    @NotNull(message = "salary is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "salary must be greater than 0")
    private BigDecimal salary;

    @NotNull(message = "dateOfJoining is required")
    @PastOrPresent(message = "dateOfJoining cannot be in the future")
    private LocalDate dateOfJoining;

    @NotNull(message = "departmentId is required")
    private Long departmentId;
}
