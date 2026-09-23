package com.example.customerprofile.customer.service;
import com.example.customerprofile.customer.dto.CustomerProfileUpdateRequest;
import com.example.customerprofile.customer.dto.CustomerProfileResponse;
import com.example.customerprofile.customer.entity.Customer;
import com.example.customerprofile.customer.exception.CustomerNotFoundException;
import com.example.customerprofile.customer.repository.CustomerRepository;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public CustomerProfileResponse getCustomerProfile(Long id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        return new CustomerProfileResponse(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhoto()
        );
    }

    public CustomerProfileResponse updateCustomerProfile(
            Long id,
            CustomerProfileUpdateRequest request) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new CustomerNotFoundException(id));

        customer.updateProfile(
                request.getName(),
                request.getEmail(),
                request.getPhoto()
        );

        Customer updatedCustomer = customerRepository.save(customer);

        return new CustomerProfileResponse(
                updatedCustomer.getId(),
                updatedCustomer.getName(),
                updatedCustomer.getEmail(),
                updatedCustomer.getPhoto()
        );
    }

}