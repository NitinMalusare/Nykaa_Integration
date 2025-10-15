package com.eshop.auth.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NyCustomAuthResponse {
    private String status;
    private String apiKey;
    private String apiOwner;
}
