package com.eshop.auth.service;

import com.eshop.auth.dto.OrderListRequestDTO;
import com.eshop.auth.dto.OrderListResponseDTO;

public interface OrderService {
    OrderListResponseDTO getOrderList(OrderListRequestDTO requestDTO, String sellerType);
}
