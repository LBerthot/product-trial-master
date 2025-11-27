package com.producttrial.back.exception;

public class CartItemNotFoundException extends RuntimeException {
    public CartItemNotFoundException(String message) {
        super(message);
    }
    public CartItemNotFoundException(Long id) {
        super("Cart item with id " + id + " not found");
    }
}
