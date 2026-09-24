package com.example.customerprofile.customer.mapper;

import com.example.customerprofile.customer.dto.CustomerProfileCreateRequest;
import com.example.customerprofile.customer.dto.CustomerProfileResponse;
import com.example.customerprofile.customer.dto.CustomerProfileUpdateRequest;
import com.example.customerprofile.customer.entity.Customers;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerMapperTest {

    private final CustomerMapper mapper = new CustomerMapperImpl();

    @Test
    void toResponse_copiesAllFields() {
        Customers customer = new Customers(1L, "Ann Smith", "ann@example.com", "ann.png");

        assertThat(mapper.toResponse(customer))
                .isEqualTo(new CustomerProfileResponse(1L, "Ann Smith", "ann@example.com", "ann.png"));
    }

    @Test
    void toResponse_returnsNull_forNullInput() {
        assertThat(mapper.toResponse(null)).isNull();
    }

    @Test
    void toResponseList_mapsEveryElement() {
        List<Customers> customers = List.of(
                new Customers(1L, "Ann", "ann@example.com", "a.png"),
                new Customers(2L, "Bob", "bob@example.com", "b.png"));

        assertThat(mapper.toResponseList(customers))
                .extracting(CustomerProfileResponse::getId)
                .containsExactly(1L, 2L);
    }

    @Test
    void toResponseList_returnsNull_forNullInput() {
        assertThat(mapper.toResponseList(null)).isNull();
    }

    @Test
    void toEntity_copiesFieldsAndLeavesIdEmpty() {
        CustomerProfileCreateRequest request =
                new CustomerProfileCreateRequest("Ann Smith", "ann@example.com", "ann.png");

        assertThat(mapper.toEntity(request))
                .isEqualTo(new Customers(null, "Ann Smith", "ann@example.com", "ann.png"));
    }

    @Test
    void toEntity_returnsNull_forNullInput() {
        assertThat(mapper.toEntity(null)).isNull();
    }

    @Test
    void updateFromRequest_overwritesAllProvidedFields() {
        Customers customer = new Customers(1L, "Ann", "ann@example.com", "a.png");

        mapper.updateFromRequest(
                new CustomerProfileUpdateRequest("Bob", "bob@example.com", "b.png"), customer);

        assertThat(customer).isEqualTo(new Customers(1L, "Bob", "bob@example.com", "b.png"));
    }

    @Test
    void updateFromRequest_keepsFieldsThatAreNull() {
        Customers customer = new Customers(1L, "Ann", "ann@example.com", "a.png");

        mapper.updateFromRequest(new CustomerProfileUpdateRequest(null, null, null), customer);

        assertThat(customer).isEqualTo(new Customers(1L, "Ann", "ann@example.com", "a.png"));
    }

    @Test
    void updateFromRequest_doesNothing_forNullRequest() {
        Customers customer = new Customers(1L, "Ann", "ann@example.com", "a.png");

        mapper.updateFromRequest(null, customer);

        assertThat(customer).isEqualTo(new Customers(1L, "Ann", "ann@example.com", "a.png"));
    }
}
