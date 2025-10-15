package com.eshop.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class InventoryItemDTO {
    @NotBlank(message = "SKU is required")
    private String sku;
    
    @NotNull(message = "Inventory quantity is required")
    @Positive(message = "Inventory quantity must be positive")
    private Integer invQty;
    
    @NotBlank(message = "Seller ID is required")
    private String sellerId;
}

