package com.company.ems.repository;

import com.company.ems.model.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByDepartmentId(Long departmentId);

    /**
     * Dynamic filter used by the employee listing endpoint. Each filter is
     * optional: passing {@code null} for a parameter simply skips that
     * predicate, which keeps a single query reusable for "list all",
     * "filter by department", "filter by status" and "search by name/email"
     * without building a query string at runtime.
     */
    @Query("""
            SELECT e FROM Employee e
            WHERE (:departmentId IS NULL OR e.department.id = :departmentId)
              AND (:active IS NULL OR e.active = :active)
              AND (:search IS NULL
                   OR LOWER(e.firstName) LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(e.lastName)  LIKE LOWER(CONCAT('%', :search, '%'))
                   OR LOWER(e.email)     LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<Employee> search(@Param("departmentId") Long departmentId,
                           @Param("active") Boolean active,
                           @Param("search") String search,
                           Pageable pageable);
}
