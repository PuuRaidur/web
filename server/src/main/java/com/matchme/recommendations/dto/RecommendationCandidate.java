package com.matchme.recommendations.dto;

// hold all data needed for scoring a candidate
public class RecommendationCandidate {
    public Long userId;
    public String location;

    public String hobbies;
    public String musicPreferences;
    public String foodPreferences;
    public String interests;
    public String lookingFor;

    public RecommendationCandidate(Long userId, String location,
                                   String hobbies, String musicPreferences,
                                   String foodPreferences, String interests,
                                   String lookingFor) {
        this.userId = userId;
        this.location = location;
        this.hobbies = hobbies;
        this.musicPreferences = musicPreferences;
        this.foodPreferences = foodPreferences;
        this.interests = interests;
        this.lookingFor = lookingFor;

    }

}
