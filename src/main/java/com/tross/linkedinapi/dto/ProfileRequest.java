package com.tross.linkedinapi.dto;

import jakarta.validation.constraints.NotBlank;

public class ProfileRequest {

    @NotBlank(message = "profileUrl must not be blank")
    private String profileUrl;

    public ProfileRequest() {
    }

    public ProfileRequest(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }
}
