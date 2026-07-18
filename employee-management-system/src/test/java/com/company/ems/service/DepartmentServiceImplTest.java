package com.company.ems.service;

import com.company.ems.dto.request.DepartmentRequest;
import com.company.ems.exception.BusinessRuleViolationException;
import com.company.ems.exception.DuplicateResourceException;
import com.company.ems.mapper.DepartmentMapper;
import com.company.ems.model.entity.Department;
import com.company.ems.repository.DepartmentRepository;
import com.company.ems.repository.EmployeeRepository;
import com.company.ems.service.impl.DepartmentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceImplTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentMapper departmentMapper;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    @Test
    void create_shouldThrowDuplicateResourceException_whenCodeAlreadyExists() {
        DepartmentRequest request = DepartmentRequest.builder()
                .name("Engineering")
                .code("ENG")
                .description("Builds the product")
                .build();

        Department existing = new Department();
        existing.setId(1L);
        existing.setCode("ENG");
        when(departmentRepository.findByCode("ENG")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> departmentService.create(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("ENG");
    }

    @Test
    void delete_shouldThrowBusinessRuleViolationException_whenDepartmentHasEmployees() {
        Department department = new Department();
        department.setId(1L);
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(employeeRepository.existsByDepartmentId(1L)).thenReturn(true);

        assertThatThrownBy(() -> departmentService.delete(1L))
                .isInstanceOf(BusinessRuleViolationException.class);
    }
}
