package com.example.customer.customer.dto;

import com.example.customer.customer.models.Address;

public record CustomerResponse (String id, String firstName, String lastName, String email, Address adress) {
}
