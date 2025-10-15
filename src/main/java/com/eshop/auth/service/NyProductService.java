package com.eshop.auth.service;

import com.eshop.auth.dto.NyProductCreateRequestDto;
import com.eshop.auth.dto.NyProductRequestDto;
import com.eshop.auth.dto.NyProductResponseDto;
import jakarta.validation.Valid;

public interface NyProductService {
    NyProductResponseDto createProducts(NyProductCreateRequestDto requestDto, String token);
    NyProductResponseDto fetchProducts(NyProductRequestDto requestDto, String token);
}
