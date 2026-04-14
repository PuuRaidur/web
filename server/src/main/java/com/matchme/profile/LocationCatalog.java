package com.matchme.profile;

import java.util.Map;

public final class LocationCatalog {

    private static final int DEFAULT_PREFERRED_DISTANCE_KM = 50;
    private static final int MAX_PREFERRED_DISTANCE_KM = 200;

    private static final Map<String, double[]> CITY_COORDINATES = Map.of(
            "tallinn", new double[]{59.4370, 24.7536},
            "tartu", new double[]{58.3776, 26.7290},
            "riga", new double[]{56.9496, 24.1052},
            "helsinki", new double[]{60.1699, 24.9384},
            "vilnius", new double[]{54.6872, 25.2797},
            "oslo", new double[]{59.9139, 10.7522},
            "tokyo", new double[]{35.6762, 139.6503}
    );

    private LocationCatalog() {
    }

    public static String normalize(String location) {
        if (location == null) {
            return null;
        }
        String normalized = location.trim().toLowerCase();
        return normalized.isEmpty() ? null : normalized;
    }

    public static boolean isKnownCity(String location) {
        String normalized = normalize(location);
        return normalized != null && CITY_COORDINATES.containsKey(normalized);
    }

    public static double[] coordinatesFor(String location) {
        String normalized = normalize(location);
        if (normalized == null) {
            return null;
        }
        return CITY_COORDINATES.get(normalized);
    }

    public static int clampPreferredDistance(Integer preferredDistanceKm) {
        int value = preferredDistanceKm != null ? preferredDistanceKm : DEFAULT_PREFERRED_DISTANCE_KM;
        if (value < 1) {
            value = 1;
        }
        return Math.min(value, MAX_PREFERRED_DISTANCE_KM);
    }
}
