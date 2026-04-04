package com.matchme.common;

import com.matchme.connection.ConnectionRepository;
import com.matchme.connection.ConnectionRequestRepository;
import com.matchme.recommendations.RecommendationDataService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RelationshipService {

    private final ConnectionRepository connectionRepository;
    private final ConnectionRequestRepository connectionRequestRepository;
    private final RecommendationDataService recommendationDataService;

    public RelationshipService(ConnectionRepository connectionRepository,
                               ConnectionRequestRepository connectionRequestRepository,
                               RecommendationDataService recommendationDataService) {
        this.connectionRepository = connectionRepository;
        this.connectionRequestRepository = connectionRequestRepository;
        this.recommendationDataService = recommendationDataService;
    }

    public boolean canViewProfile(Long viewerId, Long targetUserId) {
        if (viewerId.equals(targetUserId)) {
            return true;
        }

        boolean connected = connectionRepository.existsBetweenUsers(viewerId, targetUserId);
        if (connected) {
            return true;
        }

        boolean hasRequest = connectionRequestRepository
                .findBySenderIdAndReceiverId(viewerId, targetUserId)
                .isPresent() ||
                connectionRequestRepository
                        .findBySenderIdAndReceiverId(targetUserId, viewerId)
                        .isPresent();
        if (hasRequest) {
            return true;
        }

        return recommendationDataService.isCandidate(viewerId, targetUserId);
    }
}
