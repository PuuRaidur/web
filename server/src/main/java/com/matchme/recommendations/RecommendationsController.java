package com.matchme.recommendations;

import com.matchme.recommendations.dto.RecommendationsResponse;
import com.matchme.recommendations.RecommendationDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class RecommendationsController {

    private final RecommendationDataService recommendationDataService;

    public RecommendationsController(RecommendationDataService recommendationDataService) {
        this.recommendationDataService = recommendationDataService;
    }

    // Get up to 10 recommended user IDs (excluding current user)
    @GetMapping("/recommendations")
    public ResponseEntity<RecommendationsResponse> getRecommendations(Authentication authentication) {
        Long currentUserId = (Long) authentication.getPrincipal();

        // Phase 1: Simple implementation - return up to 10 user IDs, excluding current user
        // This will be enhanced in later phases with scoring algorithm
        List<Long> recommendedIds = recommendationDataService.loadCanditates(currentUserId)
                .stream()
                .limit(10)
                .map(candidate -> candidate.userId)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new RecommendationsResponse(recommendedIds));
    }
}
