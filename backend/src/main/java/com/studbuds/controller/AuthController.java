package com.studbuds.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
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

    if (!email.endsWith("@cooper.edu"))
        return ResponseEntity.badRequest().body("Email must be a @cooper.edu address");
    if (req.getPassword() == null || req.getPassword().length() < 9)
        return ResponseEntity.badRequest().body("Password must be at least 9 characters long");

    FirebaseToken decoded;
    try {
        decoded = firebaseAuth.verifyIdToken(firebaseToken);
    } catch (FirebaseAuthException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Firebase token: " + e.getMessage());
    }

    String uid = decoded.getUid();

    // 🔒 Reject if UID is already registered
    if (userRepository.findByFirebaseUid(uid).isPresent()) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body("This Firebase account is already registered. Try logging in instead.");
    }

    // 🧹 Cleanup if email is already in DB (from deleted account)
    Optional<User> existingUser = userRepository.findByEmailIgnoreCase(email);
    if (existingUser.isPresent()) {
        System.out.println("[⚠️] Existing user found by email. Cleaning up local record...");
        preferenceRepository.findByUser(existingUser.get()).ifPresent(preferenceRepository::delete);
        userRepository.delete(existingUser.get());
        userRepository.flush();  // 💥 critical!
    }

    // ✅ Create new user
    User user = new User();
    user.setName(name);
    user.setEmail(email);
    user.setFirebaseUid(uid);
    user.setCreatedAt(LocalDateTime.now());
    user.setMajor(req.getMajor());
    user.setYear(req.getYear());
    userRepository.save(user); // Only save once

    Preference pref = new Preference();
    pref.setUser(user);
    pref.setAvailableDays("");
    pref.setSubjectsToLearn("");
    pref.setSubjectsToTeach("");
    preferenceRepository.save(pref);

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
                firebaseAuth.getUser(uid);
                firebaseAuth.deleteUser(uid);
                System.out.println("[✅] Firebase user deleted for UID: " + uid);
            } catch (FirebaseAuthException e) {
                if ("USER_NOT_FOUND".equals(e.getAuthErrorCode().name())) {
                    System.out.println("[ℹ️] Firebase user already deleted for UID: " + uid);
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("message", "Failed to delete Firebase user: " + e.getMessage()));
                }
            }

            preferenceRepository.findByUser(user).ifPresent(pref -> {
                preferenceRepository.delete(pref);
                System.out.println("[✅] Deleted preference for UID: " + uid);
            });

            userRepository.delete(user);
            userRepository.flush(); // Ensure DB deletion
            System.out.println("[✅] Deleted user from local DB: " + uid);

            return ResponseEntity.ok(Map.of("message", "Account deleted successfully."));

        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Invalid Firebase token: " + e.getMessage()));
        }
    }

    @GetMapping("/check-sync")
    public ResponseEntity<?> checkSync(
        @RequestHeader("Authorization") String authHeader
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(Map.of("message", "Missing or invalid Authorization header"));
        }

        String idToken = authHeader.substring(7);
        try {
            FirebaseToken decoded = firebaseAuth.verifyIdToken(idToken);
            String uid = decoded.getUid();
            String email = decoded.getEmail();

            boolean existsInLocal = userRepository.findByFirebaseUid(uid).isPresent();

            Map<String, Object> result = new HashMap<>();
            result.put("firebaseUid", uid);
            result.put("email", email);
            result.put("localUserExists", existsInLocal);

            return ResponseEntity.ok(result);
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", "Invalid Firebase token", "details", e.getMessage()));
        }
    }

    @DeleteMapping("/force-delete-local")
    public ResponseEntity<?> forceDeleteLocalOnly(@RequestParam String email) {
        Optional<User> userOpt = userRepository.findByEmailIgnoreCase(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No local user found with that email.");
        }

        User user = userOpt.get();

        preferenceRepository.findByUser(user).ifPresent(preferenceRepository::delete);
        userRepository.delete(user);

        return ResponseEntity.ok("Local user manually deleted.");
    }
}
