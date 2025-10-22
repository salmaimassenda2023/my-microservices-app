package com.services.orderservice.kafka;

import com.services.orderservice.customers.customer.CustomerResponse;
import com.services.orderservice.customers.product.ProductPurchaseRequest;
import com.services.orderservice.order.PaymentMethod;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation (
        String orderReference,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        CustomerResponse customer,
        List<ProductPurchaseRequest> products

) {
}