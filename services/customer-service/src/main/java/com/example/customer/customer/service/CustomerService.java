package com.example.customer.customer.service;

import com.example.customer.customer.dto.CustomerMapper;
import com.example.customer.customer.dto.CustomerRequest;
import com.example.customer.customer.dto.CustomerResponse;
import com.example.customer.customer.models.Customer;
import com.example.customer.customer.repository.CustomerRepository;
import com.example.customer.exceptions.CustomerNotFoundException;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerRepository repository;
    private final CustomerMapper mapper ;

    public CustomerService(CustomerRepository repository, CustomerMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

//    Methodes

    public String createCustomer(CustomerRequest request){
        var customer = repository.save(mapper.toCustomer(request));
        return customer.getId();
    }

    private void mergeCustomer (Customer customer,CustomerRequest request){
        if(StringUtils.isNotBlank(request.firstName())){
           customer.setFirstName(request.firstName());
        }
        if(StringUtils.isNotBlank(request.email())){
            customer.setEmail(request.email());
        }
        if(request.adress() != null){
            customer.setAdress(request.adress());
        }
    }

    public void updateCustomer(CustomerRequest request){
        var customer = repository.findById(request.id()).orElseThrow(
                () -> new CustomerNotFoundException( String.format("Cannot update customer:: No customer found with the provided ID: %s,",request.id()) )
        );
        mergeCustomer(customer,request);
        repository.save(customer);
    }

    public void deleteCustomer(String id){
        repository.deleteById(id);
    }

    public List<CustomerResponse> getAllCustomers(){
        return this.repository.findAll()
                .stream()
                .map(mapper::fromCustomer)
                .collect(Collectors.toList());
    }

    public CustomerResponse findByid(String id){
        var customer = repository.findById(id)
                .map(mapper::fromCustomer)
                .orElseThrow(() -> new CustomerNotFoundException( String.format("Cannot update customer:: No customer found with the provided ID: %s,",id) )
        );
        return customer;

    }

    public boolean existById(String id){
        return repository.findById(id).isPresent();
    }




}
