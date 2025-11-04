package com.eshop.auth.service;

import com.eshop.auth.dto.OrderListRequestDTO;
import com.eshop.auth.dto.OrderListResponseDTO;
import com.eshop.auth.dto.OrderFetchRequestDTO;
import com.eshop.auth.dto.OrderFetchResponseDTO;
import com.eshop.auth.dto.InvoiceRequestDTO;

public interface OrderService {
    OrderListResponseDTO getOrderList(OrderListRequestDTO requestDTO, String sellerType);
    OrderFetchResponseDTO getOrderDetails(OrderFetchRequestDTO requestDTO, String sellerType);

    /**
     * Create a minimal order record from invoice request context and return the generated order number.
     */
    String createOrderFromInvoiceRequest(InvoiceRequestDTO requestDTO);
}
