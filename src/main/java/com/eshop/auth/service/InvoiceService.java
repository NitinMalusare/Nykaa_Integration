package com.eshop.auth.service;

import com.eshop.auth.dto.InvoiceJsonDTO;
import com.eshop.auth.dto.InvoiceRequestDTO;
import com.eshop.auth.dto.InvoiceResponseDTO;
import com.eshop.auth.dto.NykaaInvoicePayload;

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
     * Generate invoice PDF using the new Nykaa layout from raw payload.
     *
     * @param payload Structured payload containing all invoice data
     * @return PDF bytes and invoice number
     */
    InvoicePdfResult generateInvoiceFromPayload(NykaaInvoicePayload payload);
    
    /**
     * Generate invoice PDF from order number (fetches order and order items dynamically)
     * 
     * @param orderNo Order number
     * @param token API token for product lookup
     * @return PDF bytes and invoice number
     */
    InvoicePdfResult generateInvoiceFromOrder(String orderNo, String token);

    /**
     * Generate invoice data in JSON format.
     *
     * @param request Invoice request DTO
     * @return Invoice data as a JSON DTO
     */
    InvoiceJsonDTO generateInvoiceJson(InvoiceRequestDTO request);
    
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
