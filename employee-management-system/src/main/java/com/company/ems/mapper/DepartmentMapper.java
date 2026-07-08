package com.company.ems.mapper;

import com.company.ems.dto.request.DepartmentRequest;
import com.company.ems.dto.response.DepartmentResponse;
import com.company.ems.dto.response.DepartmentSummary;
import com.company.ems.model.entity.Department;
import org.springframework.stereotype.Component;

/**
 * Hand-written, explicit mapping between {@link Department} entities and their
 * DTOs. Kept manual (rather than e.g. MapStruct) so the project has zero
 * annotation-processing magic and is easy to step through while learning -
 * swap in MapStruct later if the mapping surface grows.
 */
@Component
public class DepartmentMapper {

    public Department toEntity(DepartmentRequest request) {
        Department department = new Department();
        department.setName(request.getName());
        department.setCode(request.getCode());
        department.setDescription(request.getDescription());
        return department;
    }

    public void updateEntity(Department department, DepartmentRequest request) {
        department.setName(request.getName());
        department.setCode(request.getCode());
        department.setDescription(request.getDescription());
    }

    public DepartmentResponse toResponse(Department department) {
        return DepartmentResponse.builder()
                .id(department.getId())
                .name(department.getName())
                .code(department.getCode())
                .description(department.getDescription())
                .createdAt(department.getCreatedAt())
                .updatedAt(department.getUpdatedAt())
                .build();
    }

    public DepartmentSummary toSummary(Department department) {
        return DepartmentSummary.builder()
                .id(department.getId())
                .name(department.getName())
                .code(department.getCode())
                .build();
    }
}
