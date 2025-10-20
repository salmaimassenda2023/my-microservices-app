package com.example.productservice.product.controller;

import com.example.productservice.product.dto.ProductPurchaseRequest;
import com.example.productservice.product.dto.ProductPurchaseResponse;
import com.example.productservice.product.dto.ProductRequest;
import com.example.productservice.product.dto.ProductResponse;
import com.example.productservice.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")

public class ProductController {

    private final ProductService service;
    // Constructeur manuel
    public ProductController(ProductService service) {
        this.service = service;
    }


    @PostMapping
    public ResponseEntity<Integer> createProduct(
            @RequestBody @Valid ProductRequest request
    ) {
        return ResponseEntity.ok(service.createProduct(request));
    }

    @PostMapping("/purchase")
    public ResponseEntity<List<ProductPurchaseResponse>> purchaseProducts(
            @RequestBody List<ProductPurchaseRequest> request
    ) {
        return ResponseEntity.ok(service.purchaseProducts(request));
    }

    @GetMapping("/{product-id}")
    public ResponseEntity<ProductResponse> findById(
            @PathVariable("product-id") Integer productId
    ) {
        return ResponseEntity.ok(service.findById(productId));
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }
}
