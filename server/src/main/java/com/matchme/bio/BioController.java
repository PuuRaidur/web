package com.matchme.bio;

import com.matchme.bio.dto.BioRequest;
import com.matchme.bio.dto.BioResponse;
import com.matchme.common.RelationshipService;
import com.matchme.user.User;
import com.matchme.user.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
public class BioController {

    private final BioRepository bioRepository;
    private final UserRepository userRepository;
    private final RelationshipService relationshipService;

    public BioController(BioRepository bioRepository,
                         UserRepository userRepository,
                         RelationshipService relationshipService) {
        this.bioRepository = bioRepository;
        this.userRepository = userRepository;
        this.relationshipService = relationshipService;
    }

    // get current user's bio
    @GetMapping("/me/bio")
    public ResponseEntity<BioResponse> getMyBio(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();

        return bioRepository.findByUserId(userId)
                .map(bio -> ResponseEntity.ok(toResponse(bio)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Create or update current user's bio
    @PutMapping("/me/bio")
    public ResponseEntity<BioResponse> upsertMyBio(
            Authentication authentication,
            @RequestBody BioRequest request
    ) {
        Long userId = (Long) authentication.getPrincipal();

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        Bio bio = bioRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Bio b = new Bio();
                    b.setUser(user);
                    return b;
                });

        // Update fields
        bio.setHobbies(request.hobbies);
        bio.setMusicPreferences(request.musicPreferences);
        bio.setFoodPreferences(request.foodPreferences);
        bio.setInterests(request.interests);
        bio.setLookingFor(request.lookingFor);
        bio.setUpdatedAt(java.time.Instant.now());

        Bio saved = bioRepository.save(bio);
        return ResponseEntity.ok(toResponse(saved));
    }

    // Public bio info for a given user id
    @GetMapping("/users/{id}/bio")
    public ResponseEntity<BioResponse> getUserBio(@PathVariable Long id,
                                                  Authentication authentication) {
        // Verify user exists
        if (userRepository.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Long viewerId = (Long) authentication.getPrincipal();
        if (!relationshipService.canViewProfile(viewerId, id)) {
            return ResponseEntity.notFound().build();
        }

        return bioRepository.findByUserId(id)
                .map(bio -> ResponseEntity.ok(toResponse(bio)))
                .orElse(ResponseEntity.notFound().build());
    }

    private BioResponse toResponse(Bio bio) {
        return new BioResponse(
                bio.getUser().getId(),
                bio.getHobbies(),
                bio.getMusicPreferences(),
                bio.getFoodPreferences(),
                bio.getInterests(),
                bio.getLookingFor()
        );
    }
}
