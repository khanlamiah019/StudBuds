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
            
            // Retrieve the Firebase user record (assumes the client already created the user).
            UserRecord userRecord = firebaseAuth.getUserByEmail(signupRequest.getEmail());
            
            // Create the local user record.
            User user = new User();
            user.setName(signupRequest.getName());
            user.setEmail(signupRequest.getEmail());
            user.setFirebaseUid(userRecord.getUid());
            user.setCreatedAt(LocalDateTime.now());
            userRepository.save(user);
            
            // Create and link the preference record.
            Preference preference = new Preference();
            preference.setUser(user);
            preference.setMajor(signupRequest.getMajor());
            preference.setYear(signupRequest.getYear());
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
            
            // If local user record is not found, create it automatically.
            if (!userOptional.isPresent()) {
                User user = new User();
                user.setEmail(email);
                user.setFirebaseUid(decodedToken.getUid());
                user.setName(decodedToken.getName() != null ? decodedToken.getName() : email.split("@")[0]);
                user.setCreatedAt(LocalDateTime.now());
                userRepository.save(user);
                userOptional = Optional.of(user);
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

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteAccount(@RequestBody DeleteAccountRequest deleteAccountRequest) {
        try {
            Optional<User> userOpt = userRepository.findByEmail(deleteAccountRequest.getEmail());
            if (!userOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }
            User user = userOpt.get();
            
            // Delete the user from Firebase using the stored UID.
            firebaseAuth.deleteUser(user.getFirebaseUid());
            
            // Delete associated preference (if exists)
            Optional<Preference> preferenceOpt = preferenceRepository.findByUser(user);
            preferenceOpt.ifPresent(preferenceRepository::delete);
            
            // Delete the local user record.
            userRepository.delete(user);
            
            return ResponseEntity.ok("Account deleted successfully.");
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Firebase error: " + e.getMessage());
        }
    }

}