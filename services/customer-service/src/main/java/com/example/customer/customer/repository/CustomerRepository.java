package com.example.customer.customer.repository;


import com.example.customer.customer.models.Customer;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface CustomerRepository extends MongoRepository<Customer,String> {
}
