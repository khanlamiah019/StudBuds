package com.studbuds.service;

import com.studbuds.model.Preference;
import com.studbuds.model.User;
import com.studbuds.repository.PreferenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MatchingService {

    @Autowired
    private PreferenceRepository preferenceRepository;

    /**
     * Finds matches for the given current user.
     * A match is valid if there is at least one common available day and at least one subject match
     * (i.e. current user's subjectsToTeach intersect with the other's subjectsToLearn OR
     * current user's subjectsToLearn intersect with the other's subjectsToTeach).
     * The match score is calculated as the total number of common days plus the number of matching subjects.
     * Returns a list of MatchingResultDTO sorted by match score in descending order.
     * If no valid matches are found, returns all other users in random order with a score of 0.
     */
    public List<MatchingResultDTO> findMatches(User currentUser) {
        Preference currentPref = currentUser.getPreference();
        if (currentPref == null) {
            return Collections.emptyList();
        }
        // Parse current user's available days and subjects (to teach and to learn)
        Set<String> currentDays = parseCSV(currentPref.getAvailableDays());
        Set<String> currentTeach = parseCSV(currentPref.getSubjectsToTeach());
        Set<String> currentLearn = parseCSV(currentPref.getSubjectsToLearn());

        List<Preference> allPrefs = preferenceRepository.findAll();
        Map<Long, MatchingResultDTO> uniqueMatches = new HashMap<>();

        for (Preference otherPref : allPrefs) {
            if (otherPref.getUser().getId().equals(currentUser.getId())) {
                continue; // Skip current user's own preferences.
            }
            Set<String> otherDays = parseCSV(otherPref.getAvailableDays());
            Set<String> otherTeach = parseCSV(otherPref.getSubjectsToTeach());
            Set<String> otherLearn = parseCSV(otherPref.getSubjectsToLearn());

            // Calculate common available days.
            Set<String> commonDays = new HashSet<>(currentDays);
            commonDays.retainAll(otherDays);

            // Calculate common subjects: currentTeach vs otherLearn and currentLearn vs otherTeach.
            Set<String> commonSubjects = new HashSet<>(currentTeach);
            commonSubjects.retainAll(otherLearn);
            Set<String> commonSubjects2 = new HashSet<>(currentLearn);
            commonSubjects2.retainAll(otherTeach);
            commonSubjects.addAll(commonSubjects2);

            if (!commonDays.isEmpty() && !commonSubjects.isEmpty()) {
                double score = commonDays.size() + commonSubjects.size();
                Long otherUserId = otherPref.getUser().getId();
                // If a match for this user already exists, keep the one with the higher score.
                if (!uniqueMatches.containsKey(otherUserId) || uniqueMatches.get(otherUserId).getMatchScore() < score) {
                    MatchingResultDTO dto = new MatchingResultDTO();
                    dto.setUser(otherPref.getUser());
                    dto.setCommonDays(new ArrayList<>(commonDays));
                    dto.setCommonSubjects(new ArrayList<>(commonSubjects));
                    dto.setMatchScore(score);
                    uniqueMatches.put(otherUserId, dto);
                }
            }
        }

        List<MatchingResultDTO> results = new ArrayList<>(uniqueMatches.values());
        if (results.isEmpty()) {
            // No valid matches: return all other users in random order with a score of 0.
            List<Preference> randomPrefs = allPrefs.stream()
                    .filter(p -> !p.getUser().getId().equals(currentUser.getId()))
                    .collect(Collectors.toList());
            Collections.shuffle(randomPrefs);
            for (Preference p : randomPrefs) {
                MatchingResultDTO dto = new MatchingResultDTO();
                dto.setUser(p.getUser());
                dto.setCommonDays(Collections.emptyList());
                dto.setCommonSubjects(Collections.emptyList());
                dto.setMatchScore(0);
                results.add(dto);
            }
        } else {
            // Sort the valid matches by score descending.
            results.sort((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore()));
        }
        return results;
    }

    // Helper method to parse a comma-separated string into a set of lower-case, trimmed strings.
    private Set<String> parseCSV(String csv) {
        if (csv == null || csv.trim().isEmpty()) {
            return Collections.emptySet();
        }
        return Arrays.stream(csv.split(","))
                     .map(String::trim)
                     .filter(s -> !s.isEmpty())
                     .map(String::toLowerCase)
                     .collect(Collectors.toSet());
    }

    // DTO for matching results.
    public static class MatchingResultDTO {
        private User user;
        private List<String> commonDays;
        private List<String> commonSubjects;
        private double matchScore;

        public User getUser() {
            return user;
        }
        public void setUser(User user) {
            this.user = user;
        }
        public List<String> getCommonDays() {
            return commonDays;
        }
        public void setCommonDays(List<String> commonDays) {
            this.commonDays = commonDays;
        }
        public List<String> getCommonSubjects() {
            return commonSubjects;
        }
        public void setCommonSubjects(List<String> commonSubjects) {
            this.commonSubjects = commonSubjects;
        }
        public double getMatchScore() {
            return matchScore;
        }
        public void setMatchScore(double matchScore) {
            this.matchScore = matchScore;
        }
    }
}
