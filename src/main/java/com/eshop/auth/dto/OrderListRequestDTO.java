package com.eshop.auth.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import java.util.List;

@Data
public class OrderListRequestDTO {
    
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}$", 
             message = "Invalid date format. Expected format: yyyy-MM-ddTHH:mm:ss")
    private String updateDate;
    
    private List<Integer> filterStatusList;
    
    @Min(value = 1, message = "Page number must be at least 1")
    private Integer pageNumber = 1;
    
    @Min(value = 1, message = "Limit must be at least 1")
    @Max(value = 200, message = "Limit cannot exceed 200")
    private Integer limit = 100;
    
    @Pattern(regexp = "^[A-Z0-9]{1,20}$", message = "Invalid seller ID format")
    private String sellerId;
}
