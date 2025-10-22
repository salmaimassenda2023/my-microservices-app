package com.services.orderservice.customers.customer;

public record CustomerResponse(
        String id,
        String firstname,
        String lastname,
        String email
) {
}
