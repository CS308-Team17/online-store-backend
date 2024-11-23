package com.example.onlinestore.service;

import com.example.onlinestore.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthenticationService(PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public String loginUser(String userId, String password) {
        String storedPassword = fetchUserPassword(userId);

        if (storedPassword != null && passwordEncoder.matches(password, storedPassword)) {
            return jwtUtil.generateToken(userId); // Use instance method
        } else {
            throw new RuntimeException("Invalid credentials");
        }
    }

    private String fetchUserPassword(String userId) {
        if ("testUser".equals(userId)) {
            return passwordEncoder.encode("password123");
        }
        return null;
    }
}
