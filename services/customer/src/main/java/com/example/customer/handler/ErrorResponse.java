package com.example.customer.handler;

import java.util.Map;

// DTO pour le message d erreur

public record ErrorResponse(Map<String,String> errors) {
}
