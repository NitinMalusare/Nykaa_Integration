package com.eshop.auth.config;

import com.eshop.auth.cache.TokenCacheService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ApiKeyInterceptor implements ClientHttpRequestInterceptor {

    private final TokenCacheService tokenCacheService;

    public ApiKeyInterceptor(TokenCacheService tokenCacheService) {
        this.tokenCacheService = tokenCacheService;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        String apiKey = tokenCacheService.getToken("nykaa:apiKey");
        if (apiKey != null && !apiKey.isEmpty()) {
            request.getHeaders().add("apiKey", apiKey);
        }
        request.getHeaders().add(HttpHeaders.ACCEPT, "application/json");
        return execution.execute(request, body);
    }
}


