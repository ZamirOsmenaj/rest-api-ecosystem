package com.company.ems.service;

import com.company.ems.dto.request.EmployeeRequest;
import com.company.ems.dto.response.EmployeeResponse;
import com.company.ems.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface EmployeeService {

    EmployeeResponse create(EmployeeRequest request);

    EmployeeResponse getById(Long id);

    PageResponse<EmployeeResponse> search(Long departmentId, Boolean active, String search, Pageable pageable);

    EmployeeResponse update(Long id, EmployeeRequest request);

    EmployeeResponse updateStatus(Long id, boolean active);

    void delete(Long id);
}
