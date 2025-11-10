package com.eshop.auth.controller;

import com.eshop.auth.dto.InvoiceRequestDTO;
import com.eshop.auth.dto.InvoiceResponseDTO;
import com.eshop.auth.dto.OrderFetchRequestDTO;
import com.eshop.auth.dto.OrderFetchResponseDTO;
import com.eshop.auth.dto.OrderDetailDTO;
import com.eshop.auth.dto.OrderItemDTO;
import com.eshop.auth.service.OrderService;
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
    private final OrderService orderService;
    
    /**
     * Generate invoice PDF and return as file download
     * 
     * @param requestDTO Invoice request with order items and details
     * @return PDF file as binary stream
     */
    @PostMapping("/generateInvoice")
    @Operation(summary = "Generate invoice PDF", 
               description = "Generates a Nykaa-style retail/tax invoice PDF dynamically based on order items and returns the PDF file directly")
    public ResponseEntity<byte[]> generateInvoice(
            @Valid @RequestBody InvoiceRequestDTO requestDTO) {
        
        log.info("Received invoice generation request for order: {}", requestDTO.getOrderNo());
        
        try {
            InvoiceService.InvoicePdfResult result = invoiceService.generateInvoicePdf(requestDTO);
            log.info("Invoice generated successfully: {}", result.getInvoiceNo());
            
            // Return PDF file with appropriate headers for PDF format
            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "inline; filename=\"" + result.getInvoiceNo() + ".pdf\"")
                    .header("Content-Length", String.valueOf(result.getPdfBytes().length))
                    .header("Cache-Control", "no-cache, no-store, must-revalidate")
                    .header("Pragma", "no-cache")
                    .header("Expires", "0")
                    .body(result.getPdfBytes());
            
        } catch (Exception e) {
            log.error("Error generating invoice for order: " + requestDTO.getOrderNo(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Generate invoice PDF and return as JSON response with Base64
     * 
     * @param requestDTO Invoice request with order items and details
     * @return Invoice response with PDF Base64 and file path
     */
    @PostMapping("/generateInvoiceJson")
    @Operation(summary = "Generate invoice PDF (JSON response)", 
               description = "Generates a Nykaa-style retail/tax invoice PDF and returns JSON response with Base64 encoded PDF")
    public ResponseEntity<InvoiceResponseDTO> generateInvoiceJson(
            @Valid @RequestBody InvoiceRequestDTO requestDTO) {
        
        log.info("Received invoice generation request (JSON) for order: {}", requestDTO.getOrderNo());
        
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

    /**
     * Generate invoice PDF from order number (dynamic data from order and order items)
     * 
     * @param orderNo Order number
     * @param token API token for product lookup
     * @return PDF file as binary stream
     */
    @PostMapping("/generateInvoiceFromOrder/{orderNo}")
    @Operation(summary = "Generate invoice PDF from order number", 
               description = "Generates invoice PDF dynamically from existing order and order items. Fetches product details including HSN codes.")
    public ResponseEntity<byte[]> generateInvoiceFromOrder(
            @PathVariable String orderNo,
            @RequestHeader("apiKey") String token) {
        
        log.info("Received invoice generation request for order: {} with token: {}", orderNo, token);
        
        try {
            InvoiceService.InvoicePdfResult result = invoiceService.generateInvoiceFromOrder(orderNo, token);
            log.info("Invoice generated successfully: {} for order: {}", result.getInvoiceNo(), orderNo);
            
            // Return PDF file with appropriate headers for PDF format
            return ResponseEntity.ok()
                    .header("Content-Type", "application/pdf")
                    .header("Content-Disposition", "inline; filename=\"" + result.getInvoiceNo() + ".pdf\"")
                    .header("Content-Length", String.valueOf(result.getPdfBytes().length))
                    .header("Cache-Control", "no-cache, no-store, must-revalidate")
                    .header("Pragma", "no-cache")
                    .header("Expires", "0")
                    .body(result.getPdfBytes());
            
        } catch (Exception e) {
            log.error("Error generating invoice for order: " + orderNo, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/invoice/{orderNo}")
    @Operation(summary = "Fetch order and return invoice PDF stream")
    public ResponseEntity<byte[]> invoiceByOrder(@PathVariable String orderNo) {
        OrderFetchRequestDTO req = new OrderFetchRequestDTO();
        req.setOrderNo(java.util.List.of(orderNo));
        OrderFetchResponseDTO orders = orderService.getOrderDetails(req, "JIT");
        if (orders.getOrders() == null || orders.getOrders().isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        OrderDetailDTO o = orders.getOrders().get(0);
        // Build minimal InvoiceRequestDTO from order
        InvoiceRequestDTO invReq = new InvoiceRequestDTO();
        invReq.setSellerName("Nykaa E-Retail Limited");
        invReq.setSellerAddress(o.getAddress1());
        invReq.setSellerGstin(o.getGstin());
        invReq.setBuyerName(o.getBillToName());
        invReq.setBuyerAddress(o.getBillAddress1());
        invReq.setAwbNo("");
        invReq.setOrderNo(o.getOrderNo());
        invReq.setOrderDate(o.getOrderDate());
        invReq.setPaymentMode(o.getPaymentMethod());
        invReq.setPlaceOfSupply(o.getState());
        // Map first item
        if (o.getOrderItems() != null && !o.getOrderItems().isEmpty()) {
            java.util.List<com.eshop.auth.dto.InvoiceItemDTO> items = new java.util.ArrayList<>();
            for (OrderItemDTO line : o.getOrderItems()) {
                com.eshop.auth.dto.InvoiceItemDTO it = new com.eshop.auth.dto.InvoiceItemDTO();
                it.setOrderNo(o.getOrderNo());
                it.setLineNo(safeInt(line.getLineNo()));
                it.setTransporterName(line.getTransName());
                it.setTrackingNo(line.getAwbNo());
                it.setDescription(line.getSkuName());
                it.setHsn("");
                it.setQty(line.getShippedQty() != null ? safeInt(line.getShippedQty()) : 1);
                it.setUnitPrice(line.getUnitPrice() != null ? safeDouble(line.getUnitPrice()) : 0.0);
                it.setDiscount(line.getDiscountAmount() != null ? safeDouble(line.getDiscountAmount()) : 0.0);
                it.setTaxRate(5.0);
                items.add(it);
            }
            invReq.setOrderItemsList(items);
        }
        InvoiceService.InvoicePdfResult result = invoiceService.generateInvoicePdf(invReq);
        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "inline; filename=\"" + result.getInvoiceNo() + ".pdf\"")
                .header("Content-Length", String.valueOf(result.getPdfBytes().length))
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("Expires", "0")
                .body(result.getPdfBytes());
    }

    private Integer safeInt(String v) {
        try { return v == null || v.isBlank() ? null : Integer.parseInt(v.trim()); } catch (Exception e) { return null; }
    }
    private Double safeDouble(String v) {
        try { return v == null || v.isBlank() ? null : Double.parseDouble(v.trim()); } catch (Exception e) { return null; }
    }
}

