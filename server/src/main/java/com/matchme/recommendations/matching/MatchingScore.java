package com.matchme.recommendations.matching;

import lombok.Builder;
import lombok.Getter;

/**
 * Represents the matching score between two users.
 */
@Getter
@Builder
public class MatchingScore {
    
    private final Long candidateUserId;
    private final double totalScore;
    private final double locationScore;
    private final double hobbiesScore;
    private final double musicScore;
    private final double foodScore;
    private final double interestsScore;
    private final double lookingForScore;
    
    /**
     * Weights for each matching category (can be adjusted based on business requirements)
     */
    public static class Weights {
        public static final double LOCATION = 0.15;
        public static final double HOBBIES = 0.25;
        public static final double MUSIC = 0.15;
        public static final double FOOD = 0.10;
        public static final double INTERESTS = 0.25;
        public static final double LOOKING_FOR = 0.10;
    }
}
