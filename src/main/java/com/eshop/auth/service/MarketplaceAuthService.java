package com.eshop.auth.service;

import com.eshop.auth.cache.TokenCacheService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class MarketplaceAuthService {

    private final RestTemplate restTemplate;
    private final TokenCacheService tokenCacheService;

    @Value("${nykaa.baseUrl:https://sandbox.nykaa.com}")
    private String baseUrl;

    public MarketplaceAuthService(RestTemplate restTemplate, TokenCacheService tokenCacheService) {
        this.restTemplate = restTemplate;
        this.tokenCacheService = tokenCacheService;
    }

    public String fetchAndCacheApiKey(String username, String password) {
        // Assuming Seller Panel Guide expects username/password query params
        String url = baseUrl + "/RestWS/api/sellerPanel/v3/authToken?username=" + username + "&password=" + password;
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        String apiKey = response.getBody();
        tokenCacheService.storeToken("nykaa:apiKey", apiKey);
        return apiKey;
    }
}


