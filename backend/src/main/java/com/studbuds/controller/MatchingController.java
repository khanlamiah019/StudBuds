package com.studbuds.controller;

import com.studbuds.model.Match;
import com.studbuds.model.User;
import com.studbuds.repository.MatchRepository;
import com.studbuds.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/matches")
public class MatchingController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MatchRepository matchRepository;

    // Endpoint to record a match between two users.
    // Only creates a match if none exists between the given users.
    @PostMapping("/connect")
    public ResponseEntity<?> connectMatch(@RequestParam("user1Id") Long user1Id,
                                          @RequestParam("user2Id") Long user2Id) {
        Optional<User> user1Opt = userRepository.findById(user1Id);
        Optional<User> user2Opt = userRepository.findById(user2Id);
        
        if (!user1Opt.isPresent() || !user2Opt.isPresent()) {
            return ResponseEntity.badRequest().body("One or both users not found.");
        }
        
        User user1 = user1Opt.get();
        User user2 = user2Opt.get();
        
        // Check if a match already exists between these two users.
        Optional<Match> existingMatch = matchRepository.findExistingMatch(user1, user2);
        if (existingMatch.isPresent()) {
            return ResponseEntity.badRequest().body("These users have already matched.");
        }
        
        // Create a new match record if none exists.
        Match match = new Match();
        match.setUser1(user1);
        match.setUser2(user2);
        match.setMatchDate(LocalDateTime.now());
        matchRepository.save(match);
        
        return ResponseEntity.ok(match);
    }
}
