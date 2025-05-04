package com.studbuds.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
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

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody SignupRequest req) {
        String email = req.getEmail().trim().toLowerCase();
        String name = req.getName();
        String firebaseToken = req.getFirebaseToken();

        // Basic validation
        if (!email.endsWith("@cooper.edu"))
            return ResponseEntity.badRequest().body("Email must be a @cooper.edu address");
        if (req.getPassword() == null || req.getPassword().length() < 9)
            return ResponseEntity.badRequest().body("Password must be at least 9 characters long");

        // Verify Firebase token and extract UID
        FirebaseToken decoded;
        try {
            decoded = firebaseAuth.verifyIdToken(firebaseToken);
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Firebase token: " + e.getMessage());
        }

        String uid = decoded.getUid();

        // Check if user already exists locally
        if (userRepository.findByFirebaseUid(uid).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already in use. Please log in instead.");
        }

        // Create local user
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setFirebaseUid(uid);
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

        Map<String, Object> resp = new HashMap<>();
        resp.put("message", "User registered successfully.");
        resp.put("userId", user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
        @RequestBody(required = false) LoginRequest body,
        @RequestHeader(value = "Authorization", required = false) String header
    ) {
        String idToken = null;
        if (header != null && header.startsWith("Bearer ")) {
            idToken = header.substring(7);
        } else if (body != null && body.getFirebaseToken() != null) {
            idToken = body.getFirebaseToken();
        }

        if (idToken == null) {
            return ResponseEntity.badRequest().body("Missing Firebase token");
        }

        try {
            FirebaseToken decoded = firebaseAuth.verifyIdToken(idToken);
            String uid = decoded.getUid();

            Optional<User> userOpt = userRepository.findByFirebaseUid(uid);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No local account found. Please sign up first.");
            }

            Map<String, Object> resp = new HashMap<>();
            resp.put("message", "Login successful.");
            resp.put("userId", userOpt.get().getId());
            return ResponseEntity.ok(resp);

        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Firebase token.");
        }
    }

    @PostMapping("/delete")
public ResponseEntity<?> deleteAccount(
    @RequestBody(required = false) DeleteAccountRequest body,
    @RequestHeader(value = "Authorization", required = false) String header
) {
    String idToken = null;
    if (header != null && header.startsWith("Bearer ")) {
        idToken = header.substring(7);
    } else if (body != null && body.getFirebaseToken() != null) {
        idToken = body.getFirebaseToken();
    }

    if (idToken == null) {
        return ResponseEntity.badRequest().body(Map.of("message", "Missing Firebase token"));
    }

    try {
        FirebaseToken decoded = firebaseAuth.verifyIdToken(idToken);
        String uid = decoded.getUid();

        Optional<User> userOpt = userRepository.findByFirebaseUid(uid);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "User not found in local DB."));
        }

        User user = userOpt.get();

        try {
            firebaseAuth.deleteUser(uid);
            System.out.println("[DEBUG] Firebase user deleted: " + uid);
        } catch (FirebaseAuthException e) {
            if (!"USER_NOT_FOUND".equals(e.getAuthErrorCode().name())) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("message", "Firebase error: " + e.getMessage()));
            } else {
                System.out.println("[DEBUG] Firebase user already gone: " + uid);
            }
        }

        // ⚠️ Confirm both local deletions happen
        preferenceRepository.findByUser(user).ifPresent(pref -> {
            preferenceRepository.delete(pref);
            System.out.println("[DEBUG] Deleted preference for user " + uid);
        });

        userRepository.delete(user);
        System.out.println("[DEBUG] Deleted user from local DB: " + uid);

        return ResponseEntity.ok(Map.of("message", "Account deleted successfully."));

    } catch (FirebaseAuthException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Firebase error: " + e.getMessage()));
    }
}

