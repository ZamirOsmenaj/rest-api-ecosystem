package com.company.ems.service.impl;

import com.company.ems.dto.request.EmployeeRequest;
import com.company.ems.dto.response.EmployeeResponse;
import com.company.ems.dto.response.PageResponse;
import com.company.ems.exception.DuplicateResourceException;
import com.company.ems.exception.ResourceNotFoundException;
import com.company.ems.mapper.EmployeeMapper;
import com.company.ems.model.entity.Department;
import com.company.ems.model.entity.Employee;
import com.company.ems.repository.DepartmentRepository;
import com.company.ems.repository.EmployeeRepository;
import com.company.ems.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeMapper employeeMapper;

    @Override
    @Transactional
    public EmployeeResponse create(EmployeeRequest request) {
        log.info("Creating employee with email='{}'", request.getEmail());

        if (employeeRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Employee already exists with email: " + request.getEmail());
        }
        Department department = findDepartmentOrThrow(request.getDepartmentId());

        Employee saved = employeeRepository.save(employeeMapper.toEntity(request, department));
        log.info("Employee created id={} email='{}'", saved.getId(), saved.getEmail());
        return employeeMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeResponse getById(Long id) {
        return employeeMapper.toResponse(findEmployeeOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<EmployeeResponse> search(Long departmentId, Boolean active, String search, Pageable pageable) {
        Page<Employee> page = employeeRepository.search(departmentId, active, normalize(search), pageable);
        return PageResponse.from(page, employeeMapper::toResponse);
    }

    @Override
    @Transactional
    public EmployeeResponse update(Long id, EmployeeRequest request) {
        Employee employee = findEmployeeOrThrow(id);

        if (!employee.getEmail().equalsIgnoreCase(request.getEmail())
                && employeeRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Employee already exists with email: " + request.getEmail());
        }
        Department department = findDepartmentOrThrow(request.getDepartmentId());

        employeeMapper.updateEntity(employee, request, department);
        Employee updated = employeeRepository.save(employee);
        log.info("Employee updated id={}", id);
        return employeeMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public EmployeeResponse updateStatus(Long id, boolean active) {
        Employee employee = findEmployeeOrThrow(id);
        employee.setActive(active);
        Employee updated = employeeRepository.save(employee);
        log.info("Employee id={} status changed to active={}", id, active);
        return employeeMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Employee employee = findEmployeeOrThrow(id);
        employeeRepository.delete(employee);
        log.info("Employee deleted id={}", id);
    }

    private Employee findEmployeeOrThrow(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with id: " + id));
    }

    private Department findDepartmentOrThrow(Long departmentId) {
        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + departmentId));
    }

    private String normalize(String search) {
        return (search == null || search.isBlank()) ? null : search.trim();
    }
}
