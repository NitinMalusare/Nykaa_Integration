package com.eshop.auth.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.List;

@Data
public class OrderFetchRequestDTO {
    
    @NotEmpty(message = "Order numbers list cannot be empty")
    @Size(max = 50, message = "Cannot fetch more than 50 orders at once")
    private List<String> orderNo;
}

