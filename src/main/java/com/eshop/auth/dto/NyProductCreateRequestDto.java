package com.eshop.auth.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.List;

@Data
public class NyProductCreateRequestDto {
    @NotEmpty(message = "Product list cannot be empty")
    @Size(max = 100, message = "Cannot create more than 100 products at once")
    @Valid
    private List<NyProductDto> products;
} 