package com.services.orderservice.stubs;


import com.github.tomakehurst.wiremock.WireMockServer;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class PaymentClientStub {

    private final WireMockServer wireMockServer;

    public PaymentClientStub(WireMockServer wireMockServer) {
        this.wireMockServer = wireMockServer;
    }

    /**
     * Mock un paiement réussi (200)
     */
    public void stubRequestOrderPayment_Success(Integer paymentId) {
        wireMockServer.stubFor(
                post(urlEqualTo("/api/v1/payments"))
                        .willReturn(aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "application/json")
                                .withBody(paymentId.toString()))
        );
    }

    /**
     * Mock un paiement refusé (400)
     */
    public void stubRequestOrderPayment_PaymentDeclined() {
        wireMockServer.stubFor(
                post(urlEqualTo("/api/v1/payments"))
                        .willReturn(aResponse()
                                .withStatus(400)
                                .withBody("Payment declined"))
        );
    }

    /**
     * Mock un dépassement de délai (408 - Request Timeout)
     */
    public void stubRequestOrderPayment_Timeout() {
        wireMockServer.stubFor(
                post(urlEqualTo("/api/v1/payments"))
                        .willReturn(aResponse()
                                .withStatus(408)
                                .withFixedDelay(5000) // 5 secondes de délai
                                .withBody("Request timeout"))
        );
    }

    /**
     * Mock une erreur serveur (500)
     */
    public void stubRequestOrderPayment_ServerError() {
        wireMockServer.stubFor(
                post(urlEqualTo("/api/v1/payments"))
                        .willReturn(aResponse()
                                .withStatus(500)
                                .withBody("Internal Server Error"))
        );
    }

    /**
     * Vérifier que le service a été appelé
     */
    public void verifyRequestOrderPaymentCalled(int times) {
        wireMockServer.verify(times, postRequestedFor(urlEqualTo("/api/v1/payments")));
    }
}