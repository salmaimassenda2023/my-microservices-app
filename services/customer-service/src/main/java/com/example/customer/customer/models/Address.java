package com.example.customer.customer.models;


import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Address {
    private String street ;
    private String houseName ;
    private String zipCode ;
}
