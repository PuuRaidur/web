package com.matchme.bio.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;

public class BioRequest {
    public List<String> interests;
    public List<String> hobbies;
    public List<String> musicTaste;

    @Min(18)
    @Max(100)
    public Integer age;

    public String occupation;
    public String company;
    public String education;
    public String relationshipStatus;
}
