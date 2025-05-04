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

    public void setPreferenceRepository(PreferenceRepository prefRepo) {
        this.preferenceRepository = prefRepo;
    }

    private static final double AVAILABILITY_WEIGHT = 2.0;
    private static final double PARTIAL_SYNERGY_WEIGHT = 0.5;
    private static final double BOTH_SYNERGY_BONUS = 0.5;
    private static final double MATCH_THRESHOLD = 1.0;

    public List<MatchingResultDTO> findMatches(User currentUser) {
        Preference currentPref = currentUser.getPreference();
        if (currentPref == null) {
            return Collections.emptyList();
        }

        Set<String> currentDays = parseCSV(currentPref.getAvailableDays());
        Set<String> currentTeach = parseCSV(currentPref.getSubjectsToTeach());
        Set<String> currentLearn = parseCSV(currentPref.getSubjectsToLearn());

        List<Preference> narrowCandidates = preferenceRepository.findSimilarPreferences(
                currentUser.getMajor(),
                currentUser.getYear(),
                currentUser.getId()
        );

        List<MatchingResultDTO> narrowResults = computeMatches(currentUser, currentDays, currentTeach, currentLearn, narrowCandidates);
        if (!narrowResults.isEmpty()) {
            narrowResults.sort((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore()));
            return narrowResults;
        }

        List<Preference> fallbackCandidates = preferenceRepository.findAll().stream()
                .filter(p -> !p.getUser().getId().equals(currentUser.getId()))
                .collect(Collectors.toList());

        List<MatchingResultDTO> fallbackResults = computeMatches(currentUser, currentDays, currentTeach, currentLearn, fallbackCandidates);
        fallbackResults.sort((a, b) -> Double.compare(b.getMatchScore(), a.getMatchScore()));
        return fallbackResults;
    }

    private List<MatchingResultDTO> computeMatches(User currentUser,
                                                   Set<String> currentDays,
                                                   Set<String> currentTeach,
                                                   Set<String> currentLearn,
                                                   List<Preference> candidates) {
        List<MatchingResultDTO> results = new ArrayList<>();
        for (Preference otherPref : candidates) {
            User otherUser = otherPref.getUser();
            if (otherUser.getId().equals(currentUser.getId())) {
                continue;
            }

            Set<String> otherDays = parseCSV(otherPref.getAvailableDays());
            Set<String> otherTeach = parseCSV(otherPref.getSubjectsToTeach());
            Set<String> otherLearn = parseCSV(otherPref.getSubjectsToLearn());

            Set<String> commonDays = new HashSet<>(currentDays);
            commonDays.retainAll(otherDays);

            Set<String> p1TeachesP2 = new HashSet<>(currentTeach);
            p1TeachesP2.retainAll(otherLearn);

            Set<String> p2TeachesP1 = new HashSet<>(currentLearn);
            p2TeachesP1.retainAll(otherTeach);

            double score = 0.0;
            if (!commonDays.isEmpty()) score += AVAILABILITY_WEIGHT;
            if (!p1TeachesP2.isEmpty()) score += PARTIAL_SYNERGY_WEIGHT;
            if (!p2TeachesP1.isEmpty()) score += PARTIAL_SYNERGY_WEIGHT;
            if (!p1TeachesP2.isEmpty() && !p2TeachesP1.isEmpty()) score += BOTH_SYNERGY_BONUS;

            if (score < MATCH_THRESHOLD) continue;

            MatchingResultDTO dto = new MatchingResultDTO();
            dto.setUser(otherUser);
            dto.setCommonDays(new ArrayList<>(commonDays));
            dto.setMatchScore(score);

            Set<String> synergySubjects = new HashSet<>(p1TeachesP2);
            synergySubjects.addAll(p2TeachesP1);
            dto.setCommonSubjects(new ArrayList<>(synergySubjects));

            results.add(dto);
        }
        return results;
    }

    private Set<String> parseCSV(String csv) {
        if (csv == null || csv.trim().isEmpty()) return Collections.emptySet();
        return Arrays.stream(csv.split(","))
                .map(s -> s.trim().toLowerCase())
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }

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
