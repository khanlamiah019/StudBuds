// src/main/java/com/studbuds/controller/AuthController.java
package com.studbuds.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.auth.UserRecord.CreateRequest;
import com.studbuds.model.Preference;
import com.studbuds.model.User;
import com.studbuds.payload.DeleteAccountRequest;
import com.studbuds.payload.LoginRequest;
import com.studbuds.payload.SignupRequest;
import com.studbuds.repository.PreferenceRepository;
import com.studbuds.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private UserRepository userRepository;
    @Autowired private PreferenceRepository preferenceRepository;
    @Autowired private FirebaseAuth firebaseAuth;

    // ─── SIGNUP ───────────────────────────────────────────────────────────────

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody SignupRequest req) {
        String email    = req.getEmail().trim().toLowerCase();
        String password = req.getPassword();
        String name     = req.getName();

        // 1) Basic validation
        if (!email.endsWith("@cooper.edu"))
            return ResponseEntity.badRequest().body("Email must be a @cooper.edu address");
        if (password == null || password.length() < 9)
            return ResponseEntity.badRequest().body("Password must be at least 9 characters long");

        // 2) Local-DB duplicate check (case-insensitive)
        if (userRepository.findByEmailIgnoreCase(email).isPresent()) {
            return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body("Email already in use. Please log in instead.");
        }

        // 3) Firebase-side check
        try {
            firebaseAuth.getUserByEmail(email);
            return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body("Email already in use. Please log in instead.");
        } catch (FirebaseAuthException e) {
            // expected if USER_NOT_FOUND, otherwise bail
            if (e.getAuthErrorCode() != com.google.firebase.auth.AuthErrorCode.USER_NOT_FOUND) {
                return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Firebase error: " + e.getMessage());
            }
        }

        // 4) Create user in Firebase
        UserRecord userRecord;
        try {
            CreateRequest createReq = new CreateRequest()
                .setEmail(email)
                .setPassword(password)
                .setDisplayName(name);
            userRecord = firebaseAuth.createUser(createReq);
        } catch (FirebaseAuthException e) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Firebase error: " + e.getMessage());
        }

        // 5) Persist locally
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setFirebaseUid(userRecord.getUid());
        user.setCreatedAt(LocalDateTime.now());
        user.setMajor(req.getMajor());
        user.setYear(req.getYear());
        userRepository.save(user);

        Preference pref = new Preference();
        pref.setUser(user);
        pref.setAvailableDays("");
        pref.setSubjectsToLearn("");
        pref.setSubjectsToTeach("");
        preferenceRepository.save(pref);

        user.setPreference(pref);
        userRepository.save(user);

        Map<String,Object> resp = new HashMap<>();
        resp.put("message", "User registered successfully.");
        resp.put("userId", user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    // ─── LOGIN ────────────────────────────────────────────────────────────────

    @PostMapping("/login")
public ResponseEntity<?> login(
    @RequestBody(required=false) LoginRequest body,
    @RequestHeader(value="Authorization", required=false) String header
) {
    String idToken = null;
    if (header != null && header.startsWith("Bearer ")) {
        idToken = header.substring(7);
    } else if (body != null && body.getFirebaseToken() != null) {
        idToken = body.getFirebaseToken();
    }
    if (idToken == null) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body("Missing Firebase token");
    }

    try {
        FirebaseToken decoded = firebaseAuth.verifyIdToken(idToken);
        String uid = decoded.getUid();

        Optional<User> userOpt = userRepository.findByFirebaseUid(uid);
        User user;
        if (userOpt.isPresent()) {
            user = userOpt.get();
        } else {
            // If Firebase user exists but local DB doesn't, create local user record
            UserRecord record = firebaseAuth.getUser(uid);
            user = new User();
            user.setName(record.getDisplayName());
            user.setEmail(record.getEmail());
            user.setFirebaseUid(uid);
            user.setCreatedAt(LocalDateTime.now());
            user.setMajor("");
            user.setYear("");
            user = userRepository.save(user);

            // Create empty preferences too
            Preference pref = new Preference();
            pref.setUser(user);
            pref.setAvailableDays("");
            pref.setSubjectsToLearn("");
            pref.setSubjectsToTeach("");
            preferenceRepository.save(pref);

            user.setPreference(pref);
            userRepository.save(user);
        }

        Map<String,Object> resp = new HashMap<>();
        resp.put("message", "Login successful.");
        resp.put("userId", user.getId());
        return ResponseEntity.ok(resp);

    } catch (FirebaseAuthException e) {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body("Invalid Firebase token.");
    }
}

    // ─── DELETE ACCOUNT ──────────────────────────────────────────────────────

    @PostMapping("/delete")
public ResponseEntity<?> deleteAccount(
    @RequestBody(required=false) DeleteAccountRequest body,
    @RequestHeader(value="Authorization", required=false) String header
) {
    // 1) Extract the token
    String idToken = null;
    if (header != null && header.startsWith("Bearer ")) {
        idToken = header.substring(7);
    } else if (body != null && body.getFirebaseToken() != null) {
        idToken = body.getFirebaseToken();
    }
    if (idToken == null) {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(Map.of("message","Missing Firebase token"));
    }

    try {
        // 2) Verify token & get uid
        FirebaseToken decoded = firebaseAuth.verifyIdToken(idToken);
        String uid = decoded.getUid();

        // 3) Find local user by firebaseUid
        Optional<User> userOpt = userRepository.findByFirebaseUid(uid);
        if (userOpt.isEmpty()) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("message","User not found."));
        }
        User user = userOpt.get();

        // 4) Attempt to delete from Firebase, skip if already deleted
        try {
            firebaseAuth.deleteUser(uid);
        } catch (FirebaseAuthException e) {
            if (e.getAuthErrorCode() == com.google.firebase.auth.AuthErrorCode.USER_NOT_FOUND) {
                System.out.println("Firebase user already deleted.");
            } else {
                return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Firebase error: " + e.getMessage()));
            }
        }

        // 5) Delete local preference & user
        preferenceRepository.findByUser(user).ifPresent(preferenceRepository::delete);
        userRepository.delete(user);

        return ResponseEntity.ok(Map.of("message","Account deleted successfully."));
    } catch (FirebaseAuthException e) {
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of("message","Firebase error: " + e.getMessage()));
    }
}
}
