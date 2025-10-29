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
        dto.setOrderNo(safe(order.getOrderNo()));
        dto.setOrderStatus(safe(order.getOrderStatus()));
        dto.setOrderDate(formatDateForResponseSafe(order.getOrderDate()));
        dto.setOrderLastUpdateDate(formatDateForResponseSafe(order.getOrderLastUpdateDate()));
        dto.setOnHold(safe(order.getOnHold()));
        dto.setSellerId(safe(order.getSellerId()));
        return dto;
    }
    
    /**
     * Format LocalDateTime to string format for response
     */
    private String formatDateForResponse(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return dateTime.format(formatter);
    }

    private String formatDateForResponseSafe(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return formatDateForResponse(dateTime);
    }

    private String safe(String value) {
        return value != null ? value : "";
    }

    private String safeZero(String value) {
        return value != null ? value : "0";
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
        
        dto.setOrderNo(safe(order.getOrderNo()));
        dto.setOrderStatus(safe(order.getOrderStatus()));
        dto.setOrderDate(formatDateForResponseSafe(order.getOrderDate()));
        dto.setSla(safe(order.getSla()));
        dto.setCustomerName(safe(order.getCustomerName()));
        dto.setPaymentMethod(safe(order.getPaymentMethod()));
        dto.setOrderAmount(safeZero(order.getOrderAmount()));
        dto.setOrderCurrency(safe(order.getOrderCurrency()));
        dto.setOrderTaxAmount(safeZero(order.getOrderTaxAmount()));
        dto.setOnHold(safe(order.getOnHold()));
        dto.setShippingCharge(safeZero(order.getShippingCharge()));
        dto.setDiscountAmount(safeZero(order.getDiscountAmount()));
        dto.setDiscCouponCode(safe(order.getDiscCouponCode()));
        dto.setStoreCredit(safeZero(order.getStoreCredit()));
        dto.setGvAmount(safeZero(order.getGvAmount()));
        dto.setCodCharge(safeZero(order.getCodCharge()));
        dto.setAddress1(safe(order.getAddress1()));
        dto.setAddress2(safe(order.getAddress2()));
        dto.setAddress3(safe(order.getAddress3()));
        dto.setPhone(safe(order.getPhone()));
        dto.setEmail(safe(order.getEmail()));
        dto.setCountryCode(safe(order.getCountryCode()));
        dto.setState(safe(order.getState()));
        dto.setCity(safe(order.getCity()));
        dto.setPinCode(safe(order.getPinCode()));
        dto.setBillToName(safe(order.getBillToName()));
        dto.setBillAddress1(safe(order.getBillAddress1()));
        dto.setBillAddress2(safe(order.getBillAddress2()));
        dto.setBillAddress3(safe(order.getBillAddress3()));
        dto.setBillPhone(safe(order.getBillPhone()));
        dto.setBillEmail(safe(order.getBillEmail()));
        dto.setBillCountryCode(safe(order.getBillCountryCode()));
        dto.setBillState(safe(order.getBillState()));
        dto.setBillCity(safe(order.getBillCity()));
        dto.setBillZipCode(safe(order.getBillZipCode()));
        dto.setCancelRemark(safe(order.getCancelRemark()));
        dto.setReasonForCancellation(safe(order.getReasonForCancellation()));
        dto.setPriority(safe(order.getPriority()));
        dto.setOrderLastUpdateDate(formatDateForResponseSafe(order.getOrderLastUpdateDate()));
        dto.setMasterOrderNo(safe(order.getMasterOrderNo()));
        dto.setGstin(safe(order.getGstin()));
        dto.setSellerId(safe(order.getSellerId()));
        
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
        dto.setLineNo(safe(item.getLineNo()));
        dto.setDeliveryMode(safe(item.getDeliveryMode()));
        dto.setSkuCode(safe(item.getSkuCode()));
        dto.setSkuName(safe(item.getSkuName()));
        dto.setOrderQty(safeZero(item.getOrderQty()));
        dto.setRejectedQty(safeZero(item.getRejectedQty()));
        dto.setCancelledQty(safeZero(item.getCancelledQty()));
        dto.setShippedQty(safeZero(item.getShippedQty()));
        dto.setReturnedQty(safeZero(item.getReturnedQty()));
        dto.setDeliveredQty(safeZero(item.getDeliveredQty()));
        dto.setLineAmount(safeZero(item.getLineAmount()));
        dto.setLineTaxAmount(safeZero(item.getLineTaxAmount()));
        dto.setUnitPrice(safeZero(item.getUnitPrice()));
        dto.setMrp(safeZero(item.getMrp()));
        dto.setDiscountAmount(safeZero(item.getDiscountAmount()));
        dto.setShippingCharge(safeZero(item.getShippingCharge()));
        dto.setCodCharge(safeZero(item.getCodCharge()));
        dto.setInvoiceNo(safe(item.getInvoiceNo()));
        dto.setTransCode(safe(item.getTransCode()));
        dto.setTransName(safe(item.getTransName()));
        dto.setAwbNo(safe(item.getAwbNo()));
        dto.setImeiNos(safe(item.getImeiNos()));
        dto.setConfirmDate(formatDateForResponseSafe(item.getConfirmDate()));
        dto.setGvAmount(safeZero(item.getGvAmount()));
        dto.setStoreCredit(safeZero(item.getStoreCredit()));
        dto.setLineStatus(safe(item.getLineStatus()));
        dto.setCst(safe(item.getCst()));
        dto.setVat(safe(item.getVat()));
        return dto;
    }
}
