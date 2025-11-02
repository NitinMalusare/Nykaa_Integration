package com.eshop.auth.controller;

import com.eshop.auth.dto.NyCustomAuthResponse;
import com.eshop.auth.service.NyAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/RestWS/api/sellerPanel/v3")
public class NyAuthController {

    @Autowired
    private NyAuthService authService;

    @PostMapping("/authToken")
        public ResponseEntity<NyCustomAuthResponse> generateAuthToken(
                @RequestParam String username,
                @RequestParam String password) {
        NyCustomAuthResponse response = authService.generateCustomResponse(username, password);
        return ResponseEntity.ok(response);
    }
}
