package com.example.customerprofile.customer.service;

import com.example.customerprofile.customer.dto.CustomerProfileCreateRequest;
import com.example.customerprofile.customer.dto.CustomerProfileUpdateRequest;
import com.example.customerprofile.customer.dto.CustomerProfileResponse;
import com.example.customerprofile.customer.entity.Customers;
import com.example.customerprofile.customer.exception.CustomerNotFoundException;
import com.example.customerprofile.customer.mapper.CustomerMapper;
import com.example.customerprofile.customer.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import java.util.List;

@Service
@AllArgsConstructor
public class CustomerService {

    @Autowired
    private final CustomerRepository customerRepository;
    @Autowired
    private final CustomerMapper customerMapper;

    public List<CustomerProfileResponse> getAllCustomerProfiles() {
        return customerMapper.toResponseList(customerRepository.findAll());
    }

    @Cacheable(value = "customerProfiles", key = "#id")
    public CustomerProfileResponse getCustomerProfile(Long id) {

        Customers customers = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        return customerMapper.toResponse(customers);
    }

    public CustomerProfileResponse createCustomerProfile(CustomerProfileCreateRequest request) {

        Customers savedCustomers = customerRepository.save(customerMapper.toEntity(request));

        return customerMapper.toResponse(savedCustomers);
    }

    @CacheEvict(value = "customerProfiles", key = "#id")
    public CustomerProfileResponse updateCustomerProfile(
            Long id,
            CustomerProfileUpdateRequest request) {

        Customers customers = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        customerMapper.updateFromRequest(request, customers);

        Customers updatedCustomers = customerRepository.save(customers);

        return customerMapper.toResponse(updatedCustomers);
    }

    @CacheEvict(value = "customerProfiles", key = "#id")
    public void deleteCustomerProfile(Long id) {

        Customers customers = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        customerRepository.delete(customers);
    }

}