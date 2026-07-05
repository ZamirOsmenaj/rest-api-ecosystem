package com.company.ems.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * A single employee record. Each employee is linked to a {@link Department}
 * via a many-to-one association, loaded lazily to avoid pulling department
 * data on every employee read unless it is actually needed.
 */
@Entity
@Table(
        name = "employees",
        uniqueConstraints = @UniqueConstraint(name = "uk_employee_email", columnNames = "email")
)
@Getter
@Setter
@NoArgsConstructor
public class Employee extends BaseEntity {

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(nullable = false, length = 100)
    private String designation;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal salary;

    @Column(name = "date_of_joining", nullable = false)
    private LocalDate dateOfJoining;

    @Column(nullable = false)
    private boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "department_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_employee_department"))
    private Department department;
}
