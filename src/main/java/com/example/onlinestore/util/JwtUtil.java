package com.example.onlinestore.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private final String SECRET_KEY;

    public JwtUtil() {
        // Load the secret key from an environment variable
        this.SECRET_KEY = System.getenv("JWT_SECRET_KEY");
        if (this.SECRET_KEY == null || this.SECRET_KEY.isEmpty()) {
            throw new IllegalArgumentException("JWT_SECRET_KEY environment variable is not set");
        }
    }

    // Generate a JWT token
    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    // Validate a JWT token
    public String validateToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // Check if the token is valid
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = validateToken(token); // Extract username from token
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    // Check if the token is expired
    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());
    }
}
