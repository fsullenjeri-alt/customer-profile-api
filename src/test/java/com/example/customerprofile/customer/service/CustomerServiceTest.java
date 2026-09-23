package com.example.customerprofile.customer.service;

import com.example.customerprofile.customer.dto.CustomerProfileResponse;
import com.example.customerprofile.customer.dto.CustomerProfileUpdateRequest;
import com.example.customerprofile.customer.entity.Customer;
import com.example.customerprofile.customer.exception.CustomerNotFoundException;
import com.example.customerprofile.customer.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void shouldGetCustomerProfile() {

        Customer customer = new Customer(
                "John Doe",
                "john.doe@example.com",
                "https://example.com/john-doe.jpg"
        );

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        CustomerProfileResponse response =
                customerService.getCustomerProfile(1L);

        assertEquals("John Doe", response.getName());
        assertEquals("john.doe@example.com", response.getEmail());
        assertEquals(
                "https://example.com/john-doe.jpg",
                response.getPhoto()
        );

        verify(customerRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenCustomerDoesNotExist() {

        when(customerRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                CustomerNotFoundException.class,
                () -> customerService.getCustomerProfile(999L)
        );

        verify(customerRepository).findById(999L);
    }

    @Test
    void shouldUpdateCustomerProfile() {

        Customer customer = new Customer(
                "John Doe",
                "john.doe@example.com",
                "https://example.com/john-doe.jpg"
        );

        CustomerProfileUpdateRequest request =
                new CustomerProfileUpdateRequest(
                        "Jane Doe",
                        "jane.doe@example.com",
                        "https://example.com/jane-doe.jpg"
                );

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        when(customerRepository.save(customer))
                .thenReturn(customer);

        CustomerProfileResponse response =
                customerService.updateCustomerProfile(1L, request);

        assertEquals("Jane Doe", response.getName());
        assertEquals("jane.doe@example.com", response.getEmail());
        assertEquals(
                "https://example.com/jane-doe.jpg",
                response.getPhoto()
        );

        verify(customerRepository).findById(1L);
        verify(customerRepository).save(customer);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingCustomer() {

        CustomerProfileUpdateRequest request =
                new CustomerProfileUpdateRequest(
                        "Jane Doe",
                        "jane.doe@example.com",
                        "https://example.com/jane-doe.jpg"
                );

        when(customerRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                CustomerNotFoundException.class,
                () -> customerService.updateCustomerProfile(999L, request)
        );

        verify(customerRepository).findById(999L);
        verify(customerRepository, never()).save(any());
    }
}