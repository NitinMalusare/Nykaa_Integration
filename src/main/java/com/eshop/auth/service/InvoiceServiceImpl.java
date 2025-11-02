package com.eshop.auth.service;

import com.eshop.auth.dto.InvoiceRequestDTO;
import com.eshop.auth.dto.InvoiceResponseDTO;
import com.eshop.auth.util.InvoicePdfBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

/**
 * Service implementation for invoice generation
 */
@Service
public class InvoiceServiceImpl implements InvoiceService {
    
    private static final Logger logger = LoggerFactory.getLogger(InvoiceServiceImpl.class);
    private static final String INVOICE_DIR = "./invoices";
    private static final String INVOICE_PREFIX = "INV-NYKA-";
    
    public InvoiceServiceImpl() {
        // Create invoices directory if it doesn't exist
        createInvoiceDirectory();
    }
    
    @Override
    public InvoiceResponseDTO generateInvoice(InvoiceRequestDTO request) {
        logger.info("Generating invoice for order: {}", request.getOrderNo());
        
        try {
            // Generate unique invoice number
            String invoiceNo = generateInvoiceNumber();
            logger.info("Generated invoice number: {}", invoiceNo);
            
            // Generate PDF byte array
            byte[] pdfBytes = InvoicePdfBuilder.generatePdf(request, invoiceNo);
            
            // Save PDF to file
            String filePath = savePdfToFile(pdfBytes, invoiceNo);
            logger.info("PDF saved to: {}", filePath);
            
            // Convert PDF to Base64
            String pdfBase64 = Base64.getEncoder().encodeToString(pdfBytes);
            
            // Build response
            InvoiceResponseDTO response = new InvoiceResponseDTO();
            response.setResponseCode(0);
            response.setResponseMessage("Success");
            response.setInvoiceNo(invoiceNo);
            response.setPdfBase64(pdfBase64);
            response.setFilePath(filePath);
            
            logger.info("Invoice generation completed successfully: {}", invoiceNo);
            return response;
            
        } catch (Exception e) {
            logger.error("Error generating invoice for order: " + request.getOrderNo(), e);
            throw new RuntimeException("Failed to generate invoice: " + e.getMessage(), e);
        }
    }
    
    /**
     * Generate unique invoice number
     * Format: INV-NYKA-YYYYMMDDHHMMSS
     */
    private String generateInvoiceNumber() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = now.format(formatter);
        return INVOICE_PREFIX + timestamp;
    }
    
    /**
     * Save PDF to file system
     */
    private String savePdfToFile(byte[] pdfBytes, String invoiceNo) throws IOException {
        String fileName = invoiceNo + ".pdf";
        String filePath = INVOICE_DIR + "/" + fileName;
        
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(pdfBytes);
            fos.flush();
        }
        
        return filePath;
    }
    
    /**
     * Create invoices directory if it doesn't exist
     */
    private void createInvoiceDirectory() {
        File directory = new File(INVOICE_DIR);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (created) {
                logger.info("Created invoices directory: {}", INVOICE_DIR);
            } else {
                logger.warn("Failed to create invoices directory: {}", INVOICE_DIR);
            }
        }
    }
}

