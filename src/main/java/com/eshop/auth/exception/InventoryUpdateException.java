package com.eshop.auth.exception;

public class InventoryUpdateException extends RuntimeException {
    private final String sku;
    private final String sellerId;
    private final String token;

    public InventoryUpdateException(String message, String sku, String sellerId, String token) {
        super(message);
        this.sku = sku;
        this.sellerId = sellerId;
        this.token = token;
    }

    public InventoryUpdateException(String message, String sku, String sellerId, String token, Throwable cause) {
        super(message, cause);
        this.sku = sku;
        this.sellerId = sellerId;
        this.token = token;
    }

    public String getSku() {
        return sku;
    }

    public String getSellerId() {
        return sellerId;
    }

    public String getToken() {
        return token;
    }
} 