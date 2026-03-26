package com.matchme.common;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CompletionDemoController {

    private final ProfileCompletionService profileCompletionService;

    public CompletionDemoController(ProfileCompletionService profileCompletionService) {
        this.profileCompletionService = profileCompletionService;
    }

    // Demo endpoint: only accessible if profile + bio are complete
    @GetMapping("/me/complete")
    public ResponseEntity<String> checkComplete(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();

        profileCompletionService.requireComplete(userId);

        return ResponseEntity.ok("Profile complete");
    }
}
