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
import com.studbuds.repository.MatchRepository;
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
    private MatchRepository matchRepository;

    @Autowired
    private FirebaseAuth firebaseAuth;

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody SignupRequest signupRequest) {
        if (userRepository.findByEmail(signupRequest.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already in use.");
        }
        try {
            // Create user in Firebase
            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(signupRequest.getEmail())
                    .setPassword(signupRequest.getPassword())
                    .setDisplayName(signupRequest.getName());
            UserRecord userRecord = firebaseAuth.createUser(request);
            
            // Create local user record (without password, major, and year)
            User user = new User();
            user.setName(userRecord.getDisplayName());
            user.setEmail(userRecord.getEmail());
            user.setFirebaseUid(userRecord.getUid());
            user.setCreatedAt(LocalDateTime.now());
            userRepository.save(user);
            
            // Create preference record with major and year data moved here
            Preference preference = new Preference();
            preference.setUser(user);
            preference.setMajor(signupRequest.getMajor());
            preference.setYear(signupRequest.getYear());
            preference.setAvailableDays("");
            preference.setSubjectsToLearn("");
            preference.setSubjectsToTeach("");
            preferenceRepository.save(preference);
            
            // Associate user with preference
            user.setPreference(preference);
            userRepository.save(user);
            
            return ResponseEntity.ok("User registered successfully.");
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Verify Firebase token sent from the client
            FirebaseToken decodedToken = firebaseAuth.verifyIdToken(loginRequest.getFirebaseToken());
            String email = decodedToken.getEmail();
            
            Optional<User> userOptional = userRepository.findByEmail(email);
            if (!userOptional.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found in local DB.");
            }
            User user = userOptional.get();
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful.");
            response.put("userId", user.getId());
            return ResponseEntity.ok(response);
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Firebase token.");
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
            // Delete user from Firebase using the stored Firebase UID
            firebaseAuth.deleteUser(user.getFirebaseUid());
            
            // Delete associated preference if exists
            Optional<Preference> preferenceOpt = preferenceRepository.findByUser(user);
            preferenceOpt.ifPresent(preferenceRepository::delete);
            
            // Delete all matches involving this user
            matchRepository.deleteAll(matchRepository.findAllByUser(user));
            
            // Delete the local user record
            userRepository.delete(user);
            
            return ResponseEntity.ok("Account deleted successfully.");
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}