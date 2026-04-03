package com.matchme.recommendations;

import com.matchme.recommendations.dto.RecommendationsResponse;
import com.matchme.recommendations.RecommendationDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class RecommendationsController {

    private final RecommendationDataService recommendationDataService;
    private final DismissedRecommendationRepository dismissedRecommendationRepository;

    public RecommendationsController(RecommendationDataService recommendationDataService,
                                     DismissedRecommendationRepository dismissedRecommendationRepository) {
        this.recommendationDataService = recommendationDataService;
        this.dismissedRecommendationRepository = dismissedRecommendationRepository;
    }

    // Get up to 10 recommended user IDs (excluding current user and dismissed users)
    @GetMapping("/recommendations")
    public ResponseEntity<RecommendationsResponse> getRecommendations(Authentication authentication) {
        Long currentUserId = (Long) authentication.getPrincipal();

        // Get candidates sorted by matching score
        List<Long> recommendedIds = recommendationDataService.getRecommendedCandidates(currentUserId)
                .stream()
                .map(candidate -> candidate.userId)
                .collect(Collectors.toList());

        return ResponseEntity.ok(new RecommendationsResponse(recommendedIds));
    }

    // Dismiss a recommendation so it won't appear again
    @PostMapping("/recommendations/dismiss/{userId}")
    public ResponseEntity<Void> dismissRecommendation(Authentication authentication,
                                                       @PathVariable Long userId) {
        Long currentUserId = (Long) authentication.getPrincipal();

        // Prevent duplicate dismissals
        if (!dismissedRecommendationRepository.existsByUserIdAndDismissedUserId(currentUserId, userId)) {
            DismissedRecommendation dismissal = new DismissedRecommendation();
            dismissal.setUserId(currentUserId);
            dismissal.setDismissedUserId(userId);
            dismissedRecommendationRepository.save(dismissal);
        }

        return ResponseEntity.ok().build();
    }
}
