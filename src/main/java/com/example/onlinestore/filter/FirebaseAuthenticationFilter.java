package com.example.onlinestore.filter;

import com.example.onlinestore.security.FirebaseAuthenticationToken;
import com.example.onlinestore.service.FirebaseAuthService;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class FirebaseAuthenticationFilter extends OncePerRequestFilter {


    private final FirebaseAuthService firebaseAuthService;

    public FirebaseAuthenticationFilter(FirebaseAuthService firebaseAuthService) {
        this.firebaseAuthService = firebaseAuthService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Extract the Authorization header
        String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String idToken = authorizationHeader.substring(7);  // Remove "Bearer " prefix

            try {
                // Verify the Firebase ID token using FirebaseAuthService
                FirebaseToken decodedToken = firebaseAuthService.verifyIdToken(idToken);

                if (decodedToken != null) {
                    // If token is valid, proceed with the request
                    SecurityContextHolder.getContext().setAuthentication(
                            new FirebaseAuthenticationToken(decodedToken, null)
                    );
                }
            } catch (FirebaseAuthException e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Unauthorized: Invalid or expired token");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}