package com.company.ems.repository;

import com.company.ems.model.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {

    Optional<Department> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByName(String name);
}
