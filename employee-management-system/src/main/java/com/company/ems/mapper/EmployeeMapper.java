package com.company.ems.mapper;

import com.company.ems.dto.request.EmployeeRequest;
import com.company.ems.dto.response.EmployeeResponse;
import com.company.ems.model.entity.Department;
import com.company.ems.model.entity.Employee;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    private final DepartmentMapper departmentMapper;

    public EmployeeMapper(DepartmentMapper departmentMapper) {
        this.departmentMapper = departmentMapper;
    }

    public Employee toEntity(EmployeeRequest request, Department department) {
        Employee employee = new Employee();
        applyRequest(employee, request, department);
        return employee;
    }

    public void updateEntity(Employee employee, EmployeeRequest request, Department department) {
        applyRequest(employee, request, department);
    }

    private void applyRequest(Employee employee, EmployeeRequest request, Department department) {
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setPhoneNumber(request.getPhoneNumber());
        employee.setDesignation(request.getDesignation());
        employee.setSalary(request.getSalary());
        employee.setDateOfJoining(request.getDateOfJoining());
        employee.setDepartment(department);
    }

    public EmployeeResponse toResponse(Employee employee) {
        return EmployeeResponse.builder()
                .id(employee.getId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .fullName(employee.getFirstName() + " " + employee.getLastName())
                .email(employee.getEmail())
                .phoneNumber(employee.getPhoneNumber())
                .designation(employee.getDesignation())
                .salary(employee.getSalary())
                .dateOfJoining(employee.getDateOfJoining())
                .active(employee.isActive())
                .department(departmentMapper.toSummary(employee.getDepartment()))
                .createdAt(employee.getCreatedAt())
                .updatedAt(employee.getUpdatedAt())
                .build();
    }
}
