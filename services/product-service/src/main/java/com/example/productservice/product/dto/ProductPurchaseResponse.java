package com.example.productservice.product.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductPurchaseResponse(
        Integer productId,
        String name,
        String description,
        BigDecimal price,
        double quantity
) {
}
