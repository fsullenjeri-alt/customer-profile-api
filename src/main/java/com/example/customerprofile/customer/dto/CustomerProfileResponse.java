package com.example.customerprofile.customer.dto;

public class CustomerProfileResponse {

    private Long id;
    private String name;
    private String email;
    private String photo;

    public CustomerProfileResponse(Long id, String name, String email, String photo) {
        this.id = id;
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
}