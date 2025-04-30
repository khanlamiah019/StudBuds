package com.studbuds.controller;

import com.studbuds.model.Preference;
import com.studbuds.model.User;
import com.studbuds.repository.PreferenceRepository;
import com.studbuds.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PreferenceRepository preferenceRepository;

    // ✅ Basic endpoint to confirm server is alive
    @GetMapping
    public String helloTest() {
        return "✅ StudBuds backend is up and responding!";
    }

    // ✅ Optional: Safely test user data without crashing on DB error
    @GetMapping("/users")
    public List<User> getAllUsers() {
        try {
            return userRepository.findAll();
        } catch (Exception e) {
            System.err.println("Error fetching users: " + e.getMessage());
            return List.of(); // return empty list to avoid crash
        }
    }

    // ✅ Optional: Safely test preference data
    @GetMapping("/preferences")
    public List<Preference> getAllPreferences() {
        try {
            return preferenceRepository.findAll();
        } catch (Exception e) {
            System.err.println("Error fetching preferences: " + e.getMessage());
            return List.of(); // return empty list to avoid crash
        }
    }
}
