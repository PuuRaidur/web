package com.matchme.recommendations;

import com.matchme.bio.BioRepository;
import com.matchme.profile.ProfileRepository;
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
        // Get list of dismissed user IDs for this user
        List<Long> dismissedUserIds = dismissedRecommendationRepository.findDismissedUserIdsByUserId(currentUserId);

        return userRepository.findAllByIdNotAndIdNotIn(currentUserId, dismissedUserIds)
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

        RecommendationCandidate currentUser = getCurrentUserCandidate(currentUserId);
        if (currentUser == null) {
            return allCandidates.stream().limit(10).collect(Collectors.toList());
        }

        return matchingService.findBestMatches(currentUser, allCandidates)
                .stream()
                .limit(10)
                .collect(Collectors.toList());
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
}
