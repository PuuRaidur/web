package com.matchme.profile;

import com.matchme.profile.dto.ProfileRequest;
import com.matchme.profile.dto.ProfileResponse;
import com.matchme.user.User;
import com.matchme.user.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProfileController {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    public ProfileController(ProfileRepository profileRepository, UserRepository userRepository) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
    }

    // Get current user's profile
    @GetMapping("/me/profile")
    public ResponseEntity<ProfileResponse> getProfile(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();

        return profileRepository.findByUserId(userId)
                .map(profile -> ResponseEntity.ok(toResponse(profile)))
                .orElse(ResponseEntity.notFound().build());
    }

    // create or update current user's profile
    @PutMapping("/me/profile")
    public ResponseEntity<ProfileResponse> upsertMyProfile(
            Authentication authentication,
            @Valid @RequestBody ProfileRequest request
    )  {
        Long userId = (Long) authentication.getPrincipal();

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        Profile profile = profileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Profile p = new Profile();
                    p.setUser(user);
                    return p;
                });

        // update fields
        profile.setDisplayName(request.displayName);
        profile.setAboutMe(request.aboutMe);
        profile.setProfilePictureUrl(request.profilePictureUrl);
        profile.setLocation(request.location);
        profile.setUpdatedAt(java.time.Instant.now());

        Profile saved = profileRepository.save(profile);
        return ResponseEntity.ok(toResponse(saved));
    }

    private ProfileResponse toResponse(Profile profile) {
        return new ProfileResponse(
                profile.getUser().getId(),
                profile.getDisplayName(),
                profile.getAboutMe(),
                profile.getProfilePictureUrl(),
                profile.getLocation()
        );
    }
}
