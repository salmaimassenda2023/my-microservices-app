package com.services.orderservice.stubs;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.services.orderservice.customers.customer.CustomerResponse;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class CustomerClientStub {

    private final WireMockServer wireMockServer;
    private final ObjectMapper objectMapper;

    public CustomerClientStub(WireMockServer wireMockServer, ObjectMapper objectMapper) {
        this.wireMockServer = wireMockServer;
        this.objectMapper = objectMapper;
    }

    /**
     * Mock un client existant avec succès (200)
     */
    public void stubFindCustomerById_Success(String customerId, CustomerResponse customerResponse) throws JsonProcessingException {
        wireMockServer.stubFor(
                get(urlEqualTo("/api/v1/customers/" + customerId))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody(objectMapper.writeValueAsString(customerResponse)))
        );
    }

    /**
     * Mock un client introuvable (404)
     */
    public void stubFindCustomerById_NotFound(String customerId) {
        wireMockServer.stubFor(
                get(urlEqualTo("/api/v1/customers/" + customerId))
                        .willReturn(aResponse()
                                .withStatus(404))
        );
    }

    /**
     * Mock une erreur serveur (500)
     */
    public void stubFindCustomerById_ServerError(String customerId) {
        wireMockServer.stubFor(
                get(urlEqualTo("/api/v1/customers/" + customerId))
                        .willReturn(aResponse()
                                .withStatus(500)
                                .withBody("Internal Server Error"))
        );
    }

    /**
     * Vérifier que le service a été appelé
     */
    public void verifyFindCustomerByIdCalled(String customerId, int times) {
        wireMockServer.verify(times, getRequestedFor(urlEqualTo("/api/v1/customers/" + customerId)));
    }
}