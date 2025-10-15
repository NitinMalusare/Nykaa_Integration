package com.eshop.auth.controller;

import com.eshop.auth.dto.NyProductCreateRequestDto;
import com.eshop.auth.dto.NyProductRequestDto;
import com.eshop.auth.dto.NyProductResponseDto;
import com.eshop.auth.service.NyProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/RestWS/api/sellerPanel/v3")
@Validated
@Tag(name = "Product Management", description = "APIs for managing products")
public class NyProductController {

    @Autowired
    private NyProductService nyProductService;

    @PostMapping("/productCreate")
    @Operation(summary = "Create new products", description = "Creates one or more products with the provided details")
    public ResponseEntity<NyProductResponseDto> createProducts(
            @Valid @RequestBody NyProductCreateRequestDto requestDto,
            @RequestHeader("apiKey") String token) {
        
        if (requestDto.getProducts() == null || requestDto.getProducts().isEmpty()) {
            NyProductResponseDto errorResponse = new NyProductResponseDto();
            errorResponse.setResponseCode(400);
            errorResponse.setResponseMessage("Product list cannot be empty");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        NyProductResponseDto response = nyProductService.createProducts(requestDto, token);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/productFetch")
    @Operation(summary = "Fetch products", description = "Retrieves products with optional filtering and pagination")
    public ResponseEntity<NyProductResponseDto> fetchProducts(
            @Parameter(description = "Filter by update date (format: dd/MM/yyyy HH:mm:ss)") 
            @RequestParam(required = false) String updatedDate,
            
            @Parameter(description = "Filter by SKU codes") 
            @RequestParam(required = false) List<String> skuCode,
            
            @Parameter(description = "Page number (starts from 1)") 
            @RequestParam(defaultValue = "1") Integer pageNumber,
            
            @Parameter(description = "Number of items per page") 
            @RequestParam(defaultValue = "100") Integer limit,
            
            @RequestHeader("apiKey") String token)

    {
        
        // Validate pagination parameters
        if (pageNumber < 1) {
            pageNumber = 1;
        }
        if (limit < 1 || limit > 1000) {
            limit = 100;
        }

        // Validate date format if provided
        if (updatedDate != null && !updatedDate.matches("\\d{2}/\\d{2}/\\d{4} \\d{2}:\\d{2}:\\d{2}")) {
            NyProductResponseDto errorResponse = new NyProductResponseDto();
            errorResponse.setResponseCode(400);
            errorResponse.setResponseMessage("Invalid date format. Expected format: dd/MM/yyyy HH:mm:ss");
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        NyProductRequestDto requestDto = new NyProductRequestDto();
        requestDto.setUpdatedDate(updatedDate);
        requestDto.setSkuCode(skuCode);
        requestDto.setPageNumber(pageNumber);
        requestDto.setLimit(limit);
        
        NyProductResponseDto response = nyProductService.fetchProducts(requestDto, token);
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<NyProductResponseDto> handleException(Exception e) {
        NyProductResponseDto errorResponse = new NyProductResponseDto();
        errorResponse.setResponseCode(500);
        errorResponse.setResponseMessage("Internal server error: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
