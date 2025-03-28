package com.studbuds.controller;

import com.studbuds.model.Preference;
import com.studbuds.model.User;
import com.studbuds.repository.PreferenceRepository;
import com.studbuds.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PreferenceRepository preferenceRepository;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserDetails(@PathVariable Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        return userOpt.map(ResponseEntity::ok)
                      .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody User updatedUser) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        User user = userOpt.get();
        // Update basic user details (name, email, major, and year)
        user.setName(updatedUser.getName());
        user.setEmail(updatedUser.getEmail());
        user.setMajor(updatedUser.getMajor());
        user.setYear(updatedUser.getYear());
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/{userId}/preference")
    public ResponseEntity<?> updatePreference(@PathVariable Long userId, @RequestBody Map<String, Object> prefData) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        User user = userOpt.get();
        
        // Update major and year directly on the User
        if (prefData.get("major") != null) {
            user.setMajor((String) prefData.get("major"));
        }
        if (prefData.get("year") != null) {
            user.setYear((String) prefData.get("year"));
        }
        userRepository.save(user);
        
        // Update or create the Preference record for the other fields.
        Optional<Preference> existingPreferenceOpt = preferenceRepository.findByUser(user);
        Preference preference;
        if (existingPreferenceOpt.isPresent()) {
            preference = existingPreferenceOpt.get();
        } else {
            preference = new Preference();
            preference.setUser(user);
        }
        
        if (prefData.get("availableDays") != null) {
            preference.setAvailableDays((String) prefData.get("availableDays"));
        }
        if (prefData.get("subjectsToLearn") != null) {
            preference.setSubjectsToLearn((String) prefData.get("subjectsToLearn"));
        }
        if (prefData.get("subjectsToTeach") != null) {
            preference.setSubjectsToTeach((String) prefData.get("subjectsToTeach"));
        }
        
        Preference savedPreference = preferenceRepository.save(preference);
        return ResponseEntity.ok(savedPreference);
    }
}