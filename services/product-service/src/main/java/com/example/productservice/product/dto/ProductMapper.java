package com.example.productservice.product.dto;

import com.example.productservice.product.models.Category;
import com.example.productservice.product.models.Product;

public class ProductMapper {

   public Product toProduct(ProductRequest request){
       return Product
               .builder()
               .id(request.id())
               .name(request.name())
               .description(request.description())
               .availableQuantity(request.availableQuantity())
               .price(request.price())
               .category( Category.builder().id(request.id_Category()).build())
               .build();
   }

    public ProductResponse toProductResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getAvailableQuantity(),
                product.getPrice(),
                product.getCategory().getId(),
                product.getCategory().getName(),
                product.getCategory().getDescription()
        );
    }

    public ProductPurchaseResponse toproductPurchaseResponse(Product product, double quantity) {
        return new ProductPurchaseResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                quantity
        );
    }





}
