package com.eshop.auth.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.List;

@Data
public class InventoryPriceUpdateRequestDTO {
    @NotEmpty(message = "Inventory price list cannot be empty")
    @Size(max = 100, message = "Cannot update more than 100 items at once")
    @Valid
    private List<InventoryItemDTO> invPriceList;

    @Data
    public static class InventoryItemDTO {
        @NotNull(message = "SKU is required")
        private String sku;
        
        @NotNull(message = "Inventory quantity is required")
        @Positive(message = "Inventory quantity must be positive")
        private Integer invQty;
        
        @NotNull(message = "Seller ID is required")
        private String sellerId;
    }
}
