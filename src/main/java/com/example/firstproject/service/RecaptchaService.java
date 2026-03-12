package com.example.firstproject.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

@Service
public class RecaptchaService {

    @Value("${recaptcha.secret.key:}")
    private String secretKey;

    @Value("${recaptcha.project.id:}")
    private String projectId;

    @Value("${recaptcha.site.key:}")
    private String siteKey;

    @Value("${recaptcha.enabled:false}")
    private boolean recaptchaEnabled;

    @Value("${recaptcha.score.threshold:0.5}")
    private double scoreThreshold;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Verify reCAPTCHA Enterprise token
     * 
     * @param token The reCAPTCHA token from the client
     * @param expectedAction The expected action (e.g., "LOGIN")
     * @return true if verification passes, false otherwise
     */
    public boolean verifyToken(String token, String expectedAction) {
        // Skip verification if reCAPTCHA is disabled (for testing)
        if (!recaptchaEnabled) {
            System.out.println("⚠️  reCAPTCHA verification is DISABLED");
            return true;
        }

        if (token == null || token.isEmpty()) {
            System.out.println("❌ reCAPTCHA token is missing");
            return false;
        }

        if (secretKey == null || secretKey.isEmpty()) {
            System.out.println("❌ reCAPTCHA secret key is not configured");
            return false;
        }

        try {
            // For reCAPTCHA Enterprise
            String url = String.format(
                "https://recaptchaenterprise.googleapis.com/v1/projects/%s/assessments?key=%s",
                projectId, secretKey
            );

            // Build request body
            Map<String, Object> requestBody = new HashMap<>();
            Map<String, String> event = new HashMap<>();
            event.put("token", token);
            event.put("siteKey", siteKey);
            event.put("expectedAction", expectedAction);
            requestBody.put("event", event);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            // Make API call
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode jsonResponse = objectMapper.readTree(response.getBody());
                
                // Check if token is valid
                boolean isValid = jsonResponse.path("tokenProperties").path("valid").asBoolean(false);
                String action = jsonResponse.path("tokenProperties").path("action").asText("");
                double score = jsonResponse.path("riskAnalysis").path("score").asDouble(0.0);

                System.out.println("🔐 reCAPTCHA Verification:");
                System.out.println("   Valid: " + isValid);
                System.out.println("   Action: " + action);
                System.out.println("   Score: " + score);
                System.out.println("   Expected Action: " + expectedAction);

                // Verify action matches and score is above threshold
                boolean actionMatches = expectedAction.equalsIgnoreCase(action);
                boolean scoreAcceptable = score >= scoreThreshold;

                if (isValid && actionMatches && scoreAcceptable) {
                    System.out.println("✅ reCAPTCHA verification passed");
                    return true;
                } else {
                    System.out.println("❌ reCAPTCHA verification failed");
                    if (!actionMatches) {
                        System.out.println("   Reason: Action mismatch");
                    }
                    if (!scoreAcceptable) {
                        System.out.println("   Reason: Score too low (" + score + " < " + scoreThreshold + ")");
                    }
                    return false;
                }
            }

            System.out.println("❌ reCAPTCHA API call failed");
            return false;

        } catch (Exception e) {
            System.err.println("❌ Error verifying reCAPTCHA: " + e.getMessage());
            e.printStackTrace();
            // In production, you might want to fail closed (return false)
            // For development, we'll fail open to avoid blocking
            return false;
        }
    }

    /**
     * Alternative method for reCAPTCHA v3 (non-Enterprise)
     */
    public boolean verifyTokenV3(String token) {
        if (!recaptchaEnabled || token == null || token.isEmpty()) {
            return !recaptchaEnabled; // Pass if disabled, fail if enabled but no token
        }

        try {
            String url = "https://www.google.com/recaptcha/api/siteverify";
            
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("secret", secretKey);
            requestBody.put("response", token);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            String params = "secret=" + secretKey + "&response=" + token;
            
            ResponseEntity<String> response = restTemplate.postForEntity(
                url + "?" + params, 
                null, 
                String.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                JsonNode jsonResponse = objectMapper.readTree(response.getBody());
                boolean success = jsonResponse.path("success").asBoolean(false);
                double score = jsonResponse.path("score").asDouble(0.0);

                System.out.println("🔐 reCAPTCHA v3 Verification:");
                System.out.println("   Success: " + success);
                System.out.println("   Score: " + score);

                return success && score >= scoreThreshold;
            }

            return false;

        } catch (Exception e) {
            System.err.println("❌ Error verifying reCAPTCHA v3: " + e.getMessage());
            return false;
        }
    }
}
