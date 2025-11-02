package com.eshop.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Response DTO for invoice generation
 */
@Data
public class InvoiceResponseDTO {
    
    @JsonProperty("responseCode")
    private Integer responseCode;
    
    @JsonProperty("responseMessage")
    private String responseMessage;
    
    @JsonProperty("invoiceNo")
    private String invoiceNo;
    
    @JsonProperty("pdfBase64")
    private String pdfBase64;
    
    @JsonProperty("filePath")
    private String filePath;
}

