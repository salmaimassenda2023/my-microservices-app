package com.example.productservice.handler;

import java.util.Map;

public record ErrorResponse(
        Map<String, String> errors
) {
}
