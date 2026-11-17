package com.abhi.employeemanagement;

import com.abhi.employeemanagement.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.Mockito.*;

@SpringBootTest
class EmployeeManagementApplicationTests {

    @MockitoBean
    private EmployeeRepository employeeRepository;

    @Test
    void contextLoads() {
    }

}
