package com.company.ems.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Small payload used for the partial-update (PATCH) status endpoint, used to
 * activate or deactivate an employee without resubmitting the whole record.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeStatusUpdateRequest {

    @NotNull(message = "active is required")
    private Boolean active;
}
