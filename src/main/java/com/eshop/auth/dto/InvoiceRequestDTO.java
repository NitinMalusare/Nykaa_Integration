package com.eshop.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * Request DTO for invoice generation
 */
@Data
public class InvoiceRequestDTO {
    
    @NotEmpty(message = "Order items list cannot be empty")
    @Valid
    @JsonProperty("orderItemsList")
    private List<InvoiceItemDTO> orderItemsList;
    
    @NotBlank(message = "Seller name is required")
    @JsonProperty("sellerName")
    private String sellerName;
    
    @NotBlank(message = "Seller address is required")
    @JsonProperty("sellerAddress")
    private String sellerAddress;
    
    @NotBlank(message = "Seller GSTIN is required")
    @JsonProperty("sellerGstin")
    private String sellerGstin;
    
    @NotBlank(message = "Buyer name is required")
    @JsonProperty("buyerName")
    private String buyerName;
    
    @NotBlank(message = "Buyer address is required")
    @JsonProperty("buyerAddress")
    private String buyerAddress;
    
    @NotBlank(message = "AWB number is required")
    @JsonProperty("awbNo")
    private String awbNo;
    
    @NotBlank(message = "Order number is required")
    @JsonProperty("orderNo")
    private String orderNo;
    
    @NotBlank(message = "Order date is required")
    @JsonProperty("orderDate")
    private String orderDate;
    
    @NotBlank(message = "Payment mode is required")
    @JsonProperty("paymentMode")
    private String paymentMode;
    
    @NotBlank(message = "Place of supply is required")
    @JsonProperty("placeOfSupply")
    private String placeOfSupply;
}

