package com.eshop.auth.controller;

import com.eshop.auth.dto.NyProductCreateRequestDto;
import com.eshop.auth.dto.NyProductRequestDto;
import com.eshop.auth.dto.NyProductResponseDto;
import com.eshop.auth.service.NyProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@RestController
@RequestMapping("/RestWS/api/sellerPanel/v3")
@Validated
@RequiredArgsConstructor // Lombok generates constructor
@Tag(name = "Product Management", description = "APIs for managing products")
public class NyProductController {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private final NyProductService nyProductService;

    @PostMapping("/productCreate")
    @Operation(summary = "Create new products", description = "Creates one or more products with the provided details")
    public ResponseEntity<NyProductResponseDto> createProducts(
            @Valid @RequestBody NyProductCreateRequestDto requestDto,
            @RequestHeader("apiKey") String token) {

        log.info("Product creation request received for token: {}", token);

        if (requestDto.getProducts() == null || requestDto.getProducts().isEmpty()) {
            log.warn("Empty product list in creation request");
            return ResponseEntity.badRequest().body(
                    createErrorResponse(400, "Product list cannot be empty"));
        }

        try {
            NyProductResponseDto response = nyProductService.createProducts(requestDto, token);
            log.info("Successfully created {} products", requestDto.getProducts().size());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            log.error("Error creating products", e);
            throw e; // Let exception handler deal with it
        }
    }

    @PostMapping("/productFetch")
    @Operation(summary = "Fetch products", description = "Retrieves products with optional filtering and pagination")
    public ResponseEntity<NyProductResponseDto> fetchProducts(
            @RequestBody(required = false) NyProductRequestDto requestDto,
            @RequestHeader("apiKey") String token) {

        // Handle empty request body - use default values
        if (requestDto == null) {
            requestDto = NyProductRequestDto.builder()
                    .pageNumber(1)
                    .limit(100)
                    .build();
        }

        // Set defaults if not provided
        if (requestDto.getPageNumber() == null) {
            requestDto.setPageNumber(1);
        }
        if (requestDto.getLimit() == null) {
            requestDto.setLimit(100);
        }

        log.info("Product fetch request - page: {}, limit: {}, updatedDate: {}, skuCodes: {}",
                requestDto.getPageNumber(), requestDto.getLimit(), 
                requestDto.getUpdatedDate(), requestDto.getSkuCode());

        // Validate date format if provided
        if (requestDto.getUpdatedDate() != null && !isValidDateFormat(requestDto.getUpdatedDate())) {
            return ResponseEntity.badRequest().body(
                    createErrorResponse(400, "Invalid date format. Expected format: dd/MM/yyyy HH:mm:ss"));
        }

        NyProductResponseDto response = nyProductService.fetchProducts(requestDto, token);
        return ResponseEntity.ok(response);
    }

    private boolean isValidDateFormat(String dateStr) {
        if (dateStr == null) {
            return true;
        }

        try {
            LocalDateTime.parse(dateStr, DATE_FORMATTER);
            return true;
        } catch (Exception e) {
            log.warn("Invalid date format: {}", dateStr);
            return false;
        }
    }

    private NyProductResponseDto createErrorResponse(int code, String message) {
        NyProductResponseDto errorResponse = new NyProductResponseDto();
        errorResponse.setResponseCode(code);
        errorResponse.setResponseMessage(message);
        return errorResponse;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<NyProductResponseDto> handleException(Exception e) {
        log.error("Unhandled exception in ProductController", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse(500, "Internal server error: " + e.getMessage()));
    }
}