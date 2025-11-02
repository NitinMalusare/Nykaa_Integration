package com.eshop.auth.controller;

import com.eshop.auth.dto.InvoiceRequestDTO;
import com.eshop.auth.dto.InvoiceResponseDTO;
import com.eshop.auth.service.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for invoice generation
 */
@Slf4j
@RestController
@RequestMapping("/api/sellerPanel/v3")
@RequiredArgsConstructor
@Tag(name = "Invoice Management", description = "APIs for generating invoices")
public class InvoiceController {
    
    private final InvoiceService invoiceService;
    
    /**
     * Generate invoice PDF
     * 
     * @param requestDTO Invoice request with order items and details
     * @return Invoice response with PDF Base64 and file path
     */
    @PostMapping("/generateInvoice")
    @Operation(summary = "Generate invoice PDF", 
               description = "Generates a Nykaa-style retail/tax invoice PDF dynamically based on order items")
    public ResponseEntity<InvoiceResponseDTO> generateInvoice(
            @Valid @RequestBody InvoiceRequestDTO requestDTO) {
        
        log.info("Received invoice generation request for order: {}", requestDTO.getOrderNo());
        
        try {
            InvoiceResponseDTO response = invoiceService.generateInvoice(requestDTO);
            log.info("Invoice generated successfully: {}", response.getInvoiceNo());
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error generating invoice for order: " + requestDTO.getOrderNo(), e);
            
            InvoiceResponseDTO errorResponse = new InvoiceResponseDTO();
            errorResponse.setResponseCode(500);
            errorResponse.setResponseMessage("Failed to generate invoice: " + e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}

