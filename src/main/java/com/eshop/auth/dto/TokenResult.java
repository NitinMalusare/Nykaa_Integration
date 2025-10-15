package com.eshop.auth.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenResult {
    private String accessToken;
    private String refreshToken;
}
