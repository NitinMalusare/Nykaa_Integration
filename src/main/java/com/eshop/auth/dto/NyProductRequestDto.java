package com.eshop.auth.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class NyProductRequestDto {
    private String updatedDate;
    private List<String> skuCode;
    
    @Min(1)
    private Integer pageNumber;
    
    @Min(1)
    @Max(1000)
    private Integer limit;
}