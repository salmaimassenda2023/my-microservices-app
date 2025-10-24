package com.ecommerce.notificationserver.kafka.order;

public record Customer(
        String id,
        String firstname,
        String lastname,
        String email
) {
}
