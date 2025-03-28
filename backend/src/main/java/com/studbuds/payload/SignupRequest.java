package com.studbuds.payload;

public class SignupRequest {
    private String name;
    private String email;
    private String password;
    private String major;
    private String year;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        if (email != null && email.endsWith("@cooper.edu")) {
            this.email = email;
        } else {
            throw new IllegalArgumentException("Email must be a @cooper.edu address");
        }
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        if (password != null && password.length() >= 9) {
            this.password = password;
        } else {
            throw new IllegalArgumentException("Password must be at least 9 characters long");
        }
    }
    public String getMajor() {
        return major;
    }
    public void setMajor(String major) {
        this.major = major;
    }
    public String getYear() {
        return year;
    }
    public void setYear(String year) {
        this.year = year;
    }
}