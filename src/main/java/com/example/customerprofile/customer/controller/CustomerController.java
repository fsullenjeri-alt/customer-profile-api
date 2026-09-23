package com.example.customerprofile.customer.controller;

import com.example.customerprofile.customer.dto.CustomerProfileResponse;
import com.example.customerprofile.customer.service.CustomerService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/{id}")
    public CustomerProfileResponse getCustomerProfile(@PathVariable Long id) {
        return customerService.getCustomerProfile(id);
    }
}