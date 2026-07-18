package com.company.ems.controller;

import com.company.ems.dto.request.EmployeeRequest;
import com.company.ems.dto.response.DepartmentSummary;
import com.company.ems.dto.response.EmployeeResponse;
import com.company.ems.exception.ResourceNotFoundException;
import com.company.ems.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmployeeService employeeService;

    @Test
    void getById_shouldReturn200AndEmployee_whenFound() throws Exception {
        EmployeeResponse response = EmployeeResponse.builder()
                .id(1L)
                .firstName("Ada")
                .lastName("Lovelace")
                .fullName("Ada Lovelace")
                .email("ada.lovelace@example.com")
                .designation("Principal Engineer")
                .salary(new BigDecimal("125000.00"))
                .dateOfJoining(LocalDate.of(2024, 1, 15))
                .active(true)
                .department(DepartmentSummary.builder().id(1L).name("Engineering").code("ENG").build())
                .build();
        when(employeeService.getById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.email").value("ada.lovelace@example.com"));
    }

    @Test
    void getById_shouldReturn404_whenEmployeeMissing() throws Exception {
        when(employeeService.getById(99L)).thenThrow(new ResourceNotFoundException("Employee not found with id: 99"));

        mockMvc.perform(get("/api/v1/employees/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("RESOURCE_NOT_FOUND"));
    }

    @Test
    void create_shouldReturn400_whenRequiredFieldsAreMissing() throws Exception {
        EmployeeRequest invalidRequest = EmployeeRequest.builder().build();

        mockMvc.perform(post("/api/v1/employees")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("VALIDATION_FAILED"));
    }

    @Test
    void create_shouldReturn201_whenRequestIsValid() throws Exception {
        EmployeeRequest request = EmployeeRequest.builder()
                .firstName("Ada")
                .lastName("Lovelace")
                .email("ada.lovelace@example.com")
                .designation("Principal Engineer")
                .salary(new BigDecimal("125000.00"))
                .dateOfJoining(LocalDate.of(2024, 1, 15))
                .departmentId(1L)
                .build();
        EmployeeResponse response = EmployeeResponse.builder().id(1L).email(request.getEmail()).build();
        when(employeeService.create(any(EmployeeRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/employees")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(1));
    }
}
