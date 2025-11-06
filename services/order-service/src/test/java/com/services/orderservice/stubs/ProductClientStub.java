package com.services.orderservice.stubs;



import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.services.orderservice.customers.product.ProductPurchaseResponse;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class ProductClientStub {

    private final WireMockServer wireMockServer;
    private final ObjectMapper objectMapper;

    public ProductClientStub(WireMockServer wireMockServer, ObjectMapper objectMapper) {
        this.wireMockServer = wireMockServer;
        this.objectMapper = objectMapper;
    }

    /**
     * Mock l'achat de produits avec succès (200)
     */
    public void stubPurchaseProducts_Success(List<ProductPurchaseResponse> productResponses) throws JsonProcessingException {
        wireMockServer.stubFor(
                post(urlEqualTo("/api/v1/products/purchase"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody(objectMapper.writeValueAsString(productResponses)))
        );
    }

    /**
     * Mock un stock insuffisant (400)
     */
    public void stubPurchaseProducts_InsufficientStock() {
        wireMockServer.stubFor(
                post(urlEqualTo("/api/v1/products/purchase"))
                        .willReturn(aResponse()
                                .withStatus(400)
                                .withBody("Insufficient stock"))
        );
    }

    /**
     * Mock un produit introuvable (404)
     */
    public void stubPurchaseProducts_ProductNotFound() {
        wireMockServer.stubFor(
                post(urlEqualTo("/api/v1/products/purchase"))
                        .willReturn(aResponse()
                                .withStatus(404)
                                .withBody("Product not found"))
        );
    }

    /**
     * Mock une erreur serveur (500)
     */
    public void stubPurchaseProducts_ServerError() {
        wireMockServer.stubFor(
                post(urlEqualTo("/api/v1/products/purchase"))
                        .willReturn(aResponse()
                                .withStatus(500)
                                .withBody("Internal Server Error"))
        );
    }

    /**
     * Vérifier que le service a été appelé
     */
    public void verifyPurchaseProductsCalled(int times) {
        wireMockServer.verify(times, postRequestedFor(urlEqualTo("/api/v1/products/purchase")));
    }
}
