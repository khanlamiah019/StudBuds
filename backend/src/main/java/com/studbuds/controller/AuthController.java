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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PreferenceRepository preferenceRepository;

    @Autowired
    private FirebaseAuth firebaseAuth;

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody SignupRequest signupRequest) {
        try {
            // Check if the user already exists in the local DB.
            if (userRepository.findByEmail(signupRequest.getEmail()).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("User already exists. Please sign in.");
            }

            // Retrieve the Firebase user record (assumes the client already created the
            // user).
            UserRecord userRecord = firebaseAuth.getUserByEmail(signupRequest.getEmail());

            // Create the local user record and store major/year directly.
            User user = new User();
            user.setName(signupRequest.getName());
            user.setEmail(signupRequest.getEmail());
            user.setFirebaseUid(userRecord.getUid());
            user.setCreatedAt(LocalDateTime.now());
            user.setMajor(signupRequest.getMajor());
            user.setYear(signupRequest.getYear());
            userRepository.save(user);

            // Create and link the preference record (for available days and subjects).
            Preference preference = new Preference();
            preference.setUser(user);
            // Other fields can be empty or default.
            preference.setAvailableDays("");
            preference.setSubjectsToLearn("");
            preference.setSubjectsToTeach("");
            preferenceRepository.save(preference);
            user.setPreference(preference);
            userRepository.save(user);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully.");
            response.put("userId", user.getId());
            return ResponseEntity.ok(response);
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Firebase error: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Verify the Firebase token.
            FirebaseToken decodedToken = firebaseAuth.verifyIdToken(loginRequest.getFirebaseToken());
            String email = decodedToken.getEmail();
            Optional<User> userOptional = userRepository.findByEmail(email);

            // If local user record is not found, the user must sign up first.
            if (!userOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("No local account found. Please sign up before logging in.");
            }

            User user = userOptional.get();
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful.");
            response.put("userId", user.getId());
            return ResponseEntity.ok(response);
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid Firebase token.");
        }
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteAccount(@RequestBody DeleteAccountRequest req) {
        try {
            // 1) Verify incoming token
            FirebaseToken decoded = firebaseAuth.verifyIdToken(req.getFirebaseToken());
            String email = decoded.getEmail();

            // 2) Look up local user
            Optional<User> userOpt = userRepository.findByEmail(email);
            if (userOpt.isEmpty()) {
                return ResponseEntity
                        .status(404)
                        .body(Map.of("message", "User not found."));
            }

            User user = userOpt.get();

            // 3) Delete from Firebase
            firebaseAuth.deleteUser(user.getFirebaseUid());

            // 4) Delete any linked Preference row
            preferenceRepository
                .findByUser(user)
                .ifPresent(preferenceRepository::delete);

            // 5) Delete local user
            userRepository.delete(user);

            // 6) Return JSON
            return ResponseEntity.ok(Map.of("message", "Account deleted successfully."));
        } catch (FirebaseAuthException e) {
            return ResponseEntity
                    .status(500)
                    .body(Map.of("message", "Firebase error: " + e.getMessage()));
        }
    }
}
