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
    
    /**
     * Generate invoice PDF and return as byte array
     * 
     * @param request Invoice request DTO
     * @return PDF bytes and invoice number
     */
    InvoicePdfResult generateInvoicePdf(InvoiceRequestDTO request);
    
    /**
     * Result class for PDF generation
     */
    class InvoicePdfResult {
        private byte[] pdfBytes;
        private String invoiceNo;
        private String filePath;
        
        public InvoicePdfResult(byte[] pdfBytes, String invoiceNo, String filePath) {
            this.pdfBytes = pdfBytes;
            this.invoiceNo = invoiceNo;
            this.filePath = filePath;
        }
        
        public byte[] getPdfBytes() {
            return pdfBytes;
        }
        
        public String getInvoiceNo() {
            return invoiceNo;
        }
        
        public String getFilePath() {
            return filePath;
        }
    }
}

