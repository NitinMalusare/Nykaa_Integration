package com.eshop.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class InventoryPriceUpdateResponseDTO {
    @NotNull
    @JsonProperty("responseCode")
    private Integer responseCode;

    @NotNull
    @JsonProperty("responseMessage")
    private String responseMessage;

    @JsonProperty("skus")
    private List<SKUResponseDTO> skus;

    @Data
    public static class SKUResponseDTO {
        @JsonProperty("referenceKey")
        private String referenceKey;

        @JsonProperty("moderationId")
        private Long moderationId;

        @JsonProperty("sku")
        private String sku;

        @JsonProperty("requestedInvQty")
        private Integer requestedInvQty;

        @JsonProperty("approvedInvQty")
        private Integer approvedInvQty;

        @JsonProperty("status")
        private String status;

        @JsonProperty("requestedSalePrice")
        private Double requestedSalePrice;

        @JsonProperty("sellerId")
        private String sellerId;
    }
}
