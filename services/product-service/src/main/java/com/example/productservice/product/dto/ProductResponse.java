package com.example.productservice.product.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductResponse(
        Integer id,
        String name,
        String description,
        double availableQuantity,
        BigDecimal price,
        Integer id_Category,
        String categoryName,
        String categoryDescription

) {
}
