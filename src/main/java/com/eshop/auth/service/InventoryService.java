package com.eshop.auth.service;

import com.eshop.auth.dto.InventoryPriceUpdateRequestDTO;
import com.eshop.auth.dto.InventoryPriceUpdateResponseDTO;

public interface InventoryService {
    InventoryPriceUpdateResponseDTO updateInventoryPrices(InventoryPriceUpdateRequestDTO requestDTO, String token);
}
