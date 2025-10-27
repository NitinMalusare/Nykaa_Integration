package com.eshop.auth.cache;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.Duration;
import java.util.Objects;

/**
 * Service for managing authentication tokens in Redis cache.
 * Provides thread-safe operations for storing, retrieving, and removing user tokens.
 */
@Service
public class TokenCacheService {

    private static final String KEY_PREFIX = "auth:token:";
    private static final Duration TOKEN_TTL = Duration.ofHours(24); // Reduced from 365 days

    private final StringRedisTemplate redisTemplate;

    /**
     * Constructor injection for better testability and immutability.
     *
     * @param redisTemplate Redis template for string operations
     */
    public TokenCacheService(StringRedisTemplate redisTemplate) {
        Assert.notNull(redisTemplate, "RedisTemplate must not be null");
        this.redisTemplate = redisTemplate;
    }

    /**
     * Stores an authentication token for a user with TTL.
     *
     * @param username the username (must not be null or empty)
     * @param token the authentication token (must not be null or empty)
     * @throws IllegalArgumentException if username or token is invalid
     */
    public void storeToken(String username, String token) {
        validateUsername(username);
        validateToken(token);

        redisTemplate.opsForValue().set(
                buildKey(username),
                token,
                TOKEN_TTL
        );
    }

    /**
     * Retrieves the authentication token for a user.
     *
     * @param username the username (must not be null or empty)
     * @return the token if present, null otherwise
     * @throws IllegalArgumentException if username is invalid
     */
    public String getToken(String username) {
        validateUsername(username);
        return redisTemplate.opsForValue().get(buildKey(username));
    }

    public void removeToken(String username) {
        validateUsername(username);
        redisTemplate.delete(buildKey(username));
    }

     // Checks if a token exists for the user.
     // Null-safe implementation that handles potential null returns from Redis.

     // @param username the username (must not be null or empty)
     // @return true if token exists, false otherwise

    public boolean containsToken(String username) {
        validateUsername(username);

        // Null-safe check that satisfies the linter
        Boolean exists = redisTemplate.hasKey(buildKey(username));
        return exists != null && exists;
    }

    /**
     * Updates the TTL for an existing token (sliding expiration).
     *
     * @param username the username (must not be null or empty)
     * @return true if token exists and TTL was updated, false otherwise
     * @throws IllegalArgumentException if username is invalid
     */
    public boolean refreshTokenTTL(String username) {
        validateUsername(username);

        Boolean success = redisTemplate.expire(buildKey(username), TOKEN_TTL);
        return success != null && success;
    }

     // Builds the Redis key for a username.
     // @param username the username
     // @return the Redis key

    private String buildKey(String username) {
        return KEY_PREFIX + username;
    }


     // Validates that username is not null or empty.

     // @param username the username to validate
     // @throws IllegalArgumentException if username is invalid

    private void validateUsername(String username) {
        Assert.hasText(username, "Username must not be null or empty");

        // Additional validation: prevent injection attacks
        if (username.contains(":") || username.contains("*")) {
            throw new IllegalArgumentException("Username contains invalid characters");
        }
    }

    //  Validates that token is not null or empty.
    private void validateToken(String token) {
        Assert.hasText(token, "Token must not be null or empty");
    }
}