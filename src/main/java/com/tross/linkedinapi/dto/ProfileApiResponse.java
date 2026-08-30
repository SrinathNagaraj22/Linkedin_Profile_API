package com.tross.linkedinapi.dto;

public class ProfileApiResponse {

    private ProfileResponse profile;
    private MetadataDto metadata;

    public ProfileApiResponse() {
    }

    public ProfileApiResponse(ProfileResponse profile, MetadataDto metadata) {
        this.profile = profile;
        this.metadata = metadata;
    }

    public ProfileResponse getProfile() {
        return profile;
    }

    public void setProfile(ProfileResponse profile) {
        this.profile = profile;
    }

    public MetadataDto getMetadata() {
        return metadata;
    }

    public void setMetadata(MetadataDto metadata) {
        this.metadata = metadata;
    }
}
