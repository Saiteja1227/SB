package com.example.firstproject.filter;

import com.example.firstproject.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        try {
            String authHeader = request.getHeader("Authorization");
            
            String token = null;
            String firebaseUid = null;
            
            // Extract JWT token from Authorization header
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
                firebaseUid = jwtUtil.extractFirebaseUid(token);
            }
            
            // Validate token and set authentication
            if (token != null && firebaseUid != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                if (jwtUtil.validateToken(token)) {
                    // Create authentication token
                    UsernamePasswordAuthenticationToken authToken = 
                            new UsernamePasswordAuthenticationToken(firebaseUid, null, new ArrayList<>());
                    
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // Set authentication in security context
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    
                    // Add user info to request attributes for easy access in controllers
                    request.setAttribute("firebaseUid", firebaseUid);
                    request.setAttribute("userId", jwtUtil.extractUserId(token));
                    request.setAttribute("phoneNumber", jwtUtil.extractPhoneNumber(token));
                }
            }
        } catch (Exception e) {
            // Log the error but don't block the request
            logger.error("JWT Authentication failed: " + e.getMessage());
        }
        
        filterChain.doFilter(request, response);
    }
}
