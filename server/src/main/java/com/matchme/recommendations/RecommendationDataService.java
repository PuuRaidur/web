package com.matchme.recommendations;
import com.matchme.bio.BioRepository;
import com.matchme.profile.ProfileRepository;
import com.matchme.recommendations.dto.RecommendationCandidate;
import com.matchme.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RecommendationDataService {

    private final UserRepository userRepository;
    private final BioRepository bioRepository;
    private final ProfileRepository profileRepository;

    public RecommendationDataService(UserRepository userRepository, BioRepository bioRepository, ProfileRepository profileRepository) {

        this.userRepository = userRepository;
        this.bioRepository = bioRepository;
        this.profileRepository = profileRepository;
    }

    // load canditates with profile + bio dat (excluding current user)
    public List<RecommendationCandidate> loadCanditates(Long currentUserId) {
        return userRepository.findAllByIdNot(currentUserId) // Gets all users except the current user.

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
                            bio.getHobbies(),
                            bio.getMusicPreferences(),
                            bio.getFoodPreferences(),
                            bio.getInterests(),
                            bio.getLookingFor()
                    );
                })
                .filter(candidate -> candidate != null) // Drops the users that didn’t have profile or bio.
                .collect(Collectors.toList()); // turns the stream back into a List.
    }
}
