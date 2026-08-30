package com.tross.linkedinapi.client.dto;

import java.util.List;

/**
 * Intermediate representation of whatever data can be extracted from
 * LinkedIn's raw endpoint response, before it is converted into our
 * stable public ProfileResponse DTO by LinkedInProfileMapper.
 *
 * The LinkedInClient implementation is responsible for populating this
 * from LinkedIn's actual (normalized, nested) JSON structure. Fields
 * here are intentionally flat and use simple types so the mapper stays
 * trivial and never needs to know about LinkedIn's internal shape.
 */
public class RawLinkedInProfile {

    private String publicIdentifier;
    private String firstName;
    private String lastName;
    private String headline;
    private String city;
    private String country;
    private String about;
    private String profileImageUrl;
    private List<RawExperience> experience;
    private List<RawEducation> education;
    private List<String> skills;
    private List<RawCertification> certifications;
    private List<String> languages;

    public RawLinkedInProfile() {
    }

    public String getPublicIdentifier() {
        return publicIdentifier;
    }

    public void setPublicIdentifier(String publicIdentifier) {
        this.publicIdentifier = publicIdentifier;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public List<RawExperience> getExperience() {
        return experience;
    }

    public void setExperience(List<RawExperience> experience) {
        this.experience = experience;
    }

    public List<RawEducation> getEducation() {
        return education;
    }

    public void setEducation(List<RawEducation> education) {
        this.education = education;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public List<RawCertification> getCertifications() {
        return certifications;
    }

    public void setCertifications(List<RawCertification> certifications) {
        this.certifications = certifications;
    }

    public List<String> getLanguages() {
        return languages;
    }

    public void setLanguages(List<String> languages) {
        this.languages = languages;
    }

    public static class RawExperience {
        private String title;
        private String companyName;
        private String location;
        private String startDate;
        private String endDate;
        private String description;

        public RawExperience() {
        }

        public RawExperience(String title, String companyName, String location,
                              String startDate, String endDate, String description) {
            this.title = title;
            this.companyName = companyName;
            this.location = location;
            this.startDate = startDate;
            this.endDate = endDate;
            this.description = description;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public static class RawEducation {
        private String schoolName;
        private String degree;
        private String fieldOfStudy;
        private String startDate;
        private String endDate;

        public RawEducation() {
        }

        public RawEducation(String schoolName, String degree, String fieldOfStudy,
                             String startDate, String endDate) {
            this.schoolName = schoolName;
            this.degree = degree;
            this.fieldOfStudy = fieldOfStudy;
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public String getSchoolName() {
            return schoolName;
        }

        public void setSchoolName(String schoolName) {
            this.schoolName = schoolName;
        }

        public String getDegree() {
            return degree;
        }

        public void setDegree(String degree) {
            this.degree = degree;
        }

        public String getFieldOfStudy() {
            return fieldOfStudy;
        }

        public void setFieldOfStudy(String fieldOfStudy) {
            this.fieldOfStudy = fieldOfStudy;
        }

        public String getStartDate() {
            return startDate;
        }

        public void setStartDate(String startDate) {
            this.startDate = startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }
    }

    public static class RawCertification {
        private String name;
        private String issuingOrganization;
        private String issueDate;

        public RawCertification() {
        }

        public RawCertification(String name, String issuingOrganization, String issueDate) {
            this.name = name;
            this.issuingOrganization = issuingOrganization;
            this.issueDate = issueDate;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getIssuingOrganization() {
            return issuingOrganization;
        }

        public void setIssuingOrganization(String issuingOrganization) {
            this.issuingOrganization = issuingOrganization;
        }

        public String getIssueDate() {
            return issueDate;
        }

        public void setIssueDate(String issueDate) {
            this.issueDate = issueDate;
        }
    }
}
