package com.studbuds.controller;

import com.studbuds.model.User;
import com.studbuds.repository.UserRepository;
import com.studbuds.service.MatchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/matches")
public class MatchingController {
    @Autowired
    private MatchingService matchingService;
    
    @Autowired
    private UserRepository userRepository;
    
    @GetMapping("/find/{userId}")
    public ResponseEntity<?> findMatches(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }
        List<User> matches = matchingService.findMatches(user);
        return ResponseEntity.ok(matches);
    }
}