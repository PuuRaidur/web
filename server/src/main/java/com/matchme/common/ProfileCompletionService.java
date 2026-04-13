package com.matchme.common;

import com.matchme.bio.BioRepository;
import com.matchme.profile.ProfileRepository;
import org.springframework.stereotype.Service;

@Service
public class ProfileCompletionService {

    private final ProfileRepository profileRepository;
    private final BioRepository bioRepository;

    public ProfileCompletionService(ProfileRepository profileRepository, BioRepository bioRepository) {
        this.profileRepository = profileRepository;
        this.bioRepository = bioRepository;
    }

    // Returns true only if profile and bio exist and required fields are filled
    public boolean isProfileComplete(Long userId) {
        var profileOpt = profileRepository.findByUserId(userId);
        var bioOpt = bioRepository.findByUserId(userId);

        if (profileOpt.isEmpty() || bioOpt.isEmpty()) {
            return false;
        }

        var profile = profileOpt.get();
        var bio = bioOpt.get();

        // profile must have required fields
        if (isBlank(profile.getDisplayName())) return false;
        if (isBlank(profile.getAboutMe())) return false;
        if (isBlank(profile.getLocation())) return false;
        if (profile.getPreferredDistanceKm() == null || profile.getPreferredDistanceKm() <= 0) return false;

        // bio must have all 5 required fields
        if (isBlank(bio.getHobbies())) return false;
        if (isBlank(bio.getMusicPreferences())) return false;
        if (isBlank(bio.getFoodPreferences())) return false;
        if (isBlank(bio.getInterests())) return false;
        if (isBlank(bio.getLookingFor())) return false;

        return true;
    }

    // throws if user profile/bio is incomplete (use this in controllers)
    public void requireComplete(Long userId) {
        if (!isProfileComplete(userId)) {
            throw new ProfileIncompleteException("Profile incomplete");
        }
    }

    // helper to check null/empty/whitespace
    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

}
