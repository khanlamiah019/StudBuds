package com.studbuds.controller;

import com.studbuds.model.Preference;
import com.studbuds.model.User;
import com.studbuds.repository.PreferenceRepository;
import com.studbuds.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        user.setMajor(updatedUser.getMajor());
        user.setYear(updatedUser.getYear());
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/{userId}/preference")
    public ResponseEntity<?> updatePreference(@PathVariable Long userId, @RequestBody Preference preferenceData) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        User user = userOpt.get();
        Optional<Preference> existingPreferenceOpt = preferenceRepository.findByUser(user);
        Preference preference;
        if (existingPreferenceOpt.isPresent()) {
            // Update existing preference
            preference = existingPreferenceOpt.get();
            preference.setAvailableDays(preferenceData.getAvailableDays());
            preference.setSubjectsToLearn(preferenceData.getSubjectsToLearn());
            preference.setSubjectsToTeach(preferenceData.getSubjectsToTeach());
        } else {
            // Create new preference and associate with user
            preference = new Preference();
            preference.setUser(user);
            preference.setAvailableDays(preferenceData.getAvailableDays());
            preference.setSubjectsToLearn(preferenceData.getSubjectsToLearn());
            preference.setSubjectsToTeach(preferenceData.getSubjectsToTeach());
        }
        Preference savedPreference = preferenceRepository.save(preference);
        return ResponseEntity.ok(savedPreference);
    }
}
