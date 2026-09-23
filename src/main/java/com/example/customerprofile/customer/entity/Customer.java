package com.example.customerprofile.customer.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String photo;

    protected Customer() {
    }

    public Customer(String name, String email, String photo) {
        this.name = name;
        this.email = email;
        this.photo = photo;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoto() {
        return photo;
    }

    public void updateProfile(String name, String email, String photo) {
        this.name = name;
        this.email = email;
        this.photo = photo;
    }
}