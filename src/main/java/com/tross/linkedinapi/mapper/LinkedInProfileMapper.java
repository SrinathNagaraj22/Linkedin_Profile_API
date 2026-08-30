package com.tross.linkedinapi.mapper;

import com.tross.linkedinapi.client.dto.RawLinkedInProfile;
import com.tross.linkedinapi.dto.CertificationDto;
import com.tross.linkedinapi.dto.EducationDto;
import com.tross.linkedinapi.dto.ExperienceDto;
import com.tross.linkedinapi.dto.LocationDto;
import com.tross.linkedinapi.dto.ProfileResponse;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * Converts LinkedIn's raw (internal) profile shape into our own stable
 * public ProfileResponse DTO. This is the only class that needs to change
 * if LinkedIn's response structure changes — the rest of the app depends
 * only on ProfileResponse.
 */
@Component
public class LinkedInProfileMapper {

    public ProfileResponse toProfileResponse(RawLinkedInProfile raw) {
        Objects.requireNonNull(raw, "raw profile must not be null");

        String fullName = joinNames(raw.getFirstName(), raw.getLastName());
        LocationDto location = new LocationDto(raw.getCity(), raw.getCountry());

        List<ExperienceDto> experience = mapList(raw.getExperience(), this::toExperienceDto);
        List<EducationDto> education = mapList(raw.getEducation(), this::toEducationDto);
        List<CertificationDto> certifications = mapList(raw.getCertifications(), this::toCertificationDto);

        List<String> skills = raw.getSkills() != null ? raw.getSkills() : Collections.emptyList();
        List<String> languages = raw.getLanguages() != null ? raw.getLanguages() : Collections.emptyList();

        return new ProfileResponse(
                fullName,
                raw.getHeadline(),
                location,
                raw.getAbout(),
                raw.getProfileImageUrl(),
                experience,
                education,
                skills,
                certifications,
                languages
        );
    }

    private ExperienceDto toExperienceDto(RawLinkedInProfile.RawExperience e) {
        return new ExperienceDto(
                e.getTitle(), e.getCompanyName(), e.getLocation(),
                e.getStartDate(), e.getEndDate(), e.getDescription());
    }

    private EducationDto toEducationDto(RawLinkedInProfile.RawEducation e) {
        return new EducationDto(
                e.getSchoolName(), e.getDegree(), e.getFieldOfStudy(),
                e.getStartDate(), e.getEndDate());
    }

    private CertificationDto toCertificationDto(RawLinkedInProfile.RawCertification c) {
        return new CertificationDto(c.getName(), c.getIssuingOrganization(), c.getIssueDate());
    }

    private String joinNames(String first, String last) {
        if (first == null && last == null) {
            return null;
        }
        return ((first != null ? first : "") + " " + (last != null ? last : "")).trim();
    }

    private <S, T> List<T> mapList(List<S> source, Function<S, T> mapperFn) {
        if (source == null) {
            return Collections.emptyList();
        }
        return source.stream().map(mapperFn).toList();
    }
}
