package com.services.orderservice.order;

import com.services.orderservice.customers.customer.CustomerClient;
import com.services.orderservice.customers.customer.CustomerResponse;
import com.services.orderservice.customers.payement.PaymentClient;
import com.services.orderservice.customers.payement.PaymentRequest;
import com.services.orderservice.customers.product.ProductClient;
import com.services.orderservice.customers.product.ProductPurchaseRequest;
import com.services.orderservice.customers.product.ProductPurchaseResponse;
import com.services.orderservice.exception.BusinessException;
import com.services.orderservice.kafka.OrderConfirmation;
import com.services.orderservice.kafka.OrderProducer;
import com.services.orderservice.orderLine.OrderLineRequest;
import com.services.orderservice.orderLine.OrderLineService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("🧪 OrderService Unit Tests")
class OrderServiceTest {

    // 🔹 Mocked dependencies
    @Mock private OrderRepository repository;
    @Mock private OrderMapper mapper;
    @Mock private CustomerClient customerClient;
    @Mock private ProductClient productClient;
    @Mock private PaymentClient paymentClient;
    @Mock private OrderLineService orderLineService;
    @Mock private OrderProducer orderProducer;

    // 🔹 Class under test
    @InjectMocks
    private OrderService orderService;

    // 🔹 Common test objects
    private OrderRequest orderRequest;
    private Order order;
    private CustomerResponse customer;
    private ProductPurchaseResponse productResponse;

    @BeforeEach
    void setUp() {
        orderRequest = new OrderRequest(
                1,
                "REF123",
                BigDecimal.valueOf(2000),
                PaymentMethod.CREDIT_CARD,
                "1", // customerId as String
                List.of(new ProductPurchaseRequest(1, 2))
        );

        order = new Order();
        order.setId(1);
        order.setReference("REF123");

        customer = new CustomerResponse("1", "Salma", "Imassenda", "salma@example.com");

        productResponse = new ProductPurchaseResponse(
                1,
                "Laptop",
                "Dell XPS",
                BigDecimal.valueOf(1000),
                2
        );
    }

    // ===================================================
    // 🧩 CREATE ORDER TESTS
    // ===================================================
    @Nested
    @DisplayName("🧩 createOrder() method")
    class CreateOrderTests {

        @Test
        @DisplayName("✅ Should create order successfully when all dependencies respond correctly")
        void shouldCreateOrderSuccessfully() {
            // GIVEN
            when(customerClient.findCustomerById("1"))
                    .thenReturn(ResponseEntity.ok(customer));
            when(productClient.purchaseProducts(any()))
                    .thenReturn(ResponseEntity.ok(List.of(productResponse)));
            when(mapper.toOrder(orderRequest)).thenReturn(order);
            when(repository.save(order)).thenReturn(order);

            // WHEN
            Integer result = orderService.createOrder(orderRequest);

            // THEN
            assertNotNull(result);
            assertEquals(1, result);
            verify(repository, times(1)).save(order);
            verify(orderLineService, times(1)).saveOrderLine(any(OrderLineRequest.class));
            verify(paymentClient, times(1)).requestOrderPayment(any(PaymentRequest.class));
            verify(orderProducer, times(1)).sendOrderConfirmation(any(OrderConfirmation.class));
        }

        @Test
        @DisplayName("🚫 Should throw BusinessException when customer not found")
        void shouldThrowExceptionWhenCustomerNotFound() {
            // GIVEN
            when(customerClient.findCustomerById(anyString()))
                    .thenReturn(ResponseEntity.ok(null));

            // WHEN + THEN
            assertThrows(BusinessException.class,
                    () -> orderService.createOrder(orderRequest));

            verify(customerClient, times(1)).findCustomerById(anyString());
            verify(repository, never()).save(any(Order.class));
        }

        @Test
        @DisplayName("⚠️ Should handle exception when payment client fails")
        void shouldHandleExceptionWhenPaymentFails() {
            // GIVEN
            when(customerClient.findCustomerById("1")).thenReturn(ResponseEntity.ok(customer));
            when(productClient.purchaseProducts(any()))
                    .thenReturn(ResponseEntity.ok(List.of(productResponse)));
            when(mapper.toOrder(orderRequest)).thenReturn(order);
            when(repository.save(order)).thenReturn(order);
            doThrow(new RuntimeException("Payment service unavailable"))
                    .when(paymentClient).requestOrderPayment(any(PaymentRequest.class));

            // WHEN + THEN
            assertThrows(RuntimeException.class, () -> orderService.createOrder(orderRequest));
            verify(paymentClient, times(1)).requestOrderPayment(any(PaymentRequest.class));
        }
    }

    // ===================================================
    // 🧩 FIND ALL ORDERS TESTS
    // ===================================================
    @Nested
    @DisplayName("🧩 findAllOrders() method")
    class FindAllOrdersTests {

        @Test
        @DisplayName("✅ Should return list of orders successfully")
        void shouldReturnAllOrders() {
            // GIVEN
            Order order2 = new Order();
            order2.setId(2);

            List<Order> orders = List.of(order, order2);

            OrderResponse response1 = new OrderResponse(
                    1, "REF123", BigDecimal.valueOf(2000), PaymentMethod.CREDIT_CARD, "1"
            );

            OrderResponse response2 = new OrderResponse(
                    2, "REF456", BigDecimal.valueOf(500), PaymentMethod.PAYPAL, "2"
            );


            when(repository.findAll()).thenReturn(orders);
            when(mapper.fromOrder(order)).thenReturn(response1);
            when(mapper.fromOrder(order2)).thenReturn(response2);

            // WHEN
            List<OrderResponse> result = orderService.findAllOrders();

            // THEN
            assertEquals(2, result.size());
            verify(repository, times(1)).findAll();
            verify(mapper, times(2)).fromOrder(any(Order.class));
        }

        @Test
        @DisplayName("🚫 Should return empty list when no orders exist")
        void shouldReturnEmptyList() {
            when(repository.findAll()).thenReturn(List.of());

            List<OrderResponse> result = orderService.findAllOrders();

            assertTrue(result.isEmpty());
            verify(repository, times(1)).findAll();
        }
    }

    // ===================================================
    // 🧩 FIND ORDER BY ID TESTS
    // ===================================================
    @Nested
    @DisplayName("🧩 findById() method")
    class FindByIdTests {

        @Test
        @DisplayName("✅ Should return order when found by ID")
        void shouldReturnOrderWhenFound() {
            OrderResponse response = new OrderResponse(
                    1, "REF123", BigDecimal.valueOf(2000), PaymentMethod.CREDIT_CARD,"1"
            );

            when(repository.findById(1)).thenReturn(Optional.of(order));
            when(mapper.fromOrder(order)).thenReturn(response);

            OrderResponse result = orderService.findById(1);

            assertNotNull(result);
            assertEquals("REF123", result.reference());
            verify(repository, times(1)).findById(1);
            verify(mapper, times(1)).fromOrder(order);
        }

        @Test
        @DisplayName("🚫 Should throw EntityNotFoundException when order not found")
        void shouldThrowExceptionWhenOrderNotFound() {
            when(repository.findById(99)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> orderService.findById(99));

            verify(repository, times(1)).findById(99);
        }
    }
}
