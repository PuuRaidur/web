package com.matchme.recommendations.dto;

import java.util.List;

public class RecommendationsResponse {
    public List<Long> ids;

    public RecommendationsResponse(List<Long> ids) {
        this.ids = ids;
    }
}
