package com.example.customer.customer.service;

import com.example.customer.customer.dto.CustomerMapper;
import com.example.customer.customer.dto.CustomerRequest;
import com.example.customer.customer.dto.CustomerResponse;
import com.example.customer.customer.models.Address;
import com.example.customer.customer.models.Customer;
import com.example.customer.customer.repository.CustomerRepository;
import com.example.customer.exceptions.CustomerNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
    Customer customer2;
    CustomerResponse response2;



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
        this.customer2 = Customer.builder()
                .id("456")
                .firstName("Jane")
                .lastName("Smith")
                .email("jane@test.com")
                .build();

        this.response2 = CustomerResponse.builder()
                .id("456")
                .firstName("Jane")
                .lastName("Smith")
                .email("jane@test.com")
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

    @Nested
    @DisplayName("Testing Update Customer Method ...")
    class UpdateCustomer{

        @Test
        @DisplayName("Should merge customer successfuly when customer exists")
        void shouldUpdateCustumer_WhenCustumerExists(){
            // Given
            CustomerRequest updateRequest = CustomerRequest.builder()
                    .id("123")
                    .firstName("Salma")
                    .email("Salma@test.com")
                    .build();
            when(repository.findById("123")).thenReturn(Optional.ofNullable(customer));
            when(repository.save(any(Customer.class))).thenReturn(customer);
            // When
            customerService.updateCustomer(updateRequest);
            // Then
            verify(repository,times(1)).findById("123");
            verify(repository,times(1)).save(customer);
            assertEquals("Salma",customer.getFirstName());
            assertEquals("Salma@test.com",customer.getEmail());
        }

        @Test
        @DisplayName("Should throw CustomerNotFoundException when customer not found ")
        void shouldThrowException_WhenCustomerNotFound(){
            // Given
            CustomerRequest updateRequest = CustomerRequest.builder()
                    .id("999")
                    .firstName("NonExistent")
                    .email("notfound@test.com")
                    .build();

            when(repository.findById("999")).thenReturn(Optional.empty());
            // When & Then
            CustomerNotFoundException exception = assertThrows(
                    CustomerNotFoundException.class,
                    ()-> customerService.updateCustomer(updateRequest)
            );
            // verify
            assertTrue(exception.getMsg().contains("No customer found with the provided ID: 999"));
            verify(repository,times(1)).findById("999");
            verify(repository,never()).save(any(Customer.class));

        }

        @Test
        @DisplayName("Should update only not-null fields")
        void shouldUpdateOnlyNonNullFields(){
            // Given
            CustomerRequest partialUpdate = CustomerRequest.builder()
                    .id("123")
                    .firstName("Jane")
                    .build();

            when(repository.findById("123")).thenReturn(Optional.of(customer));
            when(repository.save(any(Customer.class))).thenReturn(customer);

            // When
            customerService.updateCustomer(partialUpdate);

            // Then
            assertEquals("Jane", customer.getFirstName());
            assertEquals("john@test.com", customer.getEmail()); // Should remain unchanged
            assertEquals("Doe", customer.getLastName()); // Should remain unchanged
            verify(repository, times(1)).save(customer);

        }

    }

    @Nested
    @DisplayName("Testing Delete Customer Method ...")
    class DeleteCustomer{

        @Test
        @DisplayName("Should delete customer successfully")
        void shouldDeleteCustomer_successfully() {
            // Given
            String customerId = "123";
            doNothing().when(repository).deleteById(customerId);

            // When
            customerService.deleteCustomer(customerId);

            // Then
            verify(repository, times(1)).deleteById(customerId);
        }

        @Test
        @DisplayName("Should handle delete when customer does not exist")
        void shouldHandleDelete_whenCustomerNotExists() {
            // Given
            String customerId = "999";
            doNothing().when(repository).deleteById(customerId);

            // When
            customerService.deleteCustomer(customerId);

            // Then
            verify(repository, times(1)).deleteById(customerId);
        }

    }

    @Nested
    @DisplayName("Testing Get All Customers Method")
    class GetAllCustomers{
        @Test
        @DisplayName("Should return all customers successfully")
        void shouldReturnAllCustomers() {
            // Given
            List<Customer> customers = Arrays.asList(customer, customer2);

            when(repository.findAll()).thenReturn(customers);
            when(mapper.fromCustomer(customer)).thenReturn(customerResponse);
            when(mapper.fromCustomer(customer2)).thenReturn(response2);

            // When
            List<CustomerResponse> result = customerService.getAllCustomers();

            // Then
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals("123", result.get(0).id());
            assertEquals("456", result.get(1).id());
            verify(repository, times(1)).findAll();
            verify(mapper, times(2)).fromCustomer(any(Customer.class));
        }

        @Test
        @DisplayName("Should return empty list when no customers exist")
        void shouldReturnEmptyList_whenNoCustomers() {
            // Given
            when(repository.findAll()).thenReturn(Arrays.asList());

            // When
            List<CustomerResponse> result = customerService.getAllCustomers();

            // Then
            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(repository, times(1)).findAll();
        }
    }
    @Nested
    @DisplayName("Testing Find Customer By ID Method")
    class FindCustomerById {

        @Test
        @DisplayName("Should return customer when found by ID")
        void shouldReturnCustomer_whenFoundById() {
            // Given
            when(repository.findById("123")).thenReturn(Optional.of(customer));
            when(mapper.fromCustomer(customer)).thenReturn(customerResponse);

            // When
            CustomerResponse result = customerService.findByid("123");

            // Then
            assertNotNull(result);
            assertEquals("123", result.id());
            assertEquals("John", result.firstName());
            assertEquals("john@test.com", result.email());
            verify(repository, times(1)).findById("123");
            verify(mapper, times(1)).fromCustomer(customer);
        }

        @Test
        @DisplayName("Should throw CustomerNotFoundException when customer not found")
        void shouldThrowException_whenCustomerNotFoundById() {
            // Given
            when(repository.findById("999")).thenReturn(Optional.empty());

            // When & Then
            CustomerNotFoundException exception = assertThrows(
                    CustomerNotFoundException.class,
                    () -> customerService.findByid("999")
            );

            assertTrue(exception.getMsg().contains("No customer found with the provided ID: 999"));
            verify(repository, times(1)).findById("999");
            verify(mapper, never()).fromCustomer(any(Customer.class));
        }
    }

    @Nested
    @DisplayName("Testing Exist By ID Method")
    class ExistById {

        @Test
        @DisplayName("Should return true when customer exists")
        void shouldReturnTrue_whenCustomerExists() {
            // Given
            when(repository.findById("123")).thenReturn(Optional.of(customer));

            // When
            boolean result = customerService.existById("123");

            // Then
            assertTrue(result);
            verify(repository, times(1)).findById("123");
        }

        @Test
        @DisplayName("Should return false when customer does not exist")
        void shouldReturnFalse_whenCustomerNotExists() {
            // Given
            when(repository.findById("999")).thenReturn(Optional.empty());

            // When
            boolean result = customerService.existById("999");

            // Then
            assertFalse(result);
            verify(repository, times(1)).findById("999");
        }
    }


}