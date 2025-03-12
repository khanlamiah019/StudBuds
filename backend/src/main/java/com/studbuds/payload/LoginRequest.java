package com.studbuds.payload;

// LoginRequest with validation
public class LoginRequest {
   private String email;
   private String password;
   
   // Getters and setters
   public String getEmail() { return email; }
   public void setEmail(String email) {
       if (email.endsWith("@cooper.edu")) {
           if (SignupRequest.registeredEmails.contains(email)) {
               this.email = email;
           } else {
               throw new IllegalArgumentException("Email not found. Please sign up first.");
           }
       } else {
           throw new IllegalArgumentException("Email must be a @cooper.edu address");
       }
   }
   
   public String getPassword() { return password; }
   public void setPassword(String password) {
       if (password.length() >= 9) {
           this.password = password;
       } else {
           throw new IllegalArgumentException("Password must be at least 9 characters long");
       }
   }
}
