package com.company.ems.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * A business department/org unit, e.g. Engineering, Human Resources, Finance.
 * Employees belong to exactly one department.
 */
@Entity
@Table(
        name = "departments",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_department_code", columnNames = "code"),
                @UniqueConstraint(name = "uk_department_name", columnNames = "name")
        }
)
@Getter
@Setter
@NoArgsConstructor
public class Department extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    /** Short, uppercase identifier, e.g. "ENG", "HR", "FIN". */
    @Column(nullable = false, length = 20)
    private String code;

    @Column(length = 255)
    private String description;
}
