package com.eshop.auth.controller;

import com.eshop.auth.dto.NyProductCreateRequestDto;
import com.eshop.auth.dto.NyProductRequestDto;
import com.eshop.auth.dto.NyProductResponseDto;
import com.eshop.auth.service.NyProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/RestWS/api/sellerPanel/v3")
@Validated
@RequiredArgsConstructor // Lombok generates constructor
@Tag(name = "Product Management", description = "APIs for managing products")
public class NyProductController {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final int DEFAULT_PAGE_SIZE = 100;
    private static final int MAX_PAGE_SIZE = 1000;

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

    @GetMapping("/productFetch")
    @Operation(summary = "Fetch products", description = "Retrieves products with optional filtering and pagination")
    public ResponseEntity<NyProductResponseDto> fetchProducts(
            @Parameter(description = "Filter by update date (format: dd/MM/yyyy HH:mm:ss)")
            @RequestParam(required = false) String updatedDate,

            @Parameter(description = "Filter by SKU codes")
            @RequestParam(required = false) List<String> skuCode,

            @Parameter(description = "Page number (starts from 1)")
            @RequestParam(defaultValue = "1")
            @Min(1) Integer pageNumber,

            @Parameter(description = "Number of items per page")
            @RequestParam(defaultValue = "100")
            @Min(1) @Max(1000) Integer limit,

            @RequestHeader("apiKey") String token) {

        log.info("Product fetch request - page: {}, limit: {}, updatedDate: {}",
                pageNumber, limit, updatedDate);

        // Validate date format if provided
        if (!isValidDateFormat(updatedDate)) {
            return ResponseEntity.badRequest().body(
                    createErrorResponse(400, "Invalid date format. Expected format: dd/MM/yyyy HH:mm:ss"));
        }

        NyProductRequestDto requestDto = NyProductRequestDto.builder()
                .updatedDate(updatedDate)
                .skuCode(skuCode)
                .pageNumber(pageNumber)
                .limit(limit)
                .build();

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