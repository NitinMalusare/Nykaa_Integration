package com.eshop.auth.controller;

import com.eshop.auth.dto.*;
import com.eshop.auth.service.InvoiceService;
import com.eshop.auth.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/RestWS/api/sellerPanel/v3")
@Tag(name = "Order Management", description = "APIs for managing orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private InvoiceService invoiceService;

    @PostMapping("/order/invoice/json")
    @Operation(summary = "Generate invoice in JSON format",
            description = "Generates a detailed invoice in JSON format based on the provided order data.")
    public ResponseEntity<InvoiceJsonDTO> generateInvoiceJson(
            @RequestHeader("apiKey") String apiKey,
            @Valid @RequestBody InvoiceRequestDTO requestDTO) {
        logger.info("JSON Invoice request received for order: {}", requestDTO.getOrderNo());
        try {
            InvoiceJsonDTO jsonInvoice = invoiceService.generateInvoiceJson(requestDTO);
            return ResponseEntity.ok(jsonInvoice);
        } catch (Exception e) {
            logger.error("Error generating JSON invoice for order: {}", requestDTO.getOrderNo(), e);
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/orderList")
    @Operation(summary = "Fetch order list for Dropship sellers",
               description = "Fetches list of orders created after a specific date for Dropship sellers")
    public ResponseEntity<OrderListResponseDTO> getOrderList(
            @RequestHeader("apiKey") String apiKey,
            @Valid @RequestBody OrderListRequestDTO requestDTO) {
        
        try {
            logger.info("Received order list request for Dropship seller: {}", requestDTO);
            
            if (requestDTO.getUpdateDate() == null && 
                (requestDTO.getFilterStatusList() == null || requestDTO.getFilterStatusList().isEmpty())) {
                OrderListResponseDTO errorResponse = new OrderListResponseDTO();
                errorResponse.setResponseCode(400);
                errorResponse.setResponseMessage("Either updateDate or filterStatusList is required");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            OrderListResponseDTO response = orderService.getOrderList(requestDTO, "DROPSHIP");
            logger.info("Completed order list request for Dropship seller, found {} orders", 
                       response.getOrderList() != null ? response.getOrderList().size() : 0);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error processing order list request for Dropship seller", e);
            OrderListResponseDTO errorResponse = new OrderListResponseDTO();
            errorResponse.setResponseCode(500);
            errorResponse.setResponseMessage("An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping("/order/createAndLabel")
    @Operation(summary = "Create order and generate invoice label",
            description = "Creates a minimal order record from invoice request and returns the generated PDF label")
    public ResponseEntity<InvoiceResponseDTO> createOrderAndGenerateLabel(
            @RequestHeader("apiKey") String apiKey,
            @Valid @RequestBody InvoiceRequestDTO requestDTO) {
        logger.info("Create+Label request received for buyer: {}", requestDTO.getBuyerName());
        // Persist order
        String orderNo = orderService.createOrderFromInvoiceRequest(requestDTO);
        // Ensure the same orderNo flows into invoice
        requestDTO.setOrderNo(orderNo);
        // Generate invoice PDF
        InvoiceResponseDTO invoice = invoiceService.generateInvoice(requestDTO);
        return ResponseEntity.ok(invoice);
    }

    @PostMapping("/orderListJIT")
    @Operation(summary = "Fetch order list for JIT sellers", 
               description = "Fetches list of orders created after a specific date for JIT sellers")
    public ResponseEntity<OrderListResponseDTO> getOrderListJIT(
            @RequestHeader("apiKey") String apiKey,
            @Valid @RequestBody OrderListRequestDTO requestDTO) {
        
        try {
            logger.info("Received order list request for JIT seller: {}", requestDTO);
            
            if (requestDTO.getUpdateDate() == null && 
                (requestDTO.getFilterStatusList() == null || requestDTO.getFilterStatusList().isEmpty())) {
                OrderListResponseDTO errorResponse = new OrderListResponseDTO();
                errorResponse.setResponseCode(400);
                errorResponse.setResponseMessage("Either updateDate or filterStatusList is required");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            OrderListResponseDTO response = orderService.getOrderList(requestDTO, "JIT");
            logger.info("Completed order list request for JIT seller, found {} orders", 
                       response.getOrderList() != null ? response.getOrderList().size() : 0);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error processing order list request for JIT seller", e);
            OrderListResponseDTO errorResponse = new OrderListResponseDTO();
            errorResponse.setResponseCode(500);
            errorResponse.setResponseMessage("An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    @PostMapping("/orderFetch")
    @Operation(summary = "Fetch order details for Dropship sellers", 
               description = "Fetches detailed information about specific orders by order numbers for Dropship sellers")
    public ResponseEntity<OrderFetchResponseDTO> getOrderDetails(
            @RequestHeader("apiKey") String apiKey,
            @Valid @RequestBody OrderFetchRequestDTO requestDTO) {
        
        try {
            logger.info("Received order fetch request for Dropship seller: order numbers {}", requestDTO.getOrderNo());
            
            OrderFetchResponseDTO response = orderService.getOrderDetails(requestDTO, "DROPSHIP");
            
            logger.info("Completed order fetch request for Dropship seller, returning {} orders", 
                       response.getOrders() != null ? response.getOrders().size() : 0);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error processing order fetch request for Dropship seller", e);
            OrderFetchResponseDTO errorResponse = new OrderFetchResponseDTO();
            errorResponse.setResponseCode(500);
            errorResponse.setResponseMessage("An unexpected error occurred: " + e.getMessage());
            errorResponse.setOrders(null);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    @PostMapping("/orderFetchJIT")
    @Operation(summary = "Fetch order details for JIT sellers", 
               description = "Fetches detailed information about specific orders by order numbers for JIT sellers")
    public ResponseEntity<OrderFetchResponseDTO> getOrderDetailsJIT(
            @RequestHeader("apiKey") String apiKey,
            @Valid @RequestBody OrderFetchRequestDTO requestDTO) {
        
        try {
            logger.info("Received order fetch request for JIT seller: order numbers {}", requestDTO.getOrderNo());
            
            OrderFetchResponseDTO response = orderService.getOrderDetails(requestDTO, "JIT");
            
            logger.info("Completed order fetch request for JIT seller, returning {} orders", 
                       response.getOrders() != null ? response.getOrders().size() : 0);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error processing order fetch request for JIT seller", e);
            OrderFetchResponseDTO errorResponse = new OrderFetchResponseDTO();
            errorResponse.setResponseCode(500);
            errorResponse.setResponseMessage("An unexpected error occurred: " + e.getMessage());
            errorResponse.setOrders(null);
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}
