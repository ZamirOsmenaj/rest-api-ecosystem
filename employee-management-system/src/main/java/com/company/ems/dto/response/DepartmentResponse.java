package com.company.ems.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * What we return to clients for a department - never expose the JPA entity
 * directly, so internal persistence concerns (e.g. version column) stay hidden.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentResponse {

    private Long id;
    private String name;
    private String code;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;
}
