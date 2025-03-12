package com.studbuds.controller;

import com.studbuds.model.Match;
import com.studbuds.model.Swipe;
import com.studbuds.model.User;
import com.studbuds.repository.MatchRepository;
import com.studbuds.repository.SwipeRepository;
import com.studbuds.repository.UserRepository;
import com.studbuds.service.MatchingService;
import com.studbuds.service.MatchingService.MatchingResultDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/matches")
public class MatchingController {

    @Autowired
    private MatchingService matchingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MatchRepository matchRepository;
    
    @Autowired
    private SwipeRepository swipeRepository;

    // Existing endpoint to get matching suggestions.
    @GetMapping("/find/{userId}")
    public ResponseEntity<?> findMatches(@PathVariable Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
        User currentUser = userOpt.get();
        if (currentUser.getPreference() == null) {
            return ResponseEntity.ok("No preferences set for this user.");
        }
        List<MatchingResultDTO> matches = matchingService.findMatches(currentUser);
        return ResponseEntity.ok(matches);
    }

    /**
     * New endpoint for swiping.
     * When a user swipes right on another user:
     * - If the other user has already swiped right (pending swipe exists), a match is created.
     * - Otherwise, a new pending swipe record is created.
     */
    @PostMapping("/swipe")
    public ResponseEntity<?> swipe(@RequestParam("user1Id") Long user1Id,
                                   @RequestParam("user2Id") Long user2Id) {
        Optional<User> user1Opt = userRepository.findById(user1Id);
        Optional<User> user2Opt = userRepository.findById(user2Id);
        if (!user1Opt.isPresent() || !user2Opt.isPresent()) {
            return ResponseEntity.badRequest().body("One or both users not found.");
        }
        User user1 = user1Opt.get(); // Current user swiping right.
        User user2 = user2Opt.get(); // Target user.
    
        // Check if a confirmed match already exists.
        Optional<Match> existingMatch = matchRepository.findExistingMatch(user1, user2);
        if (existingMatch.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("These users have already matched.");
        }
    
        // Check if user1 has already swiped right on user2.
        Optional<Swipe> existingSwipe = swipeRepository.findAll().stream()
                .filter(swipe -> swipe.getFromUser().equals(user1) && swipe.getToUser().equals(user2))
                .findFirst();
        if (existingSwipe.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("You have already swiped on this user.");
        }
    
        // Check if the other user (user2) has already swiped right on user1.
        Optional<Swipe> reciprocalSwipeOpt = swipeRepository.findAll().stream()
                .filter(swipe -> swipe.getFromUser().equals(user2) && swipe.getToUser().equals(user1))
                .findFirst();
    
        if (reciprocalSwipeOpt.isPresent()) {
            // Reciprocal swipe found: Remove the pending swipe and create a confirmed match.
            swipeRepository.delete(reciprocalSwipeOpt.get());
            Match match = new Match();
            match.setUser1(user1);
            match.setUser2(user2);
            match.setMatchDate(LocalDateTime.now());
            matchRepository.save(match);
            return ResponseEntity.ok(match);
        } else {
            // No reciprocal swipe exists: Record this swipe as pending.
            Swipe swipe = new Swipe();
            swipe.setFromUser(user1);
            swipe.setToUser(user2);
            swipeRepository.save(swipe);
            return ResponseEntity.ok("Swipe recorded; waiting for mutual interest.");
        }
    }
    
    // Existing endpoints (if any) remain here.

    // For example, your profile endpoint for confirmed and pending matches.
    @GetMapping("/profile/{userId}")
    public ResponseEntity<?> getProfileMatches(@PathVariable Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
        User user = userOpt.get();
        List<Match> confirmedMatches = matchRepository.findByUser1OrUser2(user, user);
        List<User> confirmedMatchUsers = confirmedMatches.stream()
                .map(match -> match.getUser1().equals(user) ? match.getUser2() : match.getUser1())
                .collect(Collectors.toList());
        
        List<Swipe> pendingSwipes = swipeRepository.findByFromUser(user);
        List<User> pendingMatchUsers = pendingSwipes.stream()
                .map(Swipe::getToUser)
                .collect(Collectors.toList());
        
        Map<String, Object> result = new HashMap<>();
        result.put("confirmedMatches", confirmedMatchUsers);
        result.put("pendingMatches", pendingMatchUsers);
        return ResponseEntity.ok(result);
    }
}
