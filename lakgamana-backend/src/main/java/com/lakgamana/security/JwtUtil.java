package com.lakgamana.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
 
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private int jwtExpirationMs;

    @Value("${jwt.refresh-expiration}")
    private int jwtRefreshExpirationMs;

    private SecretKey getSigningKey() {
        // Ensure the key is long enough for HS512 (minimum 512 bits = 64 bytes)
        if (jwtSecret.length() < 64) {
            throw new IllegalArgumentException("JWT secret must be at least 64 characters long for HS512 algorithm. Current length: " + jwtSecret.length());
        }
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateJwtToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        return generateTokenFromUsername(userPrincipal.getUsername());
    }

    public String generateTokenFromUsername(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtRefreshExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(authToken);
            return true;
        } catch (SecurityException e) {
            org.slf4j.LoggerFactory.getLogger(JwtUtil.class).error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            org.slf4j.LoggerFactory.getLogger(JwtUtil.class).error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            org.slf4j.LoggerFactory.getLogger(JwtUtil.class).error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            org.slf4j.LoggerFactory.getLogger(JwtUtil.class).error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            org.slf4j.LoggerFactory.getLogger(JwtUtil.class).error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    public Date getExpirationDateFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }

    public boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public long getExpirationTime() {
        return jwtExpirationMs;
    }
}
