package com.example.productservice.product.repository;

import com.example.productservice.product.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository  extends JpaRepository<Product,Integer> {
    List<Product> findAllByIdInOrderById(List<Integer> ids);
}
