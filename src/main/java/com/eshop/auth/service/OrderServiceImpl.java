package com.eshop.auth.service;

import com.eshop.auth.dto.*;
import com.eshop.auth.entity.Order;
import com.eshop.auth.entity.OrderItem;
import com.eshop.auth.repository.OrderRepository;
import com.eshop.auth.repository.OrderItemRepository;
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
    
    @Autowired
    private OrderItemRepository orderItemRepository;

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
    
    @Override
    public OrderFetchResponseDTO getOrderDetails(OrderFetchRequestDTO requestDTO, String sellerType) {
        logger.info("Processing order fetch request for seller type: {}, order numbers: {}", 
                   sellerType, requestDTO.getOrderNo());
        
        OrderFetchResponseDTO response = new OrderFetchResponseDTO();
        
        try {
            // Fetch orders by order numbers
            List<Order> orders = orderRepository.findByOrderNoIn(requestDTO.getOrderNo());
            
            // Filter by seller type
            List<Order> filteredOrders = orders.stream()
                .filter(order -> order.getSellerType() != null && order.getSellerType().equals(sellerType))
                .collect(Collectors.toList());
            
            // Fetch order items for all orders
            List<String> orderNos = filteredOrders.stream()
                .map(Order::getOrderNo)
                .collect(Collectors.toList());
            
            List<OrderItem> allOrderItems = orderItemRepository.findByOrderNoIn(orderNos);
            
            // Convert to DTOs
            List<OrderDetailDTO> orderDetailDTOs = convertToOrderDetailDTOs(filteredOrders, allOrderItems);
            
            response.setResponseCode(0);
            response.setResponseMessage("Success");
            response.setOrders(orderDetailDTOs);
            
            logger.info("Successfully processed order fetch request for {} seller, returning {} orders", 
                       sellerType, orderDetailDTOs.size());
            
        } catch (Exception e) {
            logger.error("Error processing order fetch request for seller type: " + sellerType, e);
            response.setResponseCode(500);
            response.setResponseMessage("Internal server error: " + e.getMessage());
            response.setOrders(new ArrayList<>());
        }
        
        return response;
    }
    
    /**
     * Convert Order entities to OrderDetailDTOs with order items
     */
    private List<OrderDetailDTO> convertToOrderDetailDTOs(List<Order> orders, List<OrderItem> allOrderItems) {
        return orders.stream()
            .map(order -> convertToOrderDetailDTO(order, allOrderItems))
            .collect(Collectors.toList());
    }
    
    /**
     * Convert single Order entity to OrderDetailDTO with associated order items
     */
    private OrderDetailDTO convertToOrderDetailDTO(Order order, List<OrderItem> allOrderItems) {
        OrderDetailDTO dto = new OrderDetailDTO();
        
        dto.setOrderNo(order.getOrderNo());
        dto.setOrderStatus(order.getOrderStatus());
        dto.setOrderDate(formatDateForResponse(order.getOrderDate()));
        dto.setSla(order.getSla());
        dto.setCustomerName(order.getCustomerName());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setOrderAmount(order.getOrderAmount());
        dto.setOrderCurrency(order.getOrderCurrency());
        dto.setOrderTaxAmount(order.getOrderTaxAmount());
        dto.setOnHold(order.getOnHold());
        dto.setShippingCharge(order.getShippingCharge());
        dto.setDiscountAmount(order.getDiscountAmount());
        dto.setDiscCouponCode(order.getDiscCouponCode());
        dto.setStoreCredit(order.getStoreCredit());
        dto.setGvAmount(order.getGvAmount());
        dto.setCodCharge(order.getCodCharge());
        dto.setAddress1(order.getAddress1());
        dto.setAddress2(order.getAddress2());
        dto.setAddress3(order.getAddress3());
        dto.setPhone(order.getPhone());
        dto.setEmail(order.getEmail());
        dto.setCountryCode(order.getCountryCode());
        dto.setState(order.getState());
        dto.setCity(order.getCity());
        dto.setPinCode(order.getPinCode());
        dto.setBillToName(order.getBillToName());
        dto.setBillAddress1(order.getBillAddress1());
        dto.setBillAddress2(order.getBillAddress2());
        dto.setBillAddress3(order.getBillAddress3());
        dto.setBillPhone(order.getBillPhone());
        dto.setBillEmail(order.getBillEmail());
        dto.setBillCountryCode(order.getBillCountryCode());
        dto.setBillState(order.getBillState());
        dto.setBillCity(order.getBillCity());
        dto.setBillZipCode(order.getBillZipCode());
        dto.setCancelRemark(order.getCancelRemark());
        dto.setReasonForCancellation(order.getReasonForCancellation());
        dto.setPriority(order.getPriority());
        dto.setOrderLastUpdateDate(formatDateForResponse(order.getOrderLastUpdateDate()));
        dto.setMasterOrderNo(order.getMasterOrderNo());
        dto.setGstin(order.getGstin());
        dto.setSellerId(order.getSellerId());
        
        // Add order items for this order
        List<OrderItem> orderItems = allOrderItems.stream()
            .filter(item -> item.getOrderNo().equals(order.getOrderNo()))
            .collect(Collectors.toList());
        
        List<OrderItemDTO> orderItemDTOs = orderItems.stream()
            .map(this::convertToOrderItemDTO)
            .collect(Collectors.toList());
        
        dto.setOrderItems(orderItemDTOs);
        
        return dto;
    }
    
    /**
     * Convert OrderItem entity to OrderItemDTO
     */
    private OrderItemDTO convertToOrderItemDTO(OrderItem item) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setLineNo(item.getLineNo());
        dto.setDeliveryMode(item.getDeliveryMode());
        dto.setSkuCode(item.getSkuCode());
        dto.setSkuName(item.getSkuName());
        dto.setOrderQty(item.getOrderQty());
        dto.setRejectedQty(item.getRejectedQty());
        dto.setCancelledQty(item.getCancelledQty());
        dto.setShippedQty(item.getShippedQty());
        dto.setReturnedQty(item.getReturnedQty());
        dto.setDeliveredQty(item.getDeliveredQty());
        dto.setLineAmount(item.getLineAmount());
        dto.setLineTaxAmount(item.getLineTaxAmount());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setMrp(item.getMrp());
        dto.setDiscountAmount(item.getDiscountAmount());
        dto.setShippingCharge(item.getShippingCharge());
        dto.setCodCharge(item.getCodCharge());
        dto.setInvoiceNo(item.getInvoiceNo());
        dto.setTransCode(item.getTransCode());
        dto.setTransName(item.getTransName());
        dto.setAwbNo(item.getAwbNo());
        dto.setImeiNos(item.getImeiNos());
        dto.setConfirmDate(item.getConfirmDate() != null ? formatDateForResponse(item.getConfirmDate()) : null);
        dto.setGvAmount(item.getGvAmount());
        dto.setStoreCredit(item.getStoreCredit());
        dto.setLineStatus(item.getLineStatus());
        dto.setCst(item.getCst());
        dto.setVat(item.getVat());
        return dto;
    }
}
