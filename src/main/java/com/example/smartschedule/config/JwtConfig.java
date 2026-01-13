package com.example.smartschedule.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
    private String secret = "5367566B59703373367639792F423F4528482B4D6251655468576D5A71347437";
    private long expiration = 36000000; // 10 hours in milliseconds
}