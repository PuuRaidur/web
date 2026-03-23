package com.matchme.bio.dto;

// Outgoing payload for /me/bio and /users/{id}/bio
public class BioResponse {
    public Long userId;
    public String hobbies;
    public String musicPreferences;
    public String foodPreferences;
    public String interests;
    public String lookingFor;

    public BioResponse(Long userId, String hobbies, String musicPreferences,
                       String foodPreferences, String interests, String lookingFor) {
        this.userId = userId;
        this.hobbies = hobbies;
        this.musicPreferences = musicPreferences;
        this.foodPreferences = foodPreferences;
        this.interests = interests;
        this.lookingFor = lookingFor;
    }
}
