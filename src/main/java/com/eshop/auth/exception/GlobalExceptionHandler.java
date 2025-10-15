package com.eshop.auth.exception;

import com.eshop.auth.dto.InventoryPriceUpdateResponseDTO;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InventoryUpdateException.class)
    public ResponseEntity<InventoryPriceUpdateResponseDTO> handleInventoryUpdateException(InventoryUpdateException ex) {
        logger.error("Inventory update failed for SKU: {}, Seller: {}, Token: {}, Error: {}", 
            ex.getSku(), ex.getSellerId(), ex.getToken(), ex.getMessage());
        
        InventoryPriceUpdateResponseDTO response = new InventoryPriceUpdateResponseDTO();
        response.setResponseCode(304);
        response.setResponseMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<InventoryPriceUpdateResponseDTO> handleProductNotFoundException(ProductNotFoundException ex) {
        logger.error("Product not found: {}", ex.getMessage());
        
        InventoryPriceUpdateResponseDTO response = new InventoryPriceUpdateResponseDTO();
        response.setResponseCode(404);
        response.setResponseMessage(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<InventoryPriceUpdateResponseDTO> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));
        
        logger.error("Validation failed: {}", errorMessage);
        
        InventoryPriceUpdateResponseDTO response = new InventoryPriceUpdateResponseDTO();
        response.setResponseCode(400);
        response.setResponseMessage(errorMessage);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<InventoryPriceUpdateResponseDTO> handleConstraintViolationException(ConstraintViolationException ex) {
        String errorMessage = ex.getConstraintViolations().stream()
            .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
            .collect(Collectors.joining(", "));
        
        logger.error("Constraint violation: {}", errorMessage);
        
        InventoryPriceUpdateResponseDTO response = new InventoryPriceUpdateResponseDTO();
        response.setResponseCode(400);
        response.setResponseMessage(errorMessage);
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<InventoryPriceUpdateResponseDTO> handleGenericException(Exception ex) {
        logger.error("Unexpected error occurred", ex);
        
        InventoryPriceUpdateResponseDTO response = new InventoryPriceUpdateResponseDTO();
        response.setResponseCode(500);
        response.setResponseMessage("An unexpected error occurred. Please try again later.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
} 