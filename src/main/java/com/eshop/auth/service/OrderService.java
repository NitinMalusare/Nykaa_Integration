package com.eshop.auth.service;

import com.eshop.auth.dto.OrderListRequestDTO;
import com.eshop.auth.dto.OrderListResponseDTO;
import com.eshop.auth.dto.OrderFetchRequestDTO;
import com.eshop.auth.dto.OrderFetchResponseDTO;

public interface OrderService {
    OrderListResponseDTO getOrderList(OrderListRequestDTO requestDTO, String sellerType);
    OrderFetchResponseDTO getOrderDetails(OrderFetchRequestDTO requestDTO, String sellerType);
}
