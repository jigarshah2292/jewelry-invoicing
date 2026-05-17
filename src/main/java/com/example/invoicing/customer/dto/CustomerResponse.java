package com.example.invoicing.customer.dto;

import com.example.invoicing.customer.Customer;

import java.time.Instant;

public record CustomerResponse(
        Long id,
        String name,
        String email,
        String phone,
        String address,
        Instant createdAt,
        Instant updatedAt
) {
    public static CustomerResponse from(Customer c) {
        return new CustomerResponse(
                c.getId(), c.getName(), c.getEmail(), c.getPhone(), c.getAddress(),
                c.getCreatedAt(), c.getUpdatedAt()
        );
    }
}
