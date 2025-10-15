package com.eshop.auth.util;

import java.util.UUID;

public final class TokenUtil {
    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
