package com.company.ems.controller;

import com.company.ems.dto.request.EmployeeRequest;
import com.company.ems.dto.request.EmployeeStatusUpdateRequest;
import com.company.ems.dto.response.EmployeeResponse;
import com.company.ems.dto.response.PageResponse;
import com.company.ems.response.ApiResponse;
import com.company.ems.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
@Tag(name = "Employees", description = "CRUD and search operations for employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    @Operation(summary = "Create a new employee")
    public ResponseEntity<ApiResponse<EmployeeResponse>> create(@Valid @RequestBody EmployeeRequest request) {
        EmployeeResponse created = employeeService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Employee created successfully", created));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an employee by id")
    public ResponseEntity<ApiResponse<EmployeeResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(employeeService.getById(id)));
    }

    @GetMapping
    @Operation(summary = "Search/list employees with optional filters, pagination and sorting",
            description = "Supports filtering by departmentId and active status, free-text search across "
                    + "first name / last name / email, and standard Spring pagination params "
                    + "(page, size, sort), e.g. ?page=0&size=10&sort=lastName,asc")
    public ResponseEntity<ApiResponse<PageResponse<EmployeeResponse>>> search(
            @RequestParam(required = false) @Parameter(description = "Filter by department id") Long departmentId,
            @RequestParam(required = false) @Parameter(description = "Filter by active status") Boolean active,
            @RequestParam(required = false) @Parameter(description = "Free-text search across name and email") String search,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {
        PageResponse<EmployeeResponse> result = employeeService.search(departmentId, active, search, pageable);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Fully update an existing employee")
    public ResponseEntity<ApiResponse<EmployeeResponse>> update(@PathVariable Long id,
                                                                 @Valid @RequestBody EmployeeRequest request) {
        EmployeeResponse updated = employeeService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Employee updated successfully", updated));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Activate or deactivate an employee without resubmitting the full record")
    public ResponseEntity<ApiResponse<EmployeeResponse>> updateStatus(@PathVariable Long id,
                                                                       @Valid @RequestBody EmployeeStatusUpdateRequest request) {
        EmployeeResponse updated = employeeService.updateStatus(id, request.getActive());
        return ResponseEntity.ok(ApiResponse.success("Employee status updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an employee")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        employeeService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Employee deleted successfully", null));
    }
}
