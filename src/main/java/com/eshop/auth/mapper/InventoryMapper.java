package com.eshop.auth.mapper;

import com.eshop.auth.dto.InventoryPriceUpdateRequestDTO;
import com.eshop.auth.dto.InventoryPriceUpdateResponseDTO;
import com.eshop.auth.entity.InventoryPriceUpdateEntity;
import org.springframework.stereotype.Component;

@Component
public class InventoryMapper {

    public InventoryPriceUpdateEntity toEntity(InventoryPriceUpdateRequestDTO.InventoryItemDTO dto, String token) {
        InventoryPriceUpdateEntity entity = new InventoryPriceUpdateEntity();
        entity.setSku(dto.getSku());
        entity.setSellerId(dto.getSellerId());
        entity.setToken(token);
        entity.setInvQty(dto.getInvQty());
        entity.setRequestedInvQty(dto.getInvQty());
        entity.setStatus("SUCCESS");
        entity.setRequestedSalePrice(0.0);
        return entity;
    }

    public InventoryPriceUpdateResponseDTO.SKUResponseDTO toResponseDto(InventoryPriceUpdateEntity entity) {
        InventoryPriceUpdateResponseDTO.SKUResponseDTO dto = new InventoryPriceUpdateResponseDTO.SKUResponseDTO();
        dto.setSku(entity.getSku());
        dto.setSellerId(entity.getSellerId());
        dto.setModerationId(entity.getModerationId());
        dto.setRequestedInvQty(entity.getRequestedInvQty());
        dto.setApprovedInvQty(entity.getApprovedInvQty());
        dto.setStatus(entity.getStatus());
        dto.setRequestedSalePrice(entity.getRequestedSalePrice());
        return dto;
    }
} 