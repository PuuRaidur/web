package com.matchme.recommendations.matching;

import com.matchme.recommendations.dto.RecommendationCandidate;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Core matching algorithm service.
 * Calculates compatibility scores between users based on various criteria.
 */
@Service
public class MatchingService {
    
    /**
     * Calculates matching scores for all candidates against the current user.
     *
     * @param currentUser The current user's data
     * @param candidates List of potential matches
     * @return List of candidates sorted by matching score (highest first)
     */
    public List<RecommendationCandidate> findBestMatches(RecommendationCandidate currentUser,
                                                          List<RecommendationCandidate> candidates) {

        MatchingProperties currentUserProps = MatchingProperties.fromRawData(
                currentUser.latitude,
                currentUser.longitude,
                currentUser.preferredDistanceKm,
                currentUser.hobbies,
                currentUser.musicPreferences,
                currentUser.foodPreferences,
                currentUser.interests,
                currentUser.lookingFor
        );
        
        return candidates.stream()
                .map(candidate -> {
                    MatchingScore score = calculateMatchingScore(currentUserProps, candidate);
                    return new ScoredCandidate(candidate, score);
                })
                .sorted(Comparator.comparingDouble(ScoredCandidate::getScore).reversed())
                .map(ScoredCandidate::getCandidate)
                .collect(Collectors.toList());
    }
    
    /**
     * Calculates the matching score between current user and a candidate.
     */
    private MatchingScore calculateMatchingScore(MatchingProperties currentUser,
                                                  RecommendationCandidate candidate) {

        MatchingProperties candidateProps = MatchingProperties.fromRawData(
                candidate.latitude,
                candidate.longitude,
                candidate.preferredDistanceKm,
                candidate.hobbies,
                candidate.musicPreferences,
                candidate.foodPreferences,
                candidate.interests,
                candidate.lookingFor
        );
        
        // Calculate individual category scores
        double locationScore = calculateLocationScore(currentUser, candidateProps);
        double hobbiesScore = currentUser.calculateSetSimilarity(
                candidateProps.getHobbies(), 
                currentUser.getHobbies()
        );
        double musicScore = currentUser.calculateSetSimilarity(
                candidateProps.getMusicPreferences(), 
                currentUser.getMusicPreferences()
        );
        double foodScore = currentUser.calculateSetSimilarity(
                candidateProps.getFoodPreferences(), 
                currentUser.getFoodPreferences()
        );
        double interestsScore = currentUser.calculateSetSimilarity(
                candidateProps.getInterests(), 
                currentUser.getInterests()
        );
        double lookingForScore = currentUser.calculateStringMatch(
                candidateProps.getLookingFor(), 
                currentUser.getLookingFor()
        );
        
        // Calculate weighted total score
        double totalScore = (locationScore * MatchingScore.Weights.LOCATION) +
                           (hobbiesScore * MatchingScore.Weights.HOBBIES) +
                           (musicScore * MatchingScore.Weights.MUSIC) +
                           (foodScore * MatchingScore.Weights.FOOD) +
                           (interestsScore * MatchingScore.Weights.INTERESTS) +
                           (lookingForScore * MatchingScore.Weights.LOOKING_FOR);
        
        return MatchingScore.builder()
                .candidateUserId(candidate.userId)
                .totalScore(totalScore)
                .locationScore(locationScore)
                .hobbiesScore(hobbiesScore)
                .musicScore(musicScore)
                .foodScore(foodScore)
                .interestsScore(interestsScore)
                .lookingForScore(lookingForScore)
                .build();
    }
    
    /**
     * Calculates location-based score using distance calculation.
     * Uses Haversine formula to calculate distance and scores based on preferred distance.
     */
    private double calculateLocationScore(MatchingProperties currentUser,
                                           MatchingProperties candidateProps) {
        return currentUser.calculateDistanceScore(candidateProps);
    }
    
    /**
     * Inner class to hold candidate with its calculated score.
     */
    private static class ScoredCandidate {
        private final RecommendationCandidate candidate;
        private final double score;
        
        public ScoredCandidate(RecommendationCandidate candidate, MatchingScore score) {
            this.candidate = candidate;
            this.score = score.getTotalScore();
        }
        
        public RecommendationCandidate getCandidate() {
            return candidate;
        }
        
        public double getScore() {
            return score;
        }
    }
}
