package com.company.ems.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Lightweight, nested representation of a department, embedded inside an
 * {@link EmployeeResponse} so callers don't need a second round trip just to
 * see which department an employee belongs to.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentSummary {

    private Long id;
    private String name;
    private String code;
}
