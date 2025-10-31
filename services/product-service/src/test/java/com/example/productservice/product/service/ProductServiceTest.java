package com.example.productservice.product.service;



import com.example.productservice.exception.ProductPurchaseException;
import com.example.productservice.product.dto.*;

import com.example.productservice.product.models.Product;
import com.example.productservice.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Start Product Service Test")
class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @Mock
    private ProductMapper mapper;

    @InjectMocks
    private ProductService productService;

    private Product product;
    private Product product2;
    private ProductRequest request;
    private ProductResponse response;
    private ProductPurchaseRequest purchaseRequest1;
    private ProductPurchaseRequest purchaseRequest2;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1)
                .name("Laptop")
                .description("Dell XPS 13")
                .price(BigDecimal.valueOf(1500.0))
                .availableQuantity(10)
                .build();

        product2 = Product.builder()
                .id(2)
                .name("Mouse")
                .description("Wireless Mouse")
                .price(BigDecimal.valueOf(50.0))
                .availableQuantity(20)
                .build();

        request = ProductRequest.builder()
                .name("Laptop")
                .description("Dell XPS 13")
                .price(BigDecimal.valueOf(1500.0))
                .availableQuantity(10)
                .build();

        response = ProductResponse.builder()
                .id(1)
                .name("Laptop")
                .description("Dell XPS 13")
                .price(BigDecimal.valueOf(1500.0))
                .availableQuantity(10)
                .build();

        purchaseRequest1 = new ProductPurchaseRequest(1, 3);
        purchaseRequest2 = new ProductPurchaseRequest(2, 5);
    }

    @Nested
    @DisplayName("Testing Create Product Method ...")
    class CreateProduct {

        @Test
        @DisplayName("Should create product successfully and return ID")
        void shouldCreateProductSuccessfully() {
            when(mapper.toProduct(request)).thenReturn(product);
            when(repository.save(any(Product.class))).thenReturn(product);

            Integer id = productService.createProduct(request);

            assertNotNull(id);
            assertEquals(1, id);
            verify(mapper, times(1)).toProduct(request);
            verify(repository, times(1)).save(product);
        }

        @Test
        @DisplayName("Should throw exception when save fails")
        void shouldThrowExceptionWhenSaveFails() {
            when(mapper.toProduct(request)).thenReturn(product);
            when(repository.save(any(Product.class))).thenThrow(new RuntimeException("DB error"));

            assertThrows(RuntimeException.class, () -> productService.createProduct(request));
            verify(repository, times(1)).save(product);
        }
    }

    @Nested
    @DisplayName("Testing Find By ID Method ...")
    class FindById {

        @Test
        @DisplayName("Should return product when found by ID")
        void shouldReturnProductWhenFound() {
            when(repository.findById(1)).thenReturn(Optional.of(product));
            when(mapper.toProductResponse(product)).thenReturn(response);

            ProductResponse result = productService.findById(1);

            assertNotNull(result);
            assertEquals(1, result.id());
            assertEquals("Laptop", result.name());
            verify(repository, times(1)).findById(1);
            verify(mapper, times(1)).toProductResponse(product);
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when product not found")
        void shouldThrowExceptionWhenProductNotFound() {
            when(repository.findById(99)).thenReturn(Optional.empty());

            EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                    () -> productService.findById(99));

            assertTrue(ex.getMessage().contains("Product not found with ID:: 99"));
            verify(repository, times(1)).findById(99);
        }
    }

    @Nested
    @DisplayName("Testing Find All Method ...")
    class FindAll {

        @Test
        @DisplayName("Should return all products successfully")
        void shouldReturnAllProducts() {
            when(repository.findAll()).thenReturn(List.of(product, product2));
            when(mapper.toProductResponse(product)).thenReturn(response);
            when(mapper.toProductResponse(product2))
                    .thenReturn(ProductResponse.builder().id(2).name("Mouse").build());

            List<ProductResponse> results = productService.findAll();

            assertEquals(2, results.size());
            verify(repository, times(1)).findAll();
            verify(mapper, times(2)).toProductResponse(any(Product.class));
        }

        @Test
        @DisplayName("Should return empty list when no products exist")
        void shouldReturnEmptyList() {
            when(repository.findAll()).thenReturn(Collections.emptyList());

            List<ProductResponse> results = productService.findAll();

            assertTrue(results.isEmpty());
            verify(repository, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("Testing Purchase Products Method ...")
    class PurchaseProducts {

        @Test
        @DisplayName("Should purchase products successfully when stock is sufficient")
        void shouldPurchaseProductsSuccessfully() {
            List<ProductPurchaseRequest> purchaseRequests = List.of(purchaseRequest1, purchaseRequest2);
            List<Product> storedProducts = List.of(product, product2);

            when(repository.findAllByIdInOrderById(List.of(1, 2))).thenReturn(storedProducts);
            when(mapper.toproductPurchaseResponse(any(Product.class), anyDouble()))
                    .thenAnswer(invocation -> {
                        Product p = invocation.getArgument(0);
                        double qty = invocation.getArgument(1);
                        return new ProductPurchaseResponse(
                                p.getId(),
                                p.getName(),
                                p.getDescription(),
                                p.getPrice(),
                                qty
                        );
                    });

            List<ProductPurchaseResponse> result = productService.purchaseProducts(purchaseRequests);

            assertEquals(2, result.size());
            verify(repository, times(2)).save(any(Product.class));
        }

        @Test
        @DisplayName("Should throw exception when product does not exist")
        void shouldThrowExceptionWhenProductMissing() {
            List<ProductPurchaseRequest> requests = List.of(purchaseRequest1);
            when(repository.findAllByIdInOrderById(List.of(1))).thenReturn(Collections.emptyList());

            ProductPurchaseException ex = assertThrows(ProductPurchaseException.class,
                    () -> productService.purchaseProducts(requests));

            assertTrue(ex.getMessage().contains("One or more products does not exist"));
            verify(repository, times(1)).findAllByIdInOrderById(List.of(1));
        }

        @Test
        @DisplayName("Should throw exception when stock is insufficient")
        void shouldThrowExceptionWhenInsufficientStock() {
            List<ProductPurchaseRequest> requests = List.of(new ProductPurchaseRequest(1, 20));
            when(repository.findAllByIdInOrderById(List.of(1))).thenReturn(List.of(product));

            ProductPurchaseException ex = assertThrows(ProductPurchaseException.class,
                    () -> productService.purchaseProducts(requests));

            assertTrue(ex.getMessage().contains("Insufficient stock quantity"));
            verify(repository, times(1)).findAllByIdInOrderById(List.of(1));
        }
    }
}
