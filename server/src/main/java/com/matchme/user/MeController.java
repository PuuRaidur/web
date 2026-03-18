package com.matchme.user;

import com.matchme.user.dto.MeResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MeController {

    private final UserRepository userRepository;

    public MeController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/me")
    // Authentication gives us the userId (set by our JWT filter).
    public ResponseEntity<MeResponse> getMe(Authentication authentication) {
        // auth principal is userId set by JwtAuthFilter
        Long userId = (Long) authentication.getPrincipal();

        return userRepository.findById(userId)
                // fetch the user to confirm existence.
                .map(user -> {
                    // Profile not implemented yet, so name and profileImageUrl are null for now
                    return ResponseEntity.ok(new MeResponse(user.getId(),null, null   ));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
