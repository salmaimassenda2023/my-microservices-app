package com.example.customer.customer.service;

import com.example.customer.customer.dto.CustomerMapper;
import com.example.customer.customer.dto.CustomerRequest;
import com.example.customer.customer.dto.CustomerResponse;
import com.example.customer.customer.models.Address;
import com.example.customer.customer.models.Customer;
import com.example.customer.customer.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@DisplayName("Start Customer Service Test")
class CustomerServiceTest {

    // dependencies injection
    @Mock
    private  CustomerRepository repository;
    @Mock
    private  CustomerMapper mapper ;
    // instance of class object of test
    @InjectMocks
    private  CustomerService customerService;
    // object that i need work with
    CustomerRequest customerRequest;
    CustomerResponse customerResponse;
    Customer customer ;



    @BeforeEach
    void setUp() {
        this.customerRequest = CustomerRequest.builder()
                .id("123")
                .firstName("John")
                .lastName("Doe")
                .email("john@test.com")
                .adress(new Address("123 Main St", "Paris", "75001"))
                .build();

        this.customerResponse = CustomerResponse.builder()
                .id("123")
                .firstName("John")
                .lastName("Doe")
                .email("john@test.com")
                .adress(new Address("123 Main St", "Paris", "75001"))
                .build();

        this.customer = Customer.builder()
                .id("123")
                .firstName("John")
                .lastName("Doe")
                .email("john@test.com")
                .adress(new Address("123 Main St", "Paris", "75001"))
                .build();
    }

    @Nested
    @DisplayName("Testing Create Customer Method ...")
    class CreateCustomer {


        @Test
        @DisplayName("Should create customer successefuly and return ID when valid data !")
        public void shouldCreateCustomer_whenValidData (){
            // given
            when(mapper.toCustomer(customerRequest)).thenReturn(customer);
            when(repository.save(any(Customer.class))).thenReturn(customer);
            // when
            String customerId = customerService.createCustomer(customerRequest);
            // THEN - Verify results
            assertNotNull(customerId);
            assertEquals("123",customerId);
            // Verify interactions
            verify(mapper,times(1)).toCustomer(customerRequest);
            verify(repository,times(1)).save(customer);
        }

        @DisplayName("Should handle repository save failure!")
        public void shouldThrowException_whenRepositorySaveFails(){
            // given - database faild
            when(mapper.toCustomer(customerRequest)).thenReturn(customer);
            when(repository.save(any(Customer.class))).thenThrow(
                    new RuntimeException("Database connection failed") );
            // When & Then
            assertThrows(RuntimeException.class,
                    ()->{ customerService.createCustomer(customerRequest);});
            // verify
            verify(mapper, times(1)).toCustomer(customerRequest);
            verify(repository, times(1)).save(customer);
        }




    }

}