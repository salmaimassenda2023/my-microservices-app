package com.example.customer.customer.controller;
import com.example.customer.customer.dto.CustomerRequest;
import com.example.customer.customer.dto.CustomerResponse;
import com.example.customer.customer.service.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class customerController {

    private final CustomerService customerService ;

    @PostMapping
    public ResponseEntity<String> createCustomer( @RequestBody @Valid CustomerRequest request){

        return ResponseEntity.ok( customerService.createCustomer(request));

    }

    @PutMapping
    public ResponseEntity<Void> updateCustomer( @RequestBody @Valid  CustomerRequest request){
        customerService.updateCustomer(request);
        return ResponseEntity.accepted().build();
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> findAll(){
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @DeleteMapping("/{customer-id}")
    public  ResponseEntity<Void> delete( @PathVariable("customer-id") String custumer_id){
        this.customerService.deleteCustomer(custumer_id);
       return ResponseEntity.accepted().build();
    }


   @PostMapping("/exists/{customer-id}")
    public ResponseEntity<Boolean> existsById(@PathVariable("customer-id") String custumer_id ){
       return ResponseEntity.ok(this.customerService.existById(custumer_id));
   }

   @GetMapping("/{customer-id}")
   public  ResponseEntity<CustomerResponse> findByid(@PathVariable("customer-id") String custumer_id){

        return ResponseEntity.ok(this.customerService.findByid(custumer_id));
   }


}
