package com.eshop.auth.controller;

import com.eshop.auth.dto.*;
import com.eshop.auth.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/RestWS/api/sellerPanel/v3")
@RequiredArgsConstructor // Lombok generates constructor
@Tag(name = "Order Management", description = "APIs for managing orders")
public class OrderController {

    private final OrderService orderService;

    private static final String SELLER_TYPE_DROPSHIP = "DROPSHIP";
    private static final String SELLER_TYPE_JIT = "JIT";

    @PostMapping("/orderList")
    @Operation(summary = "Fetch order list for Dropship sellers",
            description = "Fetches list of orders created after a specific date for Dropship sellers")
    public ResponseEntity<OrderListResponseDTO> getOrderList(
            @RequestHeader("apiKey") String apiKey,
            @Valid @RequestBody OrderListRequestDTO requestDTO) {
        return processOrderListRequest(requestDTO, SELLER_TYPE_DROPSHIP);
    }

    @PostMapping("/orderListJIT")
    @Operation(summary = "Fetch order list for JIT sellers",
            description = "Fetches list of orders created after a specific date for JIT sellers")
    public ResponseEntity<OrderListResponseDTO> getOrderListJIT(
            @RequestHeader("apiKey") String apiKey,
            @Valid @RequestBody OrderListRequestDTO requestDTO) {
        return processOrderListRequest(requestDTO, SELLER_TYPE_JIT);
    }

    @PostMapping("/orderFetch")
    @Operation(summary = "Fetch order details for Dropship sellers",
            description = "Fetches detailed information about specific orders by order numbers for Dropship sellers")
    public ResponseEntity<OrderFetchResponseDTO> getOrderDetails(
            @RequestHeader("apiKey") String apiKey,
            @Valid @RequestBody OrderFetchRequestDTO requestDTO) {
        return processOrderFetchRequest(requestDTO, SELLER_TYPE_DROPSHIP);
    }

    @PostMapping("/orderFetchJIT")
    @Operation(summary = "Fetch order details for JIT sellers",
            description = "Fetches detailed information about specific orders by order numbers for JIT sellers")
    public ResponseEntity<OrderFetchResponseDTO> getOrderDetailsJIT(
            @RequestHeader("apiKey") String apiKey,
            @Valid @RequestBody OrderFetchRequestDTO requestDTO) {
        return processOrderFetchRequest(requestDTO, SELLER_TYPE_JIT);
    }

    // Helper methods to reduce duplication
    private ResponseEntity<OrderListResponseDTO> processOrderListRequest(
            OrderListRequestDTO requestDTO, String sellerType) {

        try {
            log.info("Received order list request for {} seller: {}", sellerType, requestDTO);

            if (!isValidOrderListRequest(requestDTO)) {
                return ResponseEntity.badRequest()
                        .body(createOrderListErrorResponse(400,
                                "Either updateDate or filterStatusList is required"));
            }

            OrderListResponseDTO response = orderService.getOrderList(requestDTO, sellerType);
            log.info("Completed order list request for {} seller, found {} orders",
                    sellerType, response.getOrderList() != null ? response.getOrderList().size() : 0);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error processing order list request for {} seller", sellerType, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createOrderListErrorResponse(500,
                            "An unexpected error occurred: " + e.getMessage()));
        }
    }

    private ResponseEntity<OrderFetchResponseDTO> processOrderFetchRequest(
            OrderFetchRequestDTO requestDTO, String sellerType) {

        try {
            log.info("Received order fetch request for {} seller: order numbers {}",
                    sellerType, requestDTO.getOrderNo());

            OrderFetchResponseDTO response = orderService.getOrderDetails(requestDTO, sellerType);

            log.info("Completed order fetch request for {} seller, returning {} orders",
                    sellerType, response.getOrders() != null ? response.getOrders().size() : 0);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error processing order fetch request for {} seller", sellerType, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createOrderFetchErrorResponse(500,
                            "An unexpected error occurred: " + e.getMessage()));
        }
    }

    private boolean isValidOrderListRequest(OrderListRequestDTO requestDTO) {
        return requestDTO.getUpdateDate() != null ||
                (requestDTO.getFilterStatusList() != null && !requestDTO.getFilterStatusList().isEmpty());
    }

    private OrderListResponseDTO createOrderListErrorResponse(int code, String message) {
        OrderListResponseDTO errorResponse = new OrderListResponseDTO();
        errorResponse.setResponseCode(code);
        errorResponse.setResponseMessage(message);
        return errorResponse;
    }

    private OrderFetchResponseDTO createOrderFetchErrorResponse(int code, String message) {
        OrderFetchResponseDTO errorResponse = new OrderFetchResponseDTO();
        errorResponse.setResponseCode(code);
        errorResponse.setResponseMessage(message);
        errorResponse.setOrders(null);
        return errorResponse;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception e) {
        log.error("Unhandled exception in OrderController", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createOrderListErrorResponse(500, "Internal server error"));
    }
}