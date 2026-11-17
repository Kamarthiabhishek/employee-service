package com.abhi.employeemanagement;


import com.abhi.employeemanagement.entity.Employee;
import com.abhi.employeemanagement.entity.dto.EmployeeRequest;
import com.abhi.employeemanagement.entity.dto.EmployeeResponse;
import com.abhi.employeemanagement.entity.enums.EmployeeStatus;
import com.abhi.employeemanagement.exception.EmployeeAlreadyExists;
import com.abhi.employeemanagement.exception.EmployeeDoesntExists;
import com.abhi.employeemanagement.kafka.EmployeeKafkaProducer;
import com.abhi.employeemanagement.repository.EmployeeRepository;
import com.abhi.employeemanagement.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTest {

    @Mock
    EmployeeRepository employeeRepository;
    @InjectMocks
    EmployeeService employeeService;
    @Mock
    EmployeeKafkaProducer kafkaProducer;


    private Employee employee;
    private EmployeeResponse response;
    private EmployeeRequest request;

    @BeforeEach
    void arrange(){
        //Arrange
        request = new EmployeeRequest("Abhi","Kamarthi","abhi@example.com",1L,"IT","Platform Engineer",1.1, LocalDate.now());
        employee = new Employee("Abhi","Kamarthi","abhi@example.com",1L,"IT","Platform Engineer",1.1, LocalDate.now(), EmployeeStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now());
        employee.setId(1);
    }

    @Test
    void shouldCreateEmployeeWithEmail(){
        //Mock the repositories
        when(employeeRepository.existsByEmail(request.email())).thenReturn(false);
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        //Act
        EmployeeResponse response = employeeService.createEmployee(request);

        //Assert
        assertThat(response).isNotNull();
        assertThat(response.firstName()).isEqualTo("Abhi");
        assertThat(response.lastName()).isEqualTo("Kamarthi");
        assertThat(response.email()).isEqualTo("abhi@example.com");
        assertThat(response.status()).isEqualTo(EmployeeStatus.ACTIVE);

        //Verify
        verify(employeeRepository, times(1)).existsByEmail(request.email());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void shouldNotCreateEmployeeWithEmail(){
        //Mock the repository
        when(employeeRepository.existsByEmail(request.email())).thenReturn(true);

        //Act & Assert
        assertThatThrownBy(() -> employeeService.createEmployee(request)).isInstanceOf(EmployeeAlreadyExists.class).hasMessageContaining("Employee Already Exists with Email : " +request.email());

        //verify
        verify(employeeRepository, times(1)).existsByEmail(request.email());
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void shouldUpdateEmployeeWithEmail(){
        //Mock the repositories
        when(employeeRepository.findById(any(Integer.class))).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        //Act
        EmployeeResponse response = employeeService.updateEmployee(1);

        //Assert
        assertThat(response).isNotNull();
        assertThat(response.status()).isEqualTo(EmployeeStatus.ON_LEAVE);
        assertThat(response.firstName()).isEqualTo("Abhi");

        //Verify
        verify(employeeRepository, times(1)).findById(any(Integer.class));
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void shouldThrowExceptionWithEmail(){
        //Mock the repository
        when(employeeRepository.findById(any(Integer.class))).thenReturn(Optional.empty());

        //Act & Assert
        assertThatThrownBy(() -> employeeService.updateEmployee(1)).isInstanceOf(EmployeeDoesntExists.class).hasMessageContaining("Employee doesn't exists with ID : 1");

        //Verify
        verify(employeeRepository, times(1)).findById(any(Integer.class));
        verify(employeeRepository,never()).save(any(Employee.class));
    }
}



