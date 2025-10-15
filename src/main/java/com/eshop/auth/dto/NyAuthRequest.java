package com.eshop.auth.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NyAuthRequest {
    private String username;
    private String password;
}
