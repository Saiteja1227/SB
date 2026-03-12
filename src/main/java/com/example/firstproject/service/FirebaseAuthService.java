package com.example.firstproject.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.firstproject.entity.AuthUser;
import com.example.firstproject.repository.AuthUserRepository;
import com.example.firstproject.util.JwtUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

@Service
public class FirebaseAuthService {
    
    @Autowired
    private AuthUserRepository authUserRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * Verify Firebase ID token and authenticate user
     */
    public Map<String, Object> verifyTokenAndAuthenticateUser(String idToken) {
        try {
            // Verify Firebase ID token
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
            
            // Extract user details
            String firebaseUid = decodedToken.getUid();
            String phoneNumber = (String) decodedToken.getClaims().get("phone_number");
            String email = decodedToken.getEmail();
            String displayName = decodedToken.getName();
            
            // Check if user exists in database
            Optional<AuthUser> existingUser = authUserRepository.findByFirebaseUid(firebaseUid);
            
            AuthUser user;
            boolean isNewUser = false;
            
            if (existingUser.isPresent()) {
                // Update existing user
                user = existingUser.get();
                user.setLastLogin(LocalDateTime.now());
                
                // Update phone number if it changed
                if (phoneNumber != null && !phoneNumber.equals(user.getPhoneNumber())) {
                    user.setPhoneNumber(phoneNumber);
                }
                
                // Update email if available
                if (email != null && !email.equals(user.getEmail())) {
                    user.setEmail(email);
                }
                
                // Update display name if available
                if (displayName != null && !displayName.equals(user.getDisplayName())) {
                    user.setDisplayName(displayName);
                }
            } else {
                // Create new user
                user = new AuthUser();
                user.setFirebaseUid(firebaseUid);
                user.setPhoneNumber(phoneNumber);
                user.setEmail(email);
                user.setDisplayName(displayName);
                user.setIsActive(true);
                isNewUser = true;
            }
            
            // Save user to database
            user = authUserRepository.save(user);
            
            // Generate JWT token
            String jwtToken = jwtUtil.generateToken(
                    user.getFirebaseUid(),
                    user.getPhoneNumber(),
                    user.getId()
            );
            
            // Prepare response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", isNewUser ? "User registered successfully" : "User authenticated successfully");
            response.put("token", jwtToken);
            response.put("user", Map.of(
                    "id", user.getId(),
                    "firebaseUid", user.getFirebaseUid(),
                    "phoneNumber", user.getPhoneNumber() != null ? user.getPhoneNumber() : "",
                    "email", user.getEmail() != null ? user.getEmail() : "",
                    "displayName", user.getDisplayName() != null ? user.getDisplayName() : "",
                    "isNewUser", isNewUser
            ));
            
            return response;
            
        } catch (FirebaseAuthException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Invalid Firebase token: " + e.getMessage());
            return errorResponse;
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Authentication failed: " + e.getMessage());
            return errorResponse;
        }
    }
    
    /**
     * Get user by Firebase UID
     */
    public Optional<AuthUser> getUserByFirebaseUid(String firebaseUid) {
        return authUserRepository.findByFirebaseUid(firebaseUid);
    }
    
    /**
     * Get user by phone number
     */
    public Optional<AuthUser> getUserByPhoneNumber(String phoneNumber) {
        return authUserRepository.findByPhoneNumber(phoneNumber);
    }
    
    /**
     * Get all users
     */
    public java.util.List<AuthUser> getAllUsers() {
        return authUserRepository.findAll();
    }
    
    /**
     * Check if user exists
     */
    public boolean userExists(String firebaseUid) {
        return authUserRepository.existsByFirebaseUid(firebaseUid);
    }
}
