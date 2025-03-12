package com.studbuds.controller;

import com.studbuds.model.Preference;
import com.studbuds.model.User;
import com.studbuds.payload.DeleteAccountRequest;
import com.studbuds.payload.LoginRequest;
import com.studbuds.payload.SignupRequest;
import com.studbuds.repository.UserRepository;
import com.studbuds.repository.PreferenceRepository;
import com.studbuds.repository.MatchRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

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
    private PreferenceRepository preferenceRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private PreferenceRepository preferenceRepository;

    @Autowired
    private MatchRepository matchRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody SignupRequest signupRequest) {
        if (userRepository.findByEmail(signupRequest.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already in use.");
        }
        User user = new User();
        user.setName(signupRequest.getName());  // Store the name from the request
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setMajor(signupRequest.getMajor());
        user.setYear(signupRequest.getYear());
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully.");
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<User> userOptional = userRepository.findByEmail(loginRequest.getEmail());
        if (!userOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not found.");
        }
        User user = userOptional.get();
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials.");
        }
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Login successful.");
        response.put("userId", user.getId());
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteAccount(@RequestBody DeleteAccountRequest deleteAccountRequest) {
        try {
            // Check if user exists
            Optional<User> userOpt = userRepository.findByEmail(deleteAccountRequest.getEmail());
            if (!userOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }

            User user = userOpt.get();

            // Validate password
            if (!passwordEncoder.matches(deleteAccountRequest.getPassword(), user.getPassword())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid credentials.");
            }

            // Delete associated preference if it exists
            Optional<Preference> preferenceOpt = preferenceRepository.findByUser(user);
            preferenceOpt.ifPresent(preferenceRepository::delete);

            // Delete all matches involving this user
            matchRepository.deleteAll(matchRepository.findAllByUser(user));

            // Now, delete the user
            userRepository.delete(user);

            return ResponseEntity.ok("Account deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An unexpected error occurred: " + e.getMessage());
        }
    }

@DeleteMapping("/delete")
public ResponseEntity<?> deleteAccount(@RequestBody DeleteAccountRequest deleteAccountRequest) {
    try {
        // Check if user exists
        Optional<User> userOpt = userRepository.findByEmail(deleteAccountRequest.getEmail());
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }

        User user = userOpt.get();

        // Validate password
        if (!passwordEncoder.matches(deleteAccountRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid credentials.");
        }

        // Delete associated preference if it exists
        Optional<Preference> preferenceOpt = preferenceRepository.findByUser(user);
        preferenceOpt.ifPresent(preferenceRepository::delete);

        // Delete all matches involving this user
        matchRepository.deleteAll(matchRepository.findAllByUser(user));

        // Now, delete the user
        userRepository.delete(user);

        return ResponseEntity.ok("Account deleted successfully.");
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("An unexpected error occurred: " + e.getMessage());
    }
}    
}
