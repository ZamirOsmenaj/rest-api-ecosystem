package com.company.ems.service;

import com.company.ems.dto.request.EmployeeRequest;
import com.company.ems.exception.DuplicateResourceException;
import com.company.ems.exception.ResourceNotFoundException;
import com.company.ems.mapper.EmployeeMapper;
import com.company.ems.model.entity.Department;
import com.company.ems.model.entity.Employee;
import com.company.ems.repository.DepartmentRepository;
import com.company.ems.repository.EmployeeRepository;
import com.company.ems.service.impl.EmployeeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private EmployeeRequest validRequest;
    private Department department;

    @BeforeEach
    void setUp() {
        department = new Department();
        department.setId(1L);
        department.setName("Engineering");
        department.setCode("ENG");

        validRequest = EmployeeRequest.builder()
                .firstName("Ada")
                .lastName("Lovelace")
                .email("ada.lovelace@example.com")
                .phoneNumber("+15551234567")
                .designation("Principal Engineer")
                .salary(new BigDecimal("125000.00"))
                .dateOfJoining(LocalDate.of(2024, 1, 15))
                .departmentId(1L)
                .build();

        lenient().when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
    }

    @Test
    void create_shouldThrowDuplicateResourceException_whenEmailAlreadyExists() {
        when(employeeRepository.existsByEmail(validRequest.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> employeeService.create(validRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining(validRequest.getEmail());
    }

    @Test
    void create_shouldThrowResourceNotFoundException_whenDepartmentDoesNotExist() {
        when(employeeRepository.existsByEmail(validRequest.getEmail())).thenReturn(false);
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.create(validRequest))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_shouldPersistEmployee_whenRequestIsValid() {
        when(employeeRepository.existsByEmail(validRequest.getEmail())).thenReturn(false);
        Employee mappedEntity = new Employee();
        when(employeeMapper.toEntity(validRequest, department)).thenReturn(mappedEntity);
        when(employeeRepository.save(mappedEntity)).thenReturn(mappedEntity);

        employeeService.create(validRequest);

        verify(employeeRepository).save(mappedEntity);
        verify(employeeMapper).toResponse(mappedEntity);
    }

    @Test
    void getById_shouldThrowResourceNotFoundException_whenMissing() {
        when(employeeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> employeeService.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void updateStatus_shouldFlipActiveFlag() {
        Employee employee = new Employee();
        employee.setId(5L);
        employee.setActive(true);
        when(employeeRepository.findById(5L)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        employeeService.updateStatus(5L, false);

        assertThat(employee.isActive()).isFalse();
        verify(employeeRepository).save(employee);
    }
}
