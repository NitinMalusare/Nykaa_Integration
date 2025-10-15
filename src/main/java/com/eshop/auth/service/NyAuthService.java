package com.eshop.auth.service;

import com.eshop.auth.cache.TokenCacheService;
import com.eshop.auth.dto.NyAuthRequest;
import com.eshop.auth.dto.NyCustomAuthResponse;
import com.eshop.auth.entity.NyEshopUser;
import com.eshop.auth.repository.NyEshopUserRepository;
import com.eshop.auth.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class NyAuthService {

    @Autowired
    private NyEshopUserRepository userRepository;

    @Autowired
    private TokenCacheService tokenCacheService;

    public NyCustomAuthResponse generateCustomResponse(String username, String password) {
        Optional<NyEshopUser> userOptional = userRepository.findByUsername(username);

        String accessToken;
        String status;
        LocalDateTime now = LocalDateTime.now();

        if (userOptional.isPresent()) {
            NyEshopUser user = userOptional.get();
            if (user.getPassword().equals(password)) {
                // Check if existing token is still valid
                if (user.getTokenExpiry() != null && user.getTokenExpiry().isAfter(now)) {
                    accessToken = user.getAccessToken();
                } else {
                    // Generate new token if expired
                    accessToken = TokenUtil.generateToken();
                    String refreshToken = TokenUtil.generateToken();
                    LocalDateTime expiry = now.plusYears(1);

                    user.setAccessToken(accessToken);
                    user.setRefreshToken(refreshToken);
                    user.setTokenExpiry(expiry);
                    userRepository.save(user);
                }

                // Store/Update in Redis
                tokenCacheService.storeToken(username, accessToken);

                status = "SUCCESS";
            } else {
                return new NyCustomAuthResponse("FAILURE", "", username);
            }
        } else {
            // Register new user
            accessToken = TokenUtil.generateToken();
            String refreshToken = TokenUtil.generateToken();
            LocalDateTime expiry = now.plusYears(1);

            NyEshopUser newUser = new NyEshopUser();
            newUser.setUsername(username);
            newUser.setPassword(password);
            newUser.setAccessToken(accessToken);
            newUser.setRefreshToken(refreshToken);
            newUser.setTokenExpiry(expiry);
            userRepository.save(newUser);

            // Store in Redis
            tokenCacheService.storeToken(username, accessToken);

            status = "SUCCESS";
        }

        return new NyCustomAuthResponse(status, accessToken, username);
    }

    public Boolean validateAuthToken(String token) {
        Optional<NyEshopUser> userOptional = userRepository.findByAccessToken(token);
        if (userOptional.isPresent()) {
            NyEshopUser user = userOptional.get();
            return user.getTokenExpiry() != null && user.getTokenExpiry().isAfter(LocalDateTime.now());
        }
        return false;
    }
}
