package com.studbuds.service;

import com.studbuds.model.Preference;
import com.studbuds.model.User;
import com.studbuds.repository.PreferenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MatchingService {
    @Autowired
    private PreferenceRepository preferenceRepository;
    
    // Basic matching: users with the same major (can be expanded later)
    public List<User> findMatches(User currentUser) {
        List<Preference> allPreferences = preferenceRepository.findAll();
        return allPreferences.stream()
            .filter(pref -> !pref.getUser().getId().equals(currentUser.getId()))
            .filter(pref -> pref.getUser().getMajor().equalsIgnoreCase(currentUser.getMajor()))
            .map(Preference::getUser)
            .collect(Collectors.toList());
    }
}