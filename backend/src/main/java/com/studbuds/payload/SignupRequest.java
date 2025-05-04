// src/main/java/com/studbuds/payload/SignupRequest.java
package com.studbuds.payload;

public class SignupRequest {
    private String name;
    private String email;
    private String password;
    private String major;
    private String year;
    private String firebaseToken;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }

    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }

    public String getFirebaseToken() { return firebaseToken; }
    public void setFirebaseToken(String firebaseToken) { this.firebaseToken = firebaseToken; }
}

