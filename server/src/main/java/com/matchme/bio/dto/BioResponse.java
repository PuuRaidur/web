package com.matchme.bio.dto;

import java.util.List;

public class BioResponse {
    public Long userId;
    public List<String> interests;
    public List<String> hobbies;
    public List<String> musicTaste;
    public Integer age;
    public String occupation;
    public String company;
    public String education;
    public String relationshipStatus;

    public BioResponse(Long userId, List<String> interests, List<String> hobbies,
                       List<String> musicTaste, Integer age, String occupation,
                       String company, String education, String relationshipStatus) {
        this.userId = userId;
        this.interests = interests;
        this.hobbies = hobbies;
        this.musicTaste = musicTaste;
        this.age = age;
        this.occupation = occupation;
        this.company = company;
        this.education = education;
        this.relationshipStatus = relationshipStatus;
    }
}
