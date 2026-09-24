package com.example.customerprofile.customer.service;

import com.example.customerprofile.customer.dto.CustomerProfileCreateRequest;
import com.example.customerprofile.customer.dto.CustomerProfileResponse;
import com.example.customerprofile.customer.dto.CustomerProfileUpdateRequest;
import com.example.customerprofile.customer.entity.Customers;
import com.example.customerprofile.customer.exception.CustomerNotFoundException;
import com.example.customerprofile.customer.mapper.CustomerMapper;
import com.example.customerprofile.customer.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerService customerService;

    private final Customers customer = new Customers(1L, "Ann Smith", "ann@example.com", "ann.png");
    private final CustomerProfileResponse response =
            new CustomerProfileResponse(1L, "Ann Smith", "ann@example.com", "ann.png");

    @Test
    void getAllCustomerProfiles_returnsMappedList() {
        when(customerRepository.findAll()).thenReturn(List.of(customer));
        when(customerMapper.toResponseList(List.of(customer))).thenReturn(List.of(response));

        assertThat(customerService.getAllCustomerProfiles()).containsExactly(response);
    }

    @Test
    void getCustomerProfile_returnsProfile_whenFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerMapper.toResponse(customer)).thenReturn(response);

        assertThat(customerService.getCustomerProfile(1L)).isEqualTo(response);
    }

    @Test
    void getCustomerProfile_throws_whenNotFound() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.getCustomerProfile(99L))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessage("Customer with id 99 not found");
    }

    @Test
    void createCustomerProfile_savesAndReturnsProfile() {
        CustomerProfileCreateRequest request =
                new CustomerProfileCreateRequest("Ann Smith", "ann@example.com", "ann.png");
        Customers newCustomer = new Customers(null, "Ann Smith", "ann@example.com", "ann.png");

        when(customerMapper.toEntity(request)).thenReturn(newCustomer);
        when(customerRepository.save(newCustomer)).thenReturn(customer);
        when(customerMapper.toResponse(customer)).thenReturn(response);

        assertThat(customerService.createCustomerProfile(request)).isEqualTo(response);
    }

    @Test
    void updateCustomerProfile_appliesChangesAndSaves_whenFound() {
        CustomerProfileUpdateRequest request = new CustomerProfileUpdateRequest("Ann Jones", null, null);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.save(customer)).thenReturn(customer);
        when(customerMapper.toResponse(customer)).thenReturn(response);

        assertThat(customerService.updateCustomerProfile(1L, request)).isEqualTo(response);
        verify(customerMapper).updateFromRequest(request, customer);
    }

    @Test
    void updateCustomerProfile_throws_whenNotFound() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());
        CustomerProfileUpdateRequest request = new CustomerProfileUpdateRequest();

        assertThatThrownBy(() -> customerService.updateCustomerProfile(99L, request))
                .isInstanceOf(CustomerNotFoundException.class);
        verify(customerRepository, never()).save(any());
    }

    @Test
    void deleteCustomerProfile_deletes_whenFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        customerService.deleteCustomerProfile(1L);

        verify(customerRepository).delete(customer);
    }

    @Test
    void deleteCustomerProfile_throws_whenNotFound() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.deleteCustomerProfile(99L))
                .isInstanceOf(CustomerNotFoundException.class);
        verify(customerRepository, never()).delete(any());
    }
}
