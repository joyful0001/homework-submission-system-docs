package com.classhomework.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {
    // 后续张欣彤开发JWT时填充
    private String secret = "class-homework-secret-key-2024";
    private long expiration = 86400000; // 24小时

    public String getSecret() {
        return secret;
    }

    public long getExpiration() {
        return expiration;
    }
}