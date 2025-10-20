package com.example.customer.customer.dto;

import com.example.customer.customer.models.Customer;
import org.springframework.stereotype.Component;


@Component
public class CustomerMapper {

    public Customer toCustomer( CustomerRequest customerRequest){
       if(customerRequest == null){
           return null;
       }
       return Customer.builder()
               .id(customerRequest.id())
               .firstName(customerRequest.firstName())
               .lastName(customerRequest.lastName())
               .email(customerRequest.email())
               .adress(customerRequest.adress())
               .build();

    }

    public CustomerResponse fromCustomer( Customer customer){
     if(customer == null){
         return null;
     }
     return new CustomerResponse(
             customer.getId(),
             customer.getFirstName(),
             customer.getLastName(),
             customer.getEmail(),
             customer.getAdress() );
    }

}
