package com.matchme.recommendations.matching;

import lombok.Builder;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Normalized matching properties extracted from user data.
 * Converts comma-separated strings into sets for easier comparison.
 */
@Getter
@Builder
public class MatchingProperties {
    
    private final String location;
    private final Set<String> hobbies;
    private final Set<String> musicPreferences;
    private final Set<String> foodPreferences;
    private final Set<String> interests;
    private final String lookingFor;
    
    /**
     * Creates MatchingProperties from raw string values.
     * Assumes comma-separated values for multi-value fields.
     */
    public static MatchingProperties fromRawData(String location, 
                                                  String hobbies,
                                                  String musicPreferences,
                                                  String foodPreferences,
                                                  String interests,
                                                  String lookingFor) {
        return MatchingProperties.builder()
                .location(normalizeString(location))
                .hobbies(parseCommaSeparatedSet(hobbies))
                .musicPreferences(parseCommaSeparatedSet(musicPreferences))
                .foodPreferences(parseCommaSeparatedSet(foodPreferences))
                .interests(parseCommaSeparatedSet(interests))
                .lookingFor(normalizeString(lookingFor))
                .build();
    }
    
    private static String normalizeString(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim().toLowerCase();
    }
    
    private static Set<String> parseCommaSeparatedSet(String value) {
        if (value == null || value.trim().isEmpty()) {
            return Collections.emptySet();
        }
        
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toSet());
    }
    
    /**
     * Calculates similarity score with another MatchingProperties instance.
     * Uses Jaccard similarity for sets (intersection over union).
     */
    public double calculateSetSimilarity(Set<String> otherSet, Set<String> thisSet) {
        if (thisSet.isEmpty() && otherSet.isEmpty()) {
            return 0.0; // No data to compare
        }
        
        if (thisSet.isEmpty() || otherSet.isEmpty()) {
            return 0.0; // One side has no data
        }
        
        Set<String> intersection = new HashSet<>(thisSet);
        intersection.retainAll(otherSet);
        
        Set<String> union = new HashSet<>(thisSet);
        union.addAll(otherSet);
        
        if (union.isEmpty()) {
            return 0.0;
        }
        
        return (double) intersection.size() / union.size();
    }
    
    /**
     * Calculates exact match score for string fields.
     */
    public double calculateStringMatch(String otherValue, String thisValue) {
        if (thisValue == null || otherValue == null) {
            return 0.0;
        }
        
        return thisValue.equals(otherValue) ? 1.0 : 0.0;
    }
}
