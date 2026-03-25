package com.matchme.user.dto;

public class UserSummaryResponse {
    public Long id;
    public String name;
    public String proofilePictureUrl;

    public UserSummaryResponse(Long id, String name, String proofilePictureUrl) {
        this.id = id;
        this.name = name;
        this.proofilePictureUrl = proofilePictureUrl;
    }
}
