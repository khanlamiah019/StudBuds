package com.studbuds.controller;

import com.studbuds.model.User;
import com.studbuds.repository.UserRepository;
import com.studbuds.service.MatchingService;
import com.studbuds.service.MatchingService.MatchingResultDTO;
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
        User currentUser = userRepository.findById(userId).orElse(null);
        if (currentUser == null) {
            return ResponseEntity.notFound().build();
        }
        List<MatchingResultDTO> matches = matchingService.findMatches(currentUser);
        return ResponseEntity.ok(matches);
    }
}
