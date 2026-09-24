package com.example.customerprofile.customer.controller;

import com.example.customerprofile.customer.dto.CustomerProfileCreateRequest;
import com.example.customerprofile.customer.dto.CustomerProfileResponse;
import com.example.customerprofile.customer.dto.CustomerProfileUpdateRequest;
import com.example.customerprofile.customer.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public ResponseEntity<List<CustomerProfileResponse>> getAllCustomerProfiles() {
        return ResponseEntity.ok(customerService.getAllCustomerProfiles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerProfileResponse> getCustomerProfile(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerProfile(id));
    }

    @PostMapping
    public ResponseEntity<CustomerProfileResponse> createCustomerProfile(
            @Valid @RequestBody CustomerProfileCreateRequest request) {

        CustomerProfileResponse response = customerService.createCustomerProfile(request);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<CustomerProfileResponse> updateCustomerProfile(
            @PathVariable Long id,
            @Valid @RequestBody CustomerProfileUpdateRequest request) {

        return ResponseEntity.ok(customerService.updateCustomerProfile(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomerProfile(@PathVariable Long id) {
        customerService.deleteCustomerProfile(id);
        return ResponseEntity.noContent().build();
    }

}
