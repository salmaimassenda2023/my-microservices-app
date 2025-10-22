package com.services.orderservice.orderLine;

public record OrderLineRequest(
        Integer id,
        Integer orderId,
        Integer productId,
        double quantity
)  {}
