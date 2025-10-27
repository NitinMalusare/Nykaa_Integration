package com.eshop.auth.controller;

import com.eshop.auth.dto.NyCustomAuthResponse;
import com.eshop.auth.service.NyAuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/RestWS/api/sellerPanel/v3")
public class NyAuthController {

    private final NyAuthService authService;

    // Constructor-based dependency injection
    public NyAuthController(NyAuthService authService) {
        Assert.notNull(authService, "NyAuthService must not be null!");
        this.authService = authService;
    }

    @PostMapping("/authToken")
    public ResponseEntity<NyCustomAuthResponse> generateAuthToken(
            @RequestParam String username,
            @RequestParam String password) {
        NyCustomAuthResponse response = authService.generateCustomResponse(username, password);
        return ResponseEntity.ok(response);
    }
}