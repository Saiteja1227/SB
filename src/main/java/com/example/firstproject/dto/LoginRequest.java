package com.example.firstproject.dto;

public class LoginRequest {
    private String username;
    private String email;
    private String password;
    private String recaptchaToken;

    // Constructors
    public LoginRequest() {
    }

    public LoginRequest(String username, String email, String password, String recaptchaToken) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.recaptchaToken = recaptchaToken;
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRecaptchaToken() {
        return recaptchaToken;
    }

    // Setters
    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRecaptchaToken(String recaptchaToken) {
        this.recaptchaToken = recaptchaToken;
    }
}
