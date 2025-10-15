package com.eshop.auth.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class TokenCacheService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String PREFIX = "connectionAccessToken_";

    public void storeToken(String username, String token) {
        redisTemplate.opsForValue().set(PREFIX + username, token, Duration.ofDays(365));
    }

    public String getToken(String username) {
        return redisTemplate.opsForValue().get(PREFIX + username);
    }

    public void removeToken(String username) {
        redisTemplate.delete(PREFIX + username);
    }

    public boolean containsToken(String username) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(PREFIX + username));
    }
}
