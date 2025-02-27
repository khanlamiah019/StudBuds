package com.studbuds.controller;

import com.studbuds.model.Match;
import com.studbuds.model.User;
import com.studbuds.repository.MatchRepository;
import com.studbuds.repository.UserRepository;
import com.studbuds.service.MatchingService;
import com.studbuds.service.MatchingService.MatchingResultDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/matches")
public class MatchingController {

    @Autowired
    private MatchingService matchingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MatchRepository matchRepository;

    // Endpoint to get matching suggestions for a given user.
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

    // Endpoint to record a match between two users (only if they haven't matched before)
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

        // Check if a match already exists (in either order)
        Optional<Match> existingMatch = matchRepository.findExistingMatch(user1, user2);
        if (existingMatch.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("These users have already matched.");
        }

        // Create new match record
        Match match = new Match();
        match.setUser1(user1);
        match.setUser2(user2);
        match.setMatchDate(LocalDateTime.now());
        matchRepository.save(match);
        return ResponseEntity.ok(match);
    }
}
