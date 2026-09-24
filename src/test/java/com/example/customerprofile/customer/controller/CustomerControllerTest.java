package com.example.customerprofile.customer.controller;

import com.example.customerprofile.customer.dto.CustomerProfileCreateRequest;
import com.example.customerprofile.customer.dto.CustomerProfileResponse;
import com.example.customerprofile.customer.dto.CustomerProfileUpdateRequest;
import com.example.customerprofile.customer.exception.CustomerNotFoundException;
import com.example.customerprofile.customer.exception.GlobalExceptionHandler;
import com.example.customerprofile.customer.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Mock
    private CustomerService customerService;

    @InjectMocks
    private CustomerController customerController;

    private MockMvc mockMvc;

    private final CustomerProfileResponse response =
            new CustomerProfileResponse(1L, "Ann Smith", "ann@example.com", "ann.png");

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(customerController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getAllCustomerProfiles_returnsList() throws Exception {
        when(customerService.getAllCustomerProfiles()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Ann Smith"));
    }

    @Test
    void getCustomerProfile_returnsProfile_whenFound() throws Exception {
        when(customerService.getCustomerProfile(1L)).thenReturn(response);

        mockMvc.perform(get("/api/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Ann Smith"))
                .andExpect(jsonPath("$.email").value("ann@example.com"))
                .andExpect(jsonPath("$.photo").value("ann.png"));
    }

    @Test
    void getCustomerProfile_returns404_whenNotFound() throws Exception {
        when(customerService.getCustomerProfile(99L)).thenThrow(new CustomerNotFoundException(99L));

        mockMvc.perform(get("/api/customers/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Customer with id 99 not found"));
    }

    @Test
    void createCustomerProfile_returnsProfile_whenValid() throws Exception {
        CustomerProfileCreateRequest request =
                new CustomerProfileCreateRequest("Ann Smith", "ann@example.com", "ann.png");
        when(customerService.createCustomerProfile(request)).thenReturn(response);

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Ann Smith","email":"ann@example.com","photo":"ann.png"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Ann Smith"));
    }

    @Test
    void createCustomerProfile_returns400_whenInvalid() throws Exception {
        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"","email":"not-an-email"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Name is required"))
                .andExpect(jsonPath("$.email").value("Email must be valid"))
                .andExpect(jsonPath("$.photo").value("Photo is required"));

        verifyNoInteractions(customerService);
    }

    @Test
    void updateCustomerProfile_returnsProfile_whenValid() throws Exception {
        CustomerProfileUpdateRequest request = new CustomerProfileUpdateRequest("Ann Smith", null, null);
        when(customerService.updateCustomerProfile(1L, request)).thenReturn(response);

        mockMvc.perform(patch("/api/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Ann Smith"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Ann Smith"));
    }

    @Test
    void updateCustomerProfile_returns400_whenInvalid() throws Exception {
        mockMvc.perform(patch("/api/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"   ","email":"not-an-email"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").value("Name must not be blank"))
                .andExpect(jsonPath("$.email").value("Email must be valid"));

        verifyNoInteractions(customerService);
    }

    @Test
    void updateCustomerProfile_returns404_whenNotFound() throws Exception {
        when(customerService.updateCustomerProfile(eq(99L), any()))
                .thenThrow(new CustomerNotFoundException(99L));

        mockMvc.perform(patch("/api/customers/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Ann Smith"}
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Customer with id 99 not found"));
    }

    @Test
    void deleteCustomerProfile_returns204() throws Exception {
        mockMvc.perform(delete("/api/customers/1"))
                .andExpect(status().isNoContent());

        verify(customerService).deleteCustomerProfile(1L);
    }

    @Test
    void deleteCustomerProfile_returns404_whenNotFound() throws Exception {
        doThrow(new CustomerNotFoundException(99L)).when(customerService).deleteCustomerProfile(99L);

        mockMvc.perform(delete("/api/customers/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Customer with id 99 not found"));
    }
}
