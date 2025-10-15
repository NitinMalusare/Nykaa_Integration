package com.eshop.auth.service;

import com.eshop.auth.dto.InventoryPriceUpdateRequestDTO;
import com.eshop.auth.dto.InventoryPriceUpdateResponseDTO;
import com.eshop.auth.entity.InventoryPriceUpdateEntity;
import com.eshop.auth.entity.NyProduct;
import com.eshop.auth.exception.InventoryUpdateException;
import com.eshop.auth.exception.ProductNotFoundException;
import com.eshop.auth.repository.InventoryRepository;
import com.eshop.auth.repository.NyProductRepository;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class InventoryServiceImpl implements InventoryService {

    private static final Logger logger = LoggerFactory.getLogger(InventoryServiceImpl.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private NyProductRepository nyProductRepository;

    @Override
    @Transactional
    public InventoryPriceUpdateResponseDTO updateInventoryPrices(InventoryPriceUpdateRequestDTO requestDTO, String token) {
        logger.info("Starting inventory price update for token: {}", token);
        
        List<InventoryPriceUpdateResponseDTO.SKUResponseDTO> skuResponses = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        try {
            for (InventoryPriceUpdateRequestDTO.InventoryItemDTO item : requestDTO.getInvPriceList()) {
                try {
                    logger.debug("Processing item: SKU={}, SellerId={}", item.getSku(), item.getSellerId());
                    validateAndUpdateInventory(item, token, skuResponses);
                } catch (ProductNotFoundException e) {
                    String errorMessage = String.format("SKU %s not found for seller %s", item.getSku(), item.getSellerId());
                    logger.error(errorMessage);
                    errors.add(errorMessage);
                    logInventoryUpdate("ERROR", token, item.getSku(), item.getSellerId(), 
                        item.getInvQty(), null, errorMessage);
                } catch (Exception e) {
                    String errorMessage = String.format("Error processing SKU %s: %s", item.getSku(), e.getMessage());
                    logger.error(errorMessage, e);
                    errors.add(errorMessage);
                    logInventoryUpdate("ERROR", token, item.getSku(), item.getSellerId(), 
                        item.getInvQty(), null, errorMessage);
                }
            }

            InventoryPriceUpdateResponseDTO response = new InventoryPriceUpdateResponseDTO();
            if (!errors.isEmpty()) {
                response.setResponseCode(304);
                response.setResponseMessage(String.join("; ", errors));
            } else {
                response.setResponseCode(0);
                response.setResponseMessage("Success");
            }
            response.setSkus(skuResponses);

            logger.info("Completed inventory price update for token: {}, response code: {}", 
                token, response.getResponseCode());
            
            return response;

        } catch (Exception e) {
            logger.error("Unexpected error during inventory update for token: " + token, e);
            throw new InventoryUpdateException("Failed to process inventory update", null, null, token, e);
        }
    }

    private void validateAndUpdateInventory(InventoryPriceUpdateRequestDTO.InventoryItemDTO item, String token,
            List<InventoryPriceUpdateResponseDTO.SKUResponseDTO> skuResponses) {
        logger.debug("Validating and updating inventory for SKU: {}, SellerId: {}", item.getSku(), item.getSellerId());

        // Check if product exists
        Optional<NyProduct> product = nyProductRepository.findBySkuAndToken(item.getSku(), token);
        if (product.isEmpty()) {
            throw new ProductNotFoundException("SKU " + item.getSku() + " not found for seller " + item.getSellerId());
        }

        // Create or update inventory
        InventoryPriceUpdateEntity inventory = inventoryRepository
            .findBySkuAndSellerIdAndToken(item.getSku(), item.getSellerId(), token)
            .orElse(new InventoryPriceUpdateEntity());

        try {
            Integer invQty = item.getInvQty();
            inventory.setSku(item.getSku());
            inventory.setSellerId(item.getSellerId());
            inventory.setToken(token);
            inventory.setInvQty(invQty);
            inventory.setRequestedInvQty(invQty);
            inventory.setStatus("SUCCESS");
            inventory.setRequestedSalePrice(0.0);

            InventoryPriceUpdateEntity savedInventory = inventoryRepository.save(inventory);
            logger.debug("Successfully saved inventory for SKU: {}", item.getSku());

            // Create response
            InventoryPriceUpdateResponseDTO.SKUResponseDTO skuResponse = new InventoryPriceUpdateResponseDTO.SKUResponseDTO();
            skuResponse.setSku(savedInventory.getSku());
            skuResponse.setSellerId(savedInventory.getSellerId());
            skuResponse.setModerationId(savedInventory.getModerationId());
            skuResponse.setRequestedInvQty(savedInventory.getRequestedInvQty());
            skuResponse.setApprovedInvQty(savedInventory.getApprovedInvQty());
            skuResponse.setStatus(savedInventory.getStatus());
            skuResponse.setRequestedSalePrice(savedInventory.getRequestedSalePrice());

            skuResponses.add(skuResponse);

            // Log success
            logInventoryUpdate("SUCCESS", token, item.getSku(), item.getSellerId(), 
                invQty, savedInventory.getModerationId(), "Inventory updated successfully");

        } catch (Exception e) {
            logger.error("Failed to update inventory for SKU: " + item.getSku(), e);
            throw new InventoryUpdateException("Failed to update inventory", item.getSku(), item.getSellerId(), token, e);
        }
    }

    private void logInventoryUpdate(String status, String token, String sku, String sellerId, 
            @NotNull Integer invQty, Long moderationId, String message) {
        String logMessage = String.format(
            "NYKAA_INVENTORY_UPDATE - Status: %s, Token: %s, SKU: %s, SellerId: %s, " +
            "InvQty: %s, ModerationId: %s, Message: %s",
            status, token, sku, sellerId, invQty, moderationId, message
        );
        
        if ("SUCCESS".equals(status)) {
            logger.info(logMessage);
        } else {
            logger.error(logMessage);
        }
    }
}
