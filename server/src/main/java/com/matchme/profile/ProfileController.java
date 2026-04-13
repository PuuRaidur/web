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

// for profile picture upload endpoint
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.Map;


@RestController
public class ProfileController {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    public ProfileController(ProfileRepository profileRepository, UserRepository userRepository) {
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
    }

    private static final Map<String, double[]> CITY_COORDINATES = Map.of(
            "tallinn", new double[]{59.4370, 24.7536},
            "tartu", new double[]{58.3776, 26.7290},
            "riga", new double[]{56.9496, 24.1052},
            "helsinki", new double[]{60.1699, 24.9384},
            "vilnius", new double[]{54.6872, 25.2797},
            "oslo", new double[]{59.9139, 10.7522},
            "tokyo", new double[]{35.6762, 139.6503}
    );

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
        profile.setPreferredDistanceKm(request.preferredDistanceKm);
        if (request.latitude != null && request.longitude != null) {
            profile.setLatitude(request.latitude);
            profile.setLongitude(request.longitude);
        } else if (request.location != null && !request.location.isBlank()) {
            double[] coords = CITY_COORDINATES.get(request.location.trim().toLowerCase());
            if (coords != null) {
                profile.setLatitude(coords[0]);
                profile.setLongitude(coords[1]);
            }
        }
        profile.setUpdatedAt(java.time.Instant.now());

        Profile saved = profileRepository.save(profile);
        return ResponseEntity.ok(toResponse(saved));
    }

    private ProfileResponse toResponse(Profile profile) {
        return new ProfileResponse(
                profile.getUser().getId(),
                profile.getUser().getEmail(),
                profile.getDisplayName(),
                profile.getAboutMe(),
                profile.getProfilePictureUrl(),
                profile.getLocation(),
                profile.getPreferredDistanceKm(),
                profile.getLatitude(),
                profile.getLongitude()
        );
    }

    // upload profile picture and update profilePictureUrl
    @PostMapping("/me/profile/picture")
    public ResponseEntity<ProfileResponse> uploadProfilePicture(
            Authentication authentication,
            @RequestParam("file") MultipartFile file
        ) throws Exception {
        Long userId = (Long) authentication.getPrincipal(); // Uses the logged‑in user ID from the security context.

        // find user and profile
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        // Finds the profile; if missing, creates a new one with a placeholder display name.
        Profile profile = profileRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Profile p = new Profile();
                    p.setUser(user);
                    p.setDisplayName("User " + userId); // placeholder 8if not set yet
                    return p;
                });

        // generate unique filename
        String ext = "";
        String original = file.getOriginalFilename();
        if (original != null && original.contains(".")) {
            ext = original.substring(original.lastIndexOf("."));
        }
        String filename = UUID.randomUUID() + ext; // Uses a random UUID to avoid filename collisions.


        // save to disk
        Path uploadDir = Paths.get("uploads");
        Files.createDirectories(uploadDir);
        Path uploadPath = uploadDir.resolve(filename);
        Files.copy(file.getInputStream(), uploadPath);

        // update profile picture URL
        String url = "/uploads/" + filename;
        profile.setProfilePictureUrl(url);
        profile.setUpdatedAt(java.time.Instant.now());
        Profile saved = profileRepository.save(profile);
        return ResponseEntity.ok(toResponse(saved));
    }

    // remove profile picture and clear URL
    @DeleteMapping("/me/profile/picture")
    public ResponseEntity<ProfileResponse> deleteProfilePicture(Authentication authentication) throws Exception {
        Long userId = (Long) authentication.getPrincipal();

        Profile  profile = profileRepository.findByUserId(userId)
                .orElse(null);
        if (profile == null) {
            return ResponseEntity.notFound().build();
        }

        // if a picture exists, delete the file
        if (profile.getProfilePictureUrl() != null) {
            String filename = profile.getProfilePictureUrl().replace("/uploads/", "");
            Path uploadPath = Paths.get("uploads", filename);
            Files.deleteIfExists(uploadPath);
        }

        // clear the URl and update profile
        profile.setProfilePictureUrl(null);
        profile.setUpdatedAt(java.time.Instant.now());
        Profile saved = profileRepository.save(profile);
        return ResponseEntity.ok(toResponse(saved));
    }

}
