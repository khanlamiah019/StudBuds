package com.studbuds.controller;

import com.studbuds.model.Preference;
import com.studbuds.model.User;
import com.studbuds.repository.PreferenceRepository;
import com.studbuds.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PreferenceRepository preferenceRepository;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserDetails(@PathVariable Long userId) {
        return userRepository.findById(userId)
            .map(ResponseEntity::ok)
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @RequestBody User updatedUser) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        User user = userOpt.get();
        user.setName(updatedUser.getName());
        user.setEmail(updatedUser.getEmail());
        user.setMajor(updatedUser.getMajor());
        user.setYear(updatedUser.getYear());
        userRepository.save(user);
        return ResponseEntity.ok(user);
    }

    /** Fetch existing—or blank—preferences for this user */
    @GetMapping("/{userId}/preference")
    public ResponseEntity<?> getPreference(@PathVariable Long userId) {
        return userRepository.findById(userId)
            .map(user -> {
                Optional<Preference> prefOpt = preferenceRepository.findByUser(user);
                if (prefOpt.isPresent()) {
                    return ResponseEntity.ok(prefOpt.get());
                } else {
                    Preference empty = new Preference();
                    empty.setUser(user);
                    empty.setAvailableDays("");
                    empty.setSubjectsToLearn("");
                    empty.setSubjectsToTeach("");
                    return ResponseEntity.ok(empty);
                }
            })
            .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /** Create or update preferences */
    @PostMapping("/{userId}/preference")
    public ResponseEntity<?> updatePreference(
            @PathVariable Long userId,
            @RequestBody Map<String, Object> prefData
    ) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        User user = userOpt.get();

        if (prefData.get("major") != null) {
            user.setMajor((String) prefData.get("major"));
        }
        if (prefData.get("year") != null) {
            user.setYear((String) prefData.get("year"));
        }
        userRepository.save(user);

        Preference preference = preferenceRepository.findByUser(user)
            .orElseGet(() -> {
                Preference p = new Preference();
                p.setUser(user);
                return p;
            });

        if (prefData.get("availableDays") != null) {
            preference.setAvailableDays((String) prefData.get("availableDays"));
        }
        if (prefData.get("subjectsToLearn") != null) {
            preference.setSubjectsToLearn((String) prefData.get("subjectsToLearn"));
        }
        if (prefData.get("subjectsToTeach") != null) {
            preference.setSubjectsToTeach((String) prefData.get("subjectsToTeach"));
        }

        Preference saved = preferenceRepository.save(preference);
        return ResponseEntity.ok(saved);
    }

    /** Delete (clear) this user’s preferences */
    @DeleteMapping("/{userId}/preference")
    public ResponseEntity<?> clearPreference(@PathVariable Long userId) {
        return userRepository.findById(userId)
            .map(user -> {
                preferenceRepository.findByUser(user)
                    .ifPresent(preferenceRepository::delete);
                return ResponseEntity.noContent().build();
            })
            .orElseGet(() -> ResponseEntity.notFound().build());
    }
}