package com.example.productservice.product.models;

import com.example.productservice.product.models.Category;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class Product {
    @Id
    @GeneratedValue
    private Integer id;
    private String name;
    private String description;
    private double availableQuantity;
    private BigDecimal price;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}
