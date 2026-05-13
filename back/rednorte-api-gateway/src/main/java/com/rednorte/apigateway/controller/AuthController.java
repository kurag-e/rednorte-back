package com.rednorte.apigateway.controller;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Value("${app.jwt.secret}")
    private String secret;

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody LoginRequest request) {

        if (!"admin".equals(request.username()) || !"admin123".equals(request.password())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        String token = Jwts.builder()
                .subject(request.username())
                .claim("role", "ADMIN")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();

        return Map.of("token", token);
    }

    public record LoginRequest(String username, String password) {}
}