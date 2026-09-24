package com.example.customerprofile.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class CustomerProfileUpdateRequest {

    @Pattern(regexp = ".*\\S.*", message = "Name must not be blank")
    private String name;

    @Pattern(regexp = ".*\\S.*", message = "Email must not be blank")
    @Email(message = "Email must be valid")
    private String email;

    @Pattern(regexp = ".*\\S.*", message = "Photo must not be blank")
    private String photo;

}
