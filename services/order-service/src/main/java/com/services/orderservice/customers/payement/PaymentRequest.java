package com.services.orderservice.customers.payement;

import com.services.orderservice.customers.customer.CustomerResponse;
import com.services.orderservice.order.PaymentMethod;

import java.math.BigDecimal;

public record PaymentRequest(
        BigDecimal amount,
        PaymentMethod paymentMethod,
        Integer orderId,
        String orderReference,
        CustomerResponse customer
) {
}
