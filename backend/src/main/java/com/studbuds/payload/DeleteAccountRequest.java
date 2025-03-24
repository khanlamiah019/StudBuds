package com.studbuds.payload;

public class DeleteAccountRequest {
    private String email;
    private String firebaseToken;

    public DeleteAccountRequest() {}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if(email != null && email.endsWith("@cooper.edu")){
            this.email = email;
        } else {
            throw new IllegalArgumentException("Email must be a @cooper.edu address");
        }
    }

    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }
}