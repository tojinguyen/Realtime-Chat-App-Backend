package com.learning.userservice.userservice.security;

import com.learning.userservice.userservice.model.UserAccount;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Component
public class JwtUtils {
    private static final Logger log = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenValidityInMilliseconds;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenValidityInMilliseconds;

    // Generate access token
    public String generateAccessToken(UserAccount user) {
        return generateToken(user.getEmail(), accessTokenValidityInMilliseconds);
    }

    // Generate refresh token
    public String generateRefreshToken(UserAccount user) {
        return generateToken(user.getEmail(), refreshTokenValidityInMilliseconds);
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            var claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String username = claims.getSubject();
            return (username.equals(userDetails.getUsername()) && !claims.getExpiration().before(new Date()));
        } catch (JwtException ex) {
            log.error("Invalid or expired token: {}", ex.getMessage());
            return false;
        }
    }

    // Validate refresh token
    public boolean validateRefreshToken(String refreshToken) {
        try {
            if (refreshToken == null || refreshToken.isBlank()) {
                return false;
            }
            if (refreshToken.startsWith("Bearer ")) {
                refreshToken = refreshToken.substring(7);
            }
            // Use JJWT library to parse and validate the refresh token
            Jwts.parserBuilder()
                    .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                    .build()
                    .parseClaimsJws(refreshToken); // If the token is invalid or expired, an exception is thrown
            return true; // Return true if validation is successful
        } catch (SecurityException | MalformedJwtException | ExpiredJwtException |
                 UnsupportedJwtException | IllegalArgumentException ex) {
            log.error("Invalid refresh token: {}", ex.getMessage());
            return false; // Return false if validation fails
        }
    }

    // Get expiration date from token
    public Instant getExpirationDateFromToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        var claims = Jwts.parserBuilder()
                .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getExpiration().toInstant();
    }

    // Get the user ID (or subject) from the refresh token
    public String extractUsername(String refreshToken) {
        try {
            // Extract the claims from the refresh token
            var claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8))
                    .build()
                    .parseClaimsJws(refreshToken)
                    .getBody();

            // Return the subject (user email or user ID from claims)
            return claims.getSubject();
        } catch (JwtException ex) {
            log.error("Failed to extract user ID from token: {}", ex.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired refresh token");
        }
    }

    // Generate token method used by both access and refresh token generation
    private String generateToken(String userName, long expirationMs) {
        var now = Instant.now();
        var expirationTime = now.plusMillis(expirationMs);

        Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .setSubject(userName)
                .setIssuer("OnlineMarketplace")
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expirationTime))
                .claim("role", "USER")
                .signWith(key)
                .compact();
    }
}
