package com.example.productservice.dto;

import java.math.BigDecimal;

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
