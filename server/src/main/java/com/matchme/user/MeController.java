package com.matchme.user;

import com.matchme.profile.Profile;
import com.matchme.profile.ProfileRepository;
import com.matchme.user.dto.MeResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MeController {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    public MeController(UserRepository userRepository, ProfileRepository profileRepository) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
    }

    @GetMapping("/me")
    public ResponseEntity<MeResponse> getMe(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();

        return userRepository.findById(userId)
                .map(user -> {
                    Profile profile = profileRepository.findByUserId(userId).orElse(null);
                    String name = (profile != null) ? profile.getDisplayName() : null;
                    String profilePictureUrl = (profile != null) ? profile.getProfilePictureUrl() : null;
                    return ResponseEntity.ok(new MeResponse(user.getId(), name, profilePictureUrl));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
