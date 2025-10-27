package com.example.customer.customer.dto;

import com.example.customer.customer.models.Address;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CustomerRequest
        (
             String id ,
             @NotNull(message = "Customer firstName is required")
             String firstName ,
             @NotNull(message = "Customer lastName is required")
             String lastName ,
             @NotNull(message = "Customer email is required")
             @Email(message = "Customer email is not valid")
             String email ,
             Address adress

    ){}



