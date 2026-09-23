package com.example.customerprofile.customer.controller;

import com.example.customerprofile.customer.dto.CustomerProfileResponse;
import com.example.customerprofile.customer.dto.CustomerProfileUpdateRequest;
import com.example.customerprofile.customer.exception.CustomerNotFoundException;
import com.example.customerprofile.customer.exception.GlobalExceptionHandler;
import com.example.customerprofile.customer.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
@Import({
        CustomerController.class,
        GlobalExceptionHandler.class,
        CustomerControllerTest.TestConfig.class
})
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerService customerService;

    @Test
    void shouldGetCustomerProfile() throws Exception {

        CustomerProfileResponse response =
                new CustomerProfileResponse(
                        1L,
                        "John Doe",
                        "john.doe@example.com",
                        "https://example.com/john-doe.jpg"
                );

        when(customerService.getCustomerProfile(1L))
                .thenReturn(response);

        mockMvc.perform(get("/api/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.photo")
                        .value("https://example.com/john-doe.jpg"));
    }

    @Test
    void shouldReturnNotFoundWhenCustomerDoesNotExist() throws Exception {

        when(customerService.getCustomerProfile(999L))
                .thenThrow(new CustomerNotFoundException(999L));

        mockMvc.perform(get("/api/customers/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error")
                        .value("Customer with id 999 not found"));
    }

    @Test
    void shouldUpdateCustomerProfile() throws Exception {

        CustomerProfileUpdateRequest request =
                new CustomerProfileUpdateRequest(
                        "Jane Doe",
                        "jane.doe@example.com",
                        "https://example.com/jane-doe.jpg"
                );

        CustomerProfileResponse response =
                new CustomerProfileResponse(
                        1L,
                        "Jane Doe",
                        "jane.doe@example.com",
                        "https://example.com/jane-doe.jpg"
                );

        when(customerService.updateCustomerProfile(eq(1L), any()))
                .thenReturn(response);

        mockMvc.perform(
                        put("/api/customers/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Jane Doe"))
                .andExpect(jsonPath("$.email")
                        .value("jane.doe@example.com"))
                .andExpect(jsonPath("$.photo")
                        .value("https://example.com/jane-doe.jpg"));
    }

    @Test
    void shouldReturnBadRequestForInvalidUpdate() throws Exception {

        String invalidRequest = """
                {
                    "name": "",
                    "email": "not-an-email",
                    "photo": ""
                }
                """;

        mockMvc.perform(
                        put("/api/customers/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(invalidRequest)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name")
                        .value("Name is required"))
                .andExpect(jsonPath("$.email")
                        .value("Email must be valid"))
                .andExpect(jsonPath("$.photo")
                        .value("Photo is required"));
    }

    @Configuration
    static class TestConfig {

        @Bean
        CustomerService customerService() {
            return Mockito.mock(CustomerService.class);
        }
    }
}