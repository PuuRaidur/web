package com.matchme.user;

import com.matchme.common.RelationshipService;
import com.matchme.profile.Profile;
import com.matchme.profile.ProfileRepository;
import com.matchme.user.dto.UserSummaryResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final RelationshipService relationshipService;

    public UserController(UserRepository userRepository,
                          ProfileRepository profileRepository,
                          RelationshipService relationshipService) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.relationshipService = relationshipService;
    }

    // public user summary: id + display name + profile picture
    @GetMapping("/{id}")
    public ResponseEntity<UserSummaryResponse> getUser(@PathVariable Long id,
                                                       Authentication authentication) {
        // verify the user exists
        if (userRepository.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Long viewerId = (Long) authentication.getPrincipal();
        if (!relationshipService.canViewProfile(viewerId, id)) {
            return ResponseEntity.notFound().build();
        }

        // profile may or may not exist yet
        Profile profile = profileRepository.findByUserId(id).orElse(null);
        String name = (profile != null) ? profile.getDisplayName() : null;
        String profilePictureUrl = (profile != null) ? profile.getProfilePictureUrl() : null;

        return ResponseEntity.ok(new UserSummaryResponse(id, name, profilePictureUrl));
    }

    // public profile info fo a given user id
    @GetMapping("/{id}/profile")
    public ResponseEntity<com.matchme.profile.dto.ProfileResponse> getUserProfile(@PathVariable Long id,
                                                                                  Authentication authentication) {
        // verify the user exists
        if (userRepository.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Long viewerId = (Long) authentication.getPrincipal();
        if (!relationshipService.canViewProfile(viewerId, id)) {
            return ResponseEntity.notFound().build();
        }

        return profileRepository.findByUserId(id)
                .map(profile -> ResponseEntity.ok(
                        new com.matchme.profile.dto.ProfileResponse(
                                profile.getUser().getId(),
                                profile.getDisplayName(),
                                profile.getAboutMe(),
                                profile.getProfilePictureUrl(),
                                profile.getLocation()
                        )
                ))
                .orElse(ResponseEntity.notFound().build());
    }
}
