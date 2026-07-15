package com.company.ems.service.impl;

import com.company.ems.dto.request.DepartmentRequest;
import com.company.ems.dto.response.DepartmentResponse;
import com.company.ems.exception.BusinessRuleViolationException;
import com.company.ems.exception.DuplicateResourceException;
import com.company.ems.exception.ResourceNotFoundException;
import com.company.ems.mapper.DepartmentMapper;
import com.company.ems.model.entity.Department;
import com.company.ems.repository.DepartmentRepository;
import com.company.ems.repository.EmployeeRepository;
import com.company.ems.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentMapper departmentMapper;

    @Override
    @Transactional
    public DepartmentResponse create(DepartmentRequest request) {
        log.info("Creating department with code='{}'", request.getCode());
        validateUniqueness(request, null);

        Department saved = departmentRepository.save(departmentMapper.toEntity(request));
        log.info("Department created id={} code='{}'", saved.getId(), saved.getCode());
        return departmentMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentResponse getById(Long id) {
        return departmentMapper.toResponse(findOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentResponse> getAll() {
        return departmentRepository.findAll().stream()
                .map(departmentMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public DepartmentResponse update(Long id, DepartmentRequest request) {
        Department department = findOrThrow(id);
        validateUniqueness(request, department);

        departmentMapper.updateEntity(department, request);
        Department updated = departmentRepository.save(department);
        log.info("Department updated id={}", id);
        return departmentMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Department department = findOrThrow(id);
        if (employeeRepository.existsByDepartmentId(id)) {
            throw new BusinessRuleViolationException(
                    "Cannot delete department id=" + id + " because it still has employees assigned to it");
        }
        departmentRepository.delete(department);
        log.info("Department deleted id={}", id);
    }

    private Department findOrThrow(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with id: " + id));
    }

    /** Ensures name/code stay unique, ignoring the record currently being updated (if any). */
    private void validateUniqueness(DepartmentRequest request, Department existing) {
        departmentRepository.findByCode(request.getCode()).ifPresent(found -> {
            if (existing == null || !found.getId().equals(existing.getId())) {
                throw new DuplicateResourceException("Department already exists with code: " + request.getCode());
            }
        });
        if (departmentRepository.existsByName(request.getName())
                && (existing == null || !existing.getName().equalsIgnoreCase(request.getName()))) {
            throw new DuplicateResourceException("Department already exists with name: " + request.getName());
        }
    }
}
