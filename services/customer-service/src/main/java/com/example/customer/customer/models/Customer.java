package com.example.customer.customer.models;


import com.example.customer.customer.models.Address;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Document
public class Customer {
    private String id ;
    private String firstName ;
    private String lastName ;
    private String email ;
    private Address adress ;
}
