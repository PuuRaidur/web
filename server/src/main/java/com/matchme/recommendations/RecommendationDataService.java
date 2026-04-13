package com.matchme.recommendations;

import com.matchme.bio.BioRepository;
import com.matchme.profile.ProfileRepository;
import com.matchme.profile.Profile;
import com.matchme.recommendations.dto.RecommendationCandidate;
import com.matchme.recommendations.matching.MatchingService;
import com.matchme.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecommendationDataService {

    private final UserRepository userRepository;
    private final BioRepository bioRepository;
    private final ProfileRepository profileRepository;
    private final MatchingService matchingService;
    private final DismissedRecommendationRepository dismissedRecommendationRepository;

    public RecommendationDataService(UserRepository userRepository,
                                     BioRepository bioRepository,
                                     ProfileRepository profileRepository,
                                     MatchingService matchingService,
                                     DismissedRecommendationRepository dismissedRecommendationRepository) {

        this.userRepository = userRepository;
        this.bioRepository = bioRepository;
        this.profileRepository = profileRepository;
        this.matchingService = matchingService;
        this.dismissedRecommendationRepository = dismissedRecommendationRepository;
    }

    // load canditates with profile + bio data (excluding current user and dismissed users)
    public List<RecommendationCandidate> loadCanditates(Long currentUserId) {
        // Get list of dismissed user IDs for this user (who they dismissed)
        List<Long> dismissedUserIds = dismissedRecommendationRepository.findDismissedUserIdsByUserId(currentUserId);
        // Get list of users who dismissed the current user (so we don't recommend them)
        List<Long> dismissedByUserIds = dismissedRecommendationRepository.findUserIdsWhoDismissed(currentUserId);

        if (dismissedUserIds == null || dismissedUserIds.isEmpty()) {
            dismissedUserIds = List.of();
        }
        if (dismissedByUserIds == null || dismissedByUserIds.isEmpty()) {
            dismissedByUserIds = List.of();
        }

        List<Long> excludedIds = new java.util.ArrayList<>(dismissedUserIds);
        excludedIds.addAll(dismissedByUserIds);

        List<com.matchme.user.User> candidates = excludedIds.isEmpty()
                ? userRepository.findAllByIdNot(currentUserId)
                : userRepository.findAllByIdNotAndIdNotIn(currentUserId, excludedIds);

        return candidates
                .stream() // Starts streaming the list so we can transform it.
                .map(user -> {
                    var profile = profileRepository.findByUserId(user.getId()).orElse(null);
                    var bio = bioRepository.findById(user.getId()).orElse(null);

                    // if profile or bio is missing, skip candidate for now
                    if (profile == null || bio == null) {
                        return null;
                    }

                    return new RecommendationCandidate(
                            user.getId(),
                            profile.getLocation(),
                            profile.getLatitude(),
                            profile.getLongitude(),
                            profile.getPreferredDistanceKm(),
                            bio.getHobbies(),
                            bio.getMusicPreferences(),
                            bio.getFoodPreferences(),
                            bio.getInterests(),
                            bio.getLookingFor()
                    );
                })
                .filter(candidate -> candidate != null) // Drops the users that didn't have profile or bio.
                .collect(Collectors.toList()); // turns the stream back into a List.
    }

    /**
     * Loads candidates and returns them sorted by matching score.
     */
    public List<RecommendationCandidate> getRecommendedCandidates(Long currentUserId) {
        List<RecommendationCandidate> allCandidates = loadCanditates(currentUserId);
        Profile currentProfile = profileRepository.findByUserId(currentUserId).orElse(null);
        List<RecommendationCandidate> filteredCandidates = filterByLocation(currentProfile, allCandidates);

        RecommendationCandidate currentUser = getCurrentUserCandidate(currentUserId);
        if (currentUser == null) {
            return filteredCandidates.stream().limit(10).collect(Collectors.toList());
        }

        return matchingService.findBestMatches(currentUser, filteredCandidates)
                .stream()
                .limit(10)
                .collect(Collectors.toList());
    }

    public boolean isCandidate(Long currentUserId, Long targetUserId) {
        return loadCanditates(currentUserId)
                .stream()
                .anyMatch(candidate -> candidate.userId.equals(targetUserId));
    }

    /**
     * Gets the current user's candidate data for matching.
     */
    private RecommendationCandidate getCurrentUserCandidate(Long currentUserId) {
        var user = userRepository.findById(currentUserId).orElse(null);
        if (user == null) {
            return null;
        }

        var profile = profileRepository.findByUserId(currentUserId).orElse(null);
        var bio = bioRepository.findById(currentUserId).orElse(null);

        if (profile == null || bio == null) {
            return null;
        }

        return new RecommendationCandidate(
                currentUserId,
                profile.getLocation(),
                profile.getLatitude(),
                profile.getLongitude(),
                profile.getPreferredDistanceKm(),
                bio.getHobbies(),
                bio.getMusicPreferences(),
                bio.getFoodPreferences(),
                bio.getInterests(),
                bio.getLookingFor()
        );
    }

    private List<RecommendationCandidate> filterByLocation(Profile currentProfile,
                                                           List<RecommendationCandidate> candidates) {
        if (currentProfile == null) {
            return candidates;
        }

        String location = currentProfile.getLocation();
        Double lat = currentProfile.getLatitude();
        Double lon = currentProfile.getLongitude();
        Integer preferredDistance = currentProfile.getPreferredDistanceKm();
        if (preferredDistance == null) {
            preferredDistance = 50;
        }

        if (lat != null && lon != null) {
            double finalPreferredDistance = preferredDistance;
            return candidates.stream()
                    .filter(candidate -> {
                        if (candidate.latitude != null && candidate.longitude != null) {
                            double distance = calculateHaversineDistance(lat, lon, candidate.latitude, candidate.longitude);
                            return distance <= finalPreferredDistance;
                        }
                        // If candidate coordinates are missing, keep candidate and let matching score handle ranking.
                        return true;
                    })
                    .collect(Collectors.toList());
        }

        // Without current user coordinates we cannot do reliable distance filtering.
        return candidates;
    }

    private double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0;

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
