package com.eshop.auth.service;

import com.eshop.auth.dto.InvoiceRequestDTO;
import com.eshop.auth.dto.InvoiceResponseDTO;

/**
 * Service interface for invoice generation
 */
public interface InvoiceService {
    
    /**
     * Generate invoice PDF
     * 
     * @param request Invoice request DTO
     * @return Invoice response DTO with PDF data
     */
    InvoiceResponseDTO generateInvoice(InvoiceRequestDTO request);
}

