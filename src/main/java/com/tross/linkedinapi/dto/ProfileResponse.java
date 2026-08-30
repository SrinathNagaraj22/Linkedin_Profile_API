package com.tross.linkedinapi.dto;

import java.util.List;
import java.util.Objects;

public class ProfileResponse {

    private String name;
    private String headline;
    private LocationDto location;
    private String about;
    private String profileImage;
    private List<ExperienceDto> experience;
    private List<EducationDto> education;
    private List<String> skills;
    private List<CertificationDto> certifications;
    private List<String> languages;

    public ProfileResponse() {
    }

    public ProfileResponse(String name, String headline, LocationDto location, String about,
                            String profileImage, List<ExperienceDto> experience,
                            List<EducationDto> education, List<String> skills,
                            List<CertificationDto> certifications, List<String> languages) {
        this.name = name;
        this.headline = headline;
        this.location = location;
        this.about = about;
        this.profileImage = profileImage;
        this.experience = experience;
        this.education = education;
        this.skills = skills;
        this.certifications = certifications;
        this.languages = languages;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public LocationDto getLocation() {
        return location;
    }

    public void setLocation(LocationDto location) {
        this.location = location;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public List<ExperienceDto> getExperience() {
        return experience;
    }

    public void setExperience(List<ExperienceDto> experience) {
        this.experience = experience;
    }

    public List<EducationDto> getEducation() {
        return education;
    }

    public void setEducation(List<EducationDto> education) {
        this.education = education;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public List<CertificationDto> getCertifications() {
        return certifications;
    }

    public void setCertifications(List<CertificationDto> certifications) {
        this.certifications = certifications;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProfileResponse that)) return false;
        return Objects.equals(name, that.name) && Objects.equals(headline, that.headline);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, headline);
    }
}
