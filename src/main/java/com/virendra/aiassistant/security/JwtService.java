package com.virendra.aiassistant.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    @Value("${app.jwt.secret:}")
    private String secret;

    private SecretKey key;

    @PostConstruct
    public void init() {
        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException(
                "JWT_SECRET environment variable is not set. " +
                "Set it before running the application: " +
                "Windows: set JWT_SECRET=your-secret-key | " +
                "Linux/Mac: export JWT_SECRET=your-secret-key"
            );
        }
        if (secret.length() < 32) {
            throw new IllegalArgumentException(
                "JWT_SECRET must be at least 32 characters long for HS256 encryption"
            );
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(UserDetails userDetails) {

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {

        return extractClaims(token).getSubject();
    }

    public boolean isValid(String token, UserDetails userDetails) {

        return extractUsername(token)
                .equals(userDetails.getUsername())
                && !isExpired(token);
    }

    private boolean isExpired(String token) {

        return extractClaims(token)
                .getExpiration()
                .before(new Date());
    }

    private Claims extractClaims(String token) {

        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
