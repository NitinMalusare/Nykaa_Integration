package com.eshop.auth.service;

import com.eshop.auth.dto.OrderDTO;
import com.eshop.auth.dto.OrderListRequestDTO;
import com.eshop.auth.dto.OrderListResponseDTO;
import com.eshop.auth.entity.Order;
import com.eshop.auth.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public OrderListResponseDTO getOrderList(OrderListRequestDTO requestDTO, String sellerType) {
        logger.info("Processing order list request for seller type: {}, request: {}", sellerType, requestDTO);
        
        OrderListResponseDTO response = new OrderListResponseDTO();
        
        try {
            // Parse update date if provided
            LocalDateTime updateDate = null;
            if (requestDTO.getUpdateDate() != null) {
                updateDate = parseUpdateDate(requestDTO.getUpdateDate());
            }
            
            // Convert status list to string list
            List<String> statusList = null;
            if (requestDTO.getFilterStatusList() != null && !requestDTO.getFilterStatusList().isEmpty()) {
                statusList = requestDTO.getFilterStatusList().stream()
                    .map(String::valueOf)
                    .collect(Collectors.toList());
            }
            
            // Fetch orders from database using repository
            List<Order> orders = orderRepository.findOrdersBySellerTypeWithFilters(
                sellerType,
                requestDTO.getSellerId(),
                updateDate,
                statusList
            );
            
            // Apply pagination
            List<Order> paginatedOrders = applyPagination(orders, requestDTO);
            
            // Convert entities to DTOs
            List<OrderDTO> orderDTOs = convertToDTOs(paginatedOrders);
            
            response.setResponseCode(0);
            response.setResponseMessage("Success");
            response.setOrderList(orderDTOs);
            
            logger.info("Successfully processed order list request for {} seller, returning {} orders out of {} total", 
                       sellerType, orderDTOs.size(), orders.size());
            
        } catch (Exception e) {
            logger.error("Error processing order list request for seller type: " + sellerType, e);
            response.setResponseCode(500);
            response.setResponseMessage("Internal server error: " + e.getMessage());
            response.setOrderList(new ArrayList<>());
        }
        
        return response;
    }
    
    /**
     * Parse update date from string format
     */
    private LocalDateTime parseUpdateDate(String updateDateStr) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            return LocalDateTime.parse(updateDateStr, formatter);
        } catch (Exception e) {
            logger.error("Error parsing update date: {}", updateDateStr, e);
            throw new IllegalArgumentException("Invalid date format. Expected: yyyy-MM-ddTHH:mm:ss");
        }
    }
    
    /**
     * Convert Order entities to OrderDTOs
     */
    private List<OrderDTO> convertToDTOs(List<Order> orders) {
        return orders.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    /**
     * Convert single Order entity to OrderDTO
     */
    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setOrderNo(order.getOrderNo());
        dto.setOrderStatus(order.getOrderStatus());
        dto.setOrderDate(formatDateForResponse(order.getOrderDate()));
        dto.setOrderLastUpdateDate(formatDateForResponse(order.getOrderLastUpdateDate()));
        dto.setOnHold(order.getOnHold());
        dto.setSellerId(order.getSellerId());
        return dto;
    }
    
    /**
     * Format LocalDateTime to string format for response
     */
    private String formatDateForResponse(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return dateTime.format(formatter);
    }
    
    /**
     * Apply pagination to filtered results
     */
    private List<Order> applyPagination(List<Order> orders, OrderListRequestDTO requestDTO) {
        int pageNumber = requestDTO.getPageNumber() != null ? requestDTO.getPageNumber() : 1;
        int limit = requestDTO.getLimit() != null ? requestDTO.getLimit() : 100;
        
        // Calculate start and end indices
        int startIndex = (pageNumber - 1) * limit;
        int endIndex = Math.min(startIndex + limit, orders.size());
        
        // Handle case where startIndex is beyond the list size
        if (startIndex >= orders.size()) {
            return new ArrayList<>();
        }
        
        return orders.subList(startIndex, endIndex);
    }
}
