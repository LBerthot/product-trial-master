package com.producttrial.back.exception;

public class WishlistItemNotFoundException extends RuntimeException {
    public WishlistItemNotFoundException(String message) {
        super(message);
    }
    public WishlistItemNotFoundException(Long id) {
        super("Wishlist item with id " + id + " not found");
    }
}
