package com.matchme.recommendations;

import com.matchme.recommendations.dto.RecommendationsResponse;
import com.matchme.user.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class RecommendationsController {

    private final UserRepository userRepository;

    public RecommendationsController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Get up to 10 recommended user IDs (excluding current user)
    @GetMapping("/recommendations")
    public ResponseEntity<RecommendationsResponse> getRecommendations(Authentication authentication) {
        Long currentUserId = (Long) authentication.getPrincipal();

        // Phase 1: Simple implementation - return up to 10 user IDs, excluding current user
        // This will be enhanced in later phases with scoring algorithm
        List<Long> recommendedIds = userRepository.findAllByIdNot(currentUserId)
                .stream()
                .limit(10)
                .map(user -> user.getId())
                .collect(Collectors.toList());

        return ResponseEntity.ok(new RecommendationsResponse(recommendedIds));
    }
}
