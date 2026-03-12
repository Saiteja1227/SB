package com.example.firstproject;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    String username;

    @Column
    String email;

    @Column
    String password;

    // ✅ NEW COLUMN
    @Column
    String department;

    // Password Reset Token Fields
    @Column
    String resetToken;

    @Column
    LocalDateTime tokenExpiry;

    // GETTERS
    public Long getId() {
        return this.id;
    }

    public String getUsername() {
        return this.username;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }

    public String getDepartment() {
        return this.department;
    }

    public String getResetToken() {
        return this.resetToken;
    }

    public LocalDateTime getTokenExpiry() {
        return this.tokenExpiry;
    }

    // SETTERS (should be public)
    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String n) {
        this.username = n;
    }

    public void setEmail(String e) {
        this.email = e;
    }

    public void setPassword(String p) {
        this.password = p;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }

    public void setTokenExpiry(LocalDateTime tokenExpiry) {
        this.tokenExpiry = tokenExpiry;
    }
}
