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

    private final Double latitude;
    private final Double longitude;
    private final Integer preferredDistanceKm;
    private final Set<String> hobbies;
    private final Set<String> musicPreferences;
    private final Set<String> foodPreferences;
    private final Set<String> interests;
    private final String lookingFor;

    /**
     * Creates MatchingProperties from raw data values.
     */
    public static MatchingProperties fromRawData(Double latitude,
                                                  Double longitude,
                                                  Integer preferredDistanceKm,
                                                  String hobbies,
                                                  String musicPreferences,
                                                  String foodPreferences,
                                                  String interests,
                                                  String lookingFor) {
        return MatchingProperties.builder()
                .latitude(latitude)
                .longitude(longitude)
                .preferredDistanceKm(preferredDistanceKm)
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

    /**
     * Calculates distance-based location score between two users.
     * Score is 1.0 if within preferred distance, decreasing linearly beyond that.
     * Returns 0.0 if coordinates are missing for either user.
     */
    public double calculateDistanceScore(MatchingProperties other) {
        if (this.latitude == null || this.longitude == null ||
            other.latitude == null || other.longitude == null) {
            return 0.0;
        }

        double actualDistance = calculateHaversineDistance(
                this.latitude, this.longitude,
                other.latitude, other.longitude
        );

        // Use the current user's preferred distance as the threshold
        Integer preferredDistance = this.preferredDistanceKm != null ?
                this.preferredDistanceKm : other.preferredDistanceKm;

        if (preferredDistance == null) {
            // Default to 50km if no preference is set
            preferredDistance = 50;
        }

        // Score calculation:
        // - 1.0 if actual distance <= preferred distance
        // - Linearly decreases from 1.0 to 0.0 as distance goes from preferred to 2x preferred
        // - 0.0 if distance >= 2x preferred distance
        if (actualDistance <= preferredDistance) {
            return 1.0;
        } else if (actualDistance >= 2.0 * preferredDistance) {
            return 0.0;
        } else {
            // Linear decrease: score = 1 - (distance - preferred) / preferred
            return 1.0 - (actualDistance - preferredDistance) / (double) preferredDistance;
        }
    }

    /**
     * Calculates the great-circle distance between two points on Earth using the Haversine formula.
     *
     * @param lat1 Latitude of point 1 in degrees
     * @param lon1 Longitude of point 1 in degrees
     * @param lat2 Latitude of point 2 in degrees
     * @param lon2 Longitude of point 2 in degrees
     * @return Distance in kilometers
     */
    private double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0; // Earth's radius in kilometers

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }
}
