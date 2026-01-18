package org.example.shopify.Exception;

public class EmptyCartCheckoutException extends RuntimeException {
    public EmptyCartCheckoutException(String message) {
        super(message);
    }
}
