package com.example.productservice.exception;

public class ProductPurchaseException extends RuntimeException{
    public ProductPurchaseException(String msg){
        super(msg);
    }
}
