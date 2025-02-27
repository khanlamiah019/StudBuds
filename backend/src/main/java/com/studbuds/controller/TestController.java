package com.studbuds.controller;

import com.studbuds.model.User;
import com.studbuds.model.Preference;
import com.studbuds.repository.UserRepository;
import com.studbuds.repository.PreferenceRepository;
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

    // A simple test endpoint
    @GetMapping("/hello")
    public String hello() {
        return "Hello, this is a test endpoint.";
    }

    // Endpoint to retrieve all users
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Endpoint to retrieve all preferences
    @GetMapping("/preferences")
    public List<Preference> getAllPreferences() {
        return preferenceRepository.findAll();
    }
}
