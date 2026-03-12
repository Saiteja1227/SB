package com.example.firstproject;


public class UpdatePasswordRequest 
{
    private String username;
    private String oldPassword;
    private String newPassword;

    // Getter and Setter for username
    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Getter and Setter for oldPassword
    public String getOldPassword() {
        return this.oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    // Getter and Setter for newPassword
    public String getNewPassword() {
        return this.newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}



// Yes ✅ this is a DTO.

// Why UpdatePasswordRequest is a DTO

// DTO = Data Transfer Object

// Your class:

// public class UpdatePasswordRequest {
//     private String username;
//     private String oldPassword;
//     private String newPassword;
// }


// ✔ It is used only to transfer data
// ✔ It has no business logic
// ✔ It is not mapped to a database table
// ✔ It is used between React → Controller

// 👉 That is exactly what a DTO is.

// Entity vs DTO (very clear difference)
// ❌ Entity (Users)
// @Entity
// public class Users {
//     private Long id;
//     private String username;
//     private String password;
// }


// Represents DB table

// Persistent

// Used by JPA/Hibernate

// ✅ DTO (UpdatePasswordRequest)
// public class UpdatePasswordRequest {
//     private String username;
//     private String oldPassword;
//     private String newPassword;
// }


// Temporary

// Used for API request

// Safe for sensitive data

// Interview-ready answer (use this 👇)

// “Yes, it is a DTO.
// DTOs are used to safely transfer data between client and server without exposing the entity or database structure.”

// Why using DTO here is IMPORTANT

// Prevents exposing full Users entity

// Avoids accidental updates

// Cleaner API design

// Industry standard for password operation