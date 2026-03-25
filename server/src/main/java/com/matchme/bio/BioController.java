package com.matchme.bio;

import com.matchme.bio.dto.BioRequest;
import com.matchme.bio.dto.BioResponse;
import com.matchme.user.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
public class BioController {

    private final BioRepository bioRepository;
    private final UserRepository userRepository;

    public BioController(BioRepository bioRepository, UserRepository userRepository) {
        this.bioRepository = bioRepository;
        this.userRepository = userRepository;
    }

    // Get bio data for a specific user (public)
    @GetMapping("/users/{id}/bio")
    public ResponseEntity<BioResponse> getUserBio(@PathVariable Long id) {
        // Verify the user exists
        if (userRepository.findById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return bioRepository.findByUserId(id)
                .map(bio -> ResponseEntity.ok(toResponse(bio)))
                .orElse(ResponseEntity.notFound().build());
    }

    // Get current user's bio
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
            @Valid @RequestBody BioRequest request
    ) {
        Long userId = (Long) authentication.getPrincipal();

        var user = userRepository.findById(userId).orElse(null);
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
        bio.setInterests(request.interests);
        bio.setHobbies(request.hobbies);
        bio.setMusicTaste(request.musicTaste);
        bio.setAge(request.age);
        bio.setOccupation(request.occupation);
        bio.setCompany(request.company);
        bio.setEducation(request.education);
        bio.setRelationshipStatus(request.relationshipStatus);
        bio.setUpdatedAt(java.time.Instant.now());

        Bio saved = bioRepository.save(bio);
        return ResponseEntity.ok(toResponse(saved));
    }

    private BioResponse toResponse(Bio bio) {
        return new BioResponse(
                bio.getUser().getId(),
                bio.getInterests(),
                bio.getHobbies(),
                bio.getMusicTaste(),
                bio.getAge(),
                bio.getOccupation(),
                bio.getCompany(),
                bio.getEducation(),
                bio.getRelationshipStatus()
        );
    }
}
