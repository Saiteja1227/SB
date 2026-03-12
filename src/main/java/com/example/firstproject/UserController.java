package com.example.firstproject;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.firstproject.dto.LoginRequest;
import com.example.firstproject.service.RecaptchaService;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    UserRepository ur;

    @Autowired
    RecaptchaService recaptchaService;

    // REGISTER
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Users nu) 
    {

        Users existingUser = this.ur.findByUsername(nu.getUsername());

        if (existingUser != null) 
        {//409
            //return "invalid username";
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
        }

        this.ur.save(nu);
        return ResponseEntity
                .status(HttpStatus.CREATED)//201
                .body("Registration successful");
    }

    // LOGIN
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {

        // Verify reCAPTCHA token
        if (loginRequest.getRecaptchaToken() != null && !loginRequest.getRecaptchaToken().isEmpty()) {
            boolean isHuman = recaptchaService.verifyToken(loginRequest.getRecaptchaToken(), "LOGIN");
            if (!isHuman) {
                return ResponseEntity
                        .status(HttpStatus.FORBIDDEN)//403
                        .body("reCAPTCHA verification failed. Please try again.");
            }
        }

        // Try to find user by username first, then by email
        Users eu = null;
        
        if (loginRequest.getUsername() != null && !loginRequest.getUsername().isEmpty()) {
            eu = this.ur.findByUsername(loginRequest.getUsername());
        }
        
        // If not found by username, try email
        if (eu == null && loginRequest.getEmail() != null && !loginRequest.getEmail().isEmpty()) {
            eu = this.ur.findByEmail(loginRequest.getEmail());
        }

        if (eu != null && eu.getPassword().equals(loginRequest.getPassword())) {
            return ResponseEntity.ok("Login successful");//200
        }

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)//401
                .body("Invalid credentials");
    }

    // UPDATE PASSWORD
    @PostMapping("/update")
    public ResponseEntity<String> updatePassword(@RequestBody UpdatePasswordRequest u) {

        Users eu = this.ur.findByUsername(u.getUsername());

        if (eu == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)//404
                    .body("User not found");
        }

        if (!eu.getPassword().equals(u.getOldPassword())) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)//401
                    .body("Old password is incorrect");
        }

        eu.setPassword(u.getNewPassword());
        this.ur.save(eu);

        return ResponseEntity.ok("Password updated successfully");
    }

    // DELETE USER
    @PostMapping("/delete")
    public ResponseEntity<String> deleteUser(@RequestBody Users u) {

        Users eu = this.ur.findByUsername(u.getUsername());

        if (eu == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)//404
                    .body("User not found");
        }

        this.ur.delete(eu);
        return ResponseEntity.ok("User deleted successfully");//200
    }

    // VIEW SINGLE USER
    @GetMapping("/view")
    public ResponseEntity<Users> viewUser(@RequestParam String data) {

        Users u = this.ur.findByUsername(data);

        if (u == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)//404
                    .build();
        }

        return ResponseEntity.ok(u);
    }

    // VIEW ALL USERS
   //  @GetMapping("/viewAll")
   //  public ResponseEntity<List<Users>> viewAllUsers() {

   //      List<Users> users = this.ur.findAll();

   //      if (users.isEmpty()) {
   //          return ResponseEntity
   //                  .status(HttpStatus.NO_CONTENT)//204
   //                  .build();
   //      }

   //      return ResponseEntity.ok(users);//200
   //  }
   @GetMapping("/viewAll")
public ResponseEntity<List<Users>> getUsers(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size) {

    Pageable pageable = PageRequest.of(page, size);
    Page<Users> usersPage = this.ur.findAll(pageable);

    return ResponseEntity.ok(usersPage.getContent());
}


// @GetMapping("/viewAll")
// public ResponseEntity<Page<Users>> getUsers(
//         @RequestParam(defaultValue = "0") int page,
//         @RequestParam(defaultValue = "5") int size) {

//     Pageable pageable = PageRequest.of(page, size);
//     Page<Users> usersPage = this.ur.findAll(pageable);

//     return ResponseEntity.ok(usersPage);
// }

    // FORGOT PASSWORD - Generate reset token
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        Users user = this.ur.findByEmail(request.getEmail());
        
        if (user == null) {
            // Return success even if user not found (security best practice)
            return ResponseEntity.ok("If an account exists with this email, you will receive a password reset link.");
        }
        
        // Generate reset token (UUID)
        String resetToken = java.util.UUID.randomUUID().toString();
        
        // Set token expiry (1 hour from now)
        java.time.LocalDateTime expiry = java.time.LocalDateTime.now().plusHours(1);
        
        user.setResetToken(resetToken);
        user.setTokenExpiry(expiry);
        this.ur.save(user);
        
        // Log the reset link (in production, send via email)
        String resetLink = "http://localhost:3000/reset-password?token=" + resetToken;
        System.out.println("🔗 Password Reset Link for " + user.getEmail() + ": " + resetLink);
        System.out.println("⏰ Token expires at: " + expiry);
        
        // FOR DEVELOPMENT: Return token in response (remove in production)
        String response = String.format("{\"message\":\"Password reset token generated\",\"token\":\"%s\",\"resetLink\":\"%s\",\"expiresAt\":\"%s\"}", 
            resetToken, resetLink, expiry.toString());
        
        return ResponseEntity.ok(response);
    }
    
    // VERIFY PASSWORD RESET TOKEN
    @GetMapping("/verify-reset-token")
    public ResponseEntity<String> verifyResetToken(@RequestParam String token) {
        Users user = this.ur.findByResetToken(token);
        
        if (user == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Invalid reset token");
        }
        
        // Check if token has expired
        if (user.getTokenExpiry().isBefore(java.time.LocalDateTime.now())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Reset token has expired");
        }
        
        return ResponseEntity.ok("Token is valid");
    }
    
    // RESET PASSWORD
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        Users user = this.ur.findByResetToken(request.getToken());
        
        if (user == null) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Invalid reset token");
        }
        
        // Check if token has expired
        if (user.getTokenExpiry().isBefore(java.time.LocalDateTime.now())) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Reset token has expired");
        }
        
        // Update password and clear token
        user.setPassword(request.getNewPassword());
        user.setResetToken(null);
        user.setTokenExpiry(null);
        this.ur.save(user);
        
        return ResponseEntity.ok("Password reset successfully");
    }

}







// 🔍 What is happening here?
// return ResponseEntity
//         .status(HttpStatus.NO_CONTENT)
//         .build();

// ❓ Why .build() is needed?

// Because ResponseEntity uses the Builder pattern.

// When you write:

// ResponseEntity.status(HttpStatus.NO_CONTENT)


// 👉 This does NOT return a ResponseEntity yet
// 👉 It returns a ResponseEntity.BodyBuilder

// So Spring is basically saying:

// “Okay… you told me the status.
// Now what? Body? Headers? Or nothing?”

// ✅ What .build() does

// .build() means:

// ✅ “Create the final ResponseEntity with NO body.”

// So this line:

// .build();


// ➡️ converts the builder into a real ResponseEntity<?>.

// 📦 Why NO_CONTENT (204) specifically uses .build()
// HTTP 204 rule:

// 204 means:

// Request was successful

// Response body must be empty

// So this is correct & clean:

// return ResponseEntity
//         .status(HttpStatus.NO_CONTENT)
//         .build();


// ❌ This would be wrong / meaningless:

// return ResponseEntity
//         .status(HttpStatus.NO_CONTENT)
//         .body("No users");  // body NOT allowed for 204

// 🧠 Compare with other cases
// ✔ When you HAVE a body
// return ResponseEntity.ok(users);

// ✔ When you WANT a body with custom status
// return ResponseEntity
//         .status(HttpStatus.CONFLICT)
//         .body("Username already exists");

// ✔ When you want ONLY status, NO body
// return ResponseEntity
//         .status(HttpStatus.NOT_FOUND)
//         .build();

// 🧪 Think of it like this (real-life analogy)

// 🧱 Builder = ordering food
// 🍽 .build() = food served

// ResponseEntity.status(...)   // placing order
// .build();                    // food is delivered


// No .build() → order never completes ❌

// ⭐ Interview-ready one-liner (remember this)

// .build() is used to create a ResponseEntity without a body, especially for 204 NO_CONTENT or when only status is required.




















// // @Controller
// @RestController
// @CrossOrigin(origins = "http://localhost:3000")
// public class UserController 
// {
//    @Autowired
//    UserRepository ur;

//    @PostMapping("/register")
//    // String m(String uname,String email,String psw,Model x)
//    ResponseEntity<?> m(@RequestBody Users nu) throws InterruptedException
//    {
//      String nuname=nu.username;
//      Users eu=this.ur.findByUsername(nuname);
//      if(eu==null)
//      {
//         this.ur.save(nu);
//       //   return "registratione done";
//       return ResponseEntity.status(HttpStatus.CREATED).body("registration done");
//      }
//    //   return "invalid username";
//    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("username not found");



      






//    //   String uname= nu.getUsername();
//    //   Users eu=this.ur.findByUsername(uname);
//    //   if(eu==null)
//    //   {
//          //  Users nu=new Users();
//          // nu.setUsername(uname);
//          // nu.setEmail(email);
//          // nu.setPassword(psw);
     
//        //  this.ur.save(nu);
         
//       //    System.out.println(uname);
//       // System.out.println(email);
//       // System.out.println(psw);
//       // x.addAttribute("username",uname);
//       // x.addAttribute("usermail",email);
//       // x.addAttribute("userpsw",psw);


//       // return "admin";   
//       //return "registration done";
   
//      //return "username already exists";
        
//    }
   
//    @GetMapping("/log")
//    String m3()
//    {
//       return "login";
//    }

//    @PostMapping("/login")
//    String m4(@RequestBody Users u) throws InterruptedException
//    {
//      String uname= u.getUsername();
//      Users eu=this.ur.findByUsername(uname);
//      if(eu != null && eu.getPassword().equals(u.getPassword()))
//      {
//         //Thread.sleep(10000);
//         return "Login successful"; 
//         //return eu;                                                                 
//      }
//     return "invalid credentials";
//     //return eu;
//    }

//    @PostMapping("/update")
//    String m5(@RequestBody UpdatePasswordRequest u)
//    {
//      String uname= u.getUsername();
//      Users eu=this.ur.findByUsername(uname);
//      if(eu != null && eu.getPassword().equals(u.getOldPassword()))
//      {
//         eu.setPassword(u.getNewPassword());
//         this.ur.save(eu);
//         return "updated successfully";
//      }
//      return "invalid credentials";
//    }

//     @PostMapping("/delete")
//    String m6(@RequestBody Users u)
//    {
//      String uname= u.getUsername();
//      Users eu=this.ur.findByUsername(uname);
//      if(eu != null && eu.getUsername().equals(u.getUsername()))
//      {
//         this.ur.delete(eu);
//         return "deleted";
//      }
//      return "invalid credentials";
//    }

//    //@GetMapping("/vdetails") 
//    @GetMapping("/view")
//    Users m7(@RequestParam String data)
//    {
//          Users u=this.ur.findByUsername(data);
//          return u;
//    }


//    @GetMapping("/viewAll")
//    List<Users> m8()
//    {
//          List<Users> all=this.ur.findAll();
//          return all;
//    }
//    @GetMapping("/h")
//    String m1()
//    {
//       return "home";
//    }

//    @GetMapping("/a")
//    String m2()
//    {
//     return "admin"; 
//    }
// }
