package com.eshop.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * DTO for individual invoice item line
 */
@Data
public class InvoiceItemDTO {
    
    @NotBlank(message = "Order number is required")
    @JsonProperty("orderNo")
    private String orderNo;
    
    @NotNull(message = "Line number is required")
    @Min(value = 1, message = "Line number must be at least 1")
    @JsonProperty("lineNo")
    private Integer lineNo;
    
    @NotBlank(message = "Transporter name is required")
    @JsonProperty("transporterName")
    private String transporterName;
    
    @JsonProperty("trackingNo")
    private String trackingNo;
    
    @NotBlank(message = "Description is required")
    @JsonProperty("description")
    private String description;
    
    @NotBlank(message = "HSN code is required")
    @JsonProperty("hsn")
    private String hsn;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @JsonProperty("qty")
    private Integer qty;
    
    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.0", message = "Unit price must be non-negative")
    @JsonProperty("unitPrice")
    private Double unitPrice;
    
    @NotNull(message = "Discount is required")
    @DecimalMin(value = "0.0", message = "Discount must be non-negative")
    @JsonProperty("discount")
    private Double discount;
    
    @NotNull(message = "Tax rate is required")
    @DecimalMin(value = "0.0", message = "Tax rate must be non-negative")
    @DecimalMax(value = "100.0", message = "Tax rate must be at most 100")
    @JsonProperty("taxRate")
    private Double taxRate;
}

