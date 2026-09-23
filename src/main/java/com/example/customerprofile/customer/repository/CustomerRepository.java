package com.example.customerprofile.customer.repository;

import com.example.customerprofile.customer.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}