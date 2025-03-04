package com.studbuds.service;

import com.studbuds.model.Preference;
import com.studbuds.model.User;
import com.studbuds.repository.PreferenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Main service that computes match scores between users based on:
 *  - Major & Year (custom query for narrowing)
 *  - Availability overlap
 *  - Teaching/Learning synergy
 *  - Shared classes
 *  - Weighted scoring system
 */
@Service
public class MatchingService {

    @Autowired
    private PreferenceRepository preferenceRepository;

    // ========== Weight Constants ========== //
    private static final double AVAILABILITY_WEIGHT     = 2.0;  // Shared availability is important
    private static final double PARTIAL_SYNERGY_WEIGHT  = 0.5;  // One-way teaching
    private static final double BOTH_SYNERGY_BONUS      = 0.5;  // Extra bonus if two-way teaching
    private static final double MATCH_THRESHOLD         = 1.0;  // Minimal score for a valid match

    /**
     * 1) Query for same major/year candidates.
     * 2) If none found, fallback to all except current user.
     * 3) Compute scores and sort by descending match score.
     */
    public List<MatchingResultDTO> findMatches(User currentUser) {
        // Current user's preference
        Preference currentPref = currentUser.getPreference();
        if (currentPref == null) {
            // If no preference, no matches
            return Collections.emptyList();
        }

        // Parse current user's preference fields into sets
        Set<String> currentDays    = parseCSV(currentPref.getAvailableDays());
        Set<String> currentTeach   = parseCSV(currentPref.getSubjectsToTeach());
        Set<String> currentLearn   = parseCSV(currentPref.getSubjectsToLearn());

        // 1) Narrow query
        List<Preference> narrowCandidates = preferenceRepository.findSimilarPreferences(
                currentUser.getMajor(),
                currentUser.getYear(),
                currentUser.getId()
        );

        // Compute matches for the narrow set
        List<MatchingResultDTO> narrowResults = computeMatches(
                currentUser, currentDays, currentTeach, currentLearn, narrowCandidates
        );

        // If we found some results, sort them and return
        if (!narrowResults.isEmpty()) {
            narrowResults.sort((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore()));
            return narrowResults;
        }

        // 2) Fallback query
        List<Preference> fallbackCandidates = preferenceRepository.findAll().stream()
            .filter(p -> !p.getUser().getId().equals(currentUser.getId()))
            .collect(Collectors.toList());
        List<MatchingResultDTO> fallbackResults = computeMatches(
                currentUser, currentDays, currentTeach, currentLearn, fallbackCandidates
        );

        // Sort or return empty
        if (fallbackResults.isEmpty()) {
            return Collections.emptyList();
        } else {
            fallbackResults.sort((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore()));
            return fallbackResults;
        }
    }

    /**
     * Core matching logic:
     *  - Check availability overlap (optional but weighted)
     *  - Check teaching synergy (partial or two-way)
     *  - Check if they share classes (strong or weak bonus)
     */
    private List<MatchingResultDTO> computeMatches(
            User currentUser,
            Set<String> currentDays,
            Set<String> currentTeach,
            Set<String> currentLearn,
            List<Preference> candidates
    ) {
        List<MatchingResultDTO> results = new ArrayList<>();

        for (Preference otherPref : candidates) {
            User otherUser = otherPref.getUser();
            if (otherUser.getId().equals(currentUser.getId())) {
                // skip if it's the same user (edge case)
                continue;
            }

            // Parse candidate's fields
            Set<String> otherDays    = parseCSV(otherPref.getAvailableDays());
            Set<String> otherTeach   = parseCSV(otherPref.getSubjectsToTeach());
            Set<String> otherLearn   = parseCSV(otherPref.getSubjectsToLearn());

            // Availability intersection
            Set<String> commonDays = new HashSet<>(currentDays);
            commonDays.retainAll(otherDays);

            // Teaching synergy: 
            //  p1TeachesP2 = intersection of currentTeach & otherLearn
            //  p2TeachesP1 = intersection of currentLearn & otherTeach
            Set<String> p1TeachesP2 = new HashSet<>(currentTeach);
            p1TeachesP2.retainAll(otherLearn);

            Set<String> p2TeachesP1 = new HashSet<>(currentLearn);
            p2TeachesP1.retainAll(otherTeach);
            double score = 0.0;
            // If they share at least some availability, add AVAILABILITY_WEIGHT
            if (!commonDays.isEmpty()) {
                score += AVAILABILITY_WEIGHT;
            }

            // Partial synergy
            if (!p1TeachesP2.isEmpty()) {
                score += PARTIAL_SYNERGY_WEIGHT;
            }
            if (!p2TeachesP1.isEmpty()) {
                score += PARTIAL_SYNERGY_WEIGHT;
            }
            // Two-way synergy bonus
            if (!p1TeachesP2.isEmpty() && !p2TeachesP1.isEmpty()) {
                score += BOTH_SYNERGY_BONUS;
            }


            // Filter out matches below threshold
            if (score < MATCH_THRESHOLD) {
                continue;
            }

            // Build the result DTO
            MatchingResultDTO dto = new MatchingResultDTO();
            dto.setUser(otherUser);
            dto.setCommonDays(new ArrayList<>(commonDays));
            dto.setMatchScore(score);

            // Combine synergy subjects
            Set<String> synergySubjects = new HashSet<>(p1TeachesP2);
            synergySubjects.addAll(p2TeachesP1);
            dto.setCommonSubjects(new ArrayList<>(synergySubjects));


            results.add(dto);
        }

        return results;
    }

    /**
     * Utility method to parse a CSV string ("a,b,c") into a set of lower-case, trimmed strings.
     */
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

    /**
     * DTO for returning matching results.
     */
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
