package com.example.customerprofile.customer.mapper;

import com.example.customerprofile.customer.dto.CustomerProfileCreateRequest;
import com.example.customerprofile.customer.dto.CustomerProfileResponse;
import com.example.customerprofile.customer.dto.CustomerProfileUpdateRequest;
import com.example.customerprofile.customer.entity.Customers;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper
public interface CustomerMapper {

    CustomerProfileResponse toResponse(Customers customers);

    List<CustomerProfileResponse> toResponseList(List<Customers> customers);

    @Mapping(target = "id", ignore = true)
    Customers toEntity(CustomerProfileCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateFromRequest(CustomerProfileUpdateRequest request, @MappingTarget Customers customers);

}
