package com.services.orderservice;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.services.orderservice.customers.customer.CustomerResponse;
import com.services.orderservice.customers.product.ProductPurchaseRequest;
import com.services.orderservice.customers.product.ProductPurchaseResponse;
import com.services.orderservice.order.OrderRepository;
import com.services.orderservice.order.OrderRequest;
import com.services.orderservice.order.PaymentMethod;
import com.services.orderservice.stubs.CustomerClientStub;
import com.services.orderservice.stubs.PaymentClientStub;
import com.services.orderservice.stubs.ProductClientStub;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrderServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    private static WireMockServer wireMockServer;

    // Stubs
    private CustomerClientStub customerStub;
    private ProductClientStub productStub;
    private PaymentClientStub paymentStub;

    @BeforeAll
    static void setupWireMock() {
        wireMockServer = new WireMockServer(8089);
        wireMockServer.start();
    }

    @AfterAll
    static void tearDownWireMock() {
        wireMockServer.stop();
    }

    @BeforeEach
    void setup() {
        wireMockServer.resetAll();

        // Initialiser les stubs
        customerStub = new CustomerClientStub(wireMockServer, objectMapper);
        productStub = new ProductClientStub(wireMockServer, objectMapper);
        paymentStub = new PaymentClientStub(wireMockServer);
    }

    @AfterEach
    void cleanDatabase() {
        orderRepository.deleteAll();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("application.config.customer-url", () -> "http://localhost:8089/api/v1/customers");
        registry.add("application.config.product-url", () -> "http://localhost:8089/api/v1/products");
        registry.add("application.config.payment-url", () -> "http://localhost:8089/api/v1/payments");
    }

    @Test
    @DisplayName("Devrait créer une commande avec succès")
    void shouldCreateOrderSuccessfully() throws Exception {
        // ARRANGE
        CustomerResponse customerResponse = new CustomerResponse(
                "CUST001",
                "John",
                "Doe",
                "john.doe@email.com"
        );

        List<ProductPurchaseResponse> productResponses = List.of(
                new ProductPurchaseResponse(1, "Laptop", "Dell XPS", BigDecimal.valueOf(1200), 2)
        );

        // Configurer les stubs
        customerStub.stubFindCustomerById_Success("CUST001", customerResponse);
        productStub.stubPurchaseProducts_Success(productResponses);
        paymentStub.stubRequestOrderPayment_Success(1);

        OrderRequest orderRequest = new OrderRequest(
                null,
                "ORD001",
                BigDecimal.valueOf(2400),
                PaymentMethod.CREDIT_CARD,
                "CUST001",
                List.of(new ProductPurchaseRequest(1, 2))
        );

        // ACT & ASSERT
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNumber());

        // Vérifier les appels
        customerStub.verifyFindCustomerByIdCalled("CUST001", 1);
        productStub.verifyPurchaseProductsCalled(1);
        paymentStub.verifyRequestOrderPaymentCalled(1);

        // Vérifier la base de données
        Assertions.assertEquals(1, orderRepository.count());
    }



    @Test
    @DisplayName("Devrait récupérer toutes les commandes")
    void shouldGetAllOrders() throws Exception {
        mockMvc.perform(get("/api/v1/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}