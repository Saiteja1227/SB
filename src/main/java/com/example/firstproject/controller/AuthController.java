package com.example.firstproject.controller;

import com.example.firstproject.service.FirebaseAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private FirebaseAuthService firebaseAuthService;
    
    /**
     * Authenticate user with Firebase ID token
     * 
     * Request body: { "idToken": "firebase_id_token_here" }
     * 
     * Response: {
     *   "success": true,
     *   "message": "User authenticated successfully",
     *   "token": "jwt_token_here",
     *   "user": {
     *     "id": 1,
     *     "firebaseUid": "...",
     *     "phoneNumber": "+1234567890",
     *     "email": "user@example.com",
     *     "displayName": "John Doe",
     *     "isNewUser": false
     *   }
     * }
     */
    @PostMapping("/verify-token")
    public ResponseEntity<Map<String, Object>> verifyFirebaseToken(@RequestBody Map<String, String> request) {
        try {
            String idToken = request.get("idToken");
            
            if (idToken == null || idToken.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of(
                                "success", false,
                                "message", "Firebase ID token is required"
                        ));
            }
            
            Map<String, Object> response = firebaseAuthService.verifyTokenAndAuthenticateUser(idToken);
            
            if ((Boolean) response.get("success")) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Authentication failed: " + e.getMessage()
                    ));
        }
    }
    
    /**
     * Alternative endpoint name for Firebase authentication
     */
    @PostMapping("/firebase-login")
    public ResponseEntity<Map<String, Object>> firebaseLogin(@RequestBody Map<String, String> request) {
        return verifyFirebaseToken(request);
    }
    
    /**
     * Get authenticated user profile
     * Requires JWT token in Authorization header
     */
    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile(@RequestAttribute("firebaseUid") String firebaseUid) {
        try {
            var user = firebaseAuthService.getUserByFirebaseUid(firebaseUid);
            
            if (user.isPresent()) {
                var authUser = user.get();
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "user", Map.of(
                                "id", authUser.getId(),
                                "firebaseUid", authUser.getFirebaseUid(),
                                "phoneNumber", authUser.getPhoneNumber() != null ? authUser.getPhoneNumber() : "",
                                "email", authUser.getEmail() != null ? authUser.getEmail() : "",
                                "displayName", authUser.getDisplayName() != null ? authUser.getDisplayName() : "",
                                "createdAt", authUser.getCreatedAt().toString(),
                                "lastLogin", authUser.getLastLogin().toString()
                        )
                ));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of(
                                "success", false,
                                "message", "User not found"
                        ));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "message", "Failed to fetch profile: " + e.getMessage()
                    ));
        }
    }
    
    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "OK",
                "message", "Authentication service is running"
        ));
    }
}
