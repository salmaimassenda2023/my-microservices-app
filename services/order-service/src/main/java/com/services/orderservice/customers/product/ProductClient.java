package com.services.orderservice.customers.product;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ProductClient {

    private final RestTemplate restTemplate;

    @Value("${application.config.product-url}")
    private String productUrl;

    public ResponseEntity<List<ProductPurchaseResponse>> purchaseProducts(List<ProductPurchaseRequest> request){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<List<ProductPurchaseRequest>> entity = new HttpEntity<>(request,headers);

        ResponseEntity<List<ProductPurchaseResponse>>   response = restTemplate.exchange(
                productUrl + "/purchase",
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<List<ProductPurchaseResponse>>() {}

        );
        if(response.getStatusCode().isError()){
            throw  new RuntimeException("Error while purchasing products: "+response.getStatusCode());
        }

                return  response;

    }

}
