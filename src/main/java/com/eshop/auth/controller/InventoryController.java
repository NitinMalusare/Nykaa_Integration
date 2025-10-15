package com.eshop.auth.controller;

import com.eshop.auth.dto.InventoryPriceUpdateRequestDTO;
import com.eshop.auth.dto.InventoryPriceUpdateResponseDTO;
import com.eshop.auth.service.InventoryService;
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
@Tag(name = "Inventory Management", description = "APIs for managing inventory")
public class InventoryController {

    private static final Logger logger = LoggerFactory.getLogger(InventoryController.class);

    @Autowired
    private InventoryService inventoryService;

    @PostMapping("/inventoryPriceUpdate")
    @Operation(summary = "Update inventory prices", description = "Updates inventory quantities for one or more products")
    public ResponseEntity<InventoryPriceUpdateResponseDTO> updateInventoryPrices(
            @RequestHeader("apiKey") String apiKey,
            @RequestHeader("token") String token,
            @Valid @RequestBody InventoryPriceUpdateRequestDTO requestDTO) {
        
        try {
            logger.info("Received inventory price update request for token: {}, request: {}", token, requestDTO);
            
            if (requestDTO == null) {
                logger.error("Request body is null");
                return createErrorResponse(400, "Request body cannot be null");
            }
            
            if (requestDTO.getInvPriceList() == null || requestDTO.getInvPriceList().isEmpty()) {
                logger.error("Inventory list is empty for token: {}", token);
                return createErrorResponse(400, "Inventory list cannot be empty");
            }

            InventoryPriceUpdateResponseDTO response = inventoryService.updateInventoryPrices(requestDTO, token);
            logger.info("Completed inventory price update for token: {}, response: {}", token, response);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error processing inventory update request for token: " + token, e);
            return createErrorResponse(500, "An unexpected error occurred: " + e.getMessage());
        }
    }

    private ResponseEntity<InventoryPriceUpdateResponseDTO> createErrorResponse(int code, String message) {
        InventoryPriceUpdateResponseDTO errorResponse = new InventoryPriceUpdateResponseDTO();
        errorResponse.setResponseCode(code);
        errorResponse.setResponseMessage(message);
        errorResponse.setSkus(null);
        return ResponseEntity.status(code).body(errorResponse);
    }
}
