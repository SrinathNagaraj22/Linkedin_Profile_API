package com.tross.linkedinapi.mapper;

import com.tross.linkedinapi.client.dto.RawLinkedInProfile;
import com.tross.linkedinapi.dto.ProfileResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class LinkedInProfileMapperTest {

    private final LinkedInProfileMapper mapper = new LinkedInProfileMapper();

    @Test
    void mapsFullProfileCorrectly() {
        RawLinkedInProfile raw = new RawLinkedInProfile();
        raw.setFirstName("Jane");
        raw.setLastName("Doe");
        raw.setHeadline("Engineer");
        raw.setCity("Bangalore");
        raw.setCountry("India");
        raw.setAbout("About text");
        raw.setSkills(List.of("Java", "Spring"));
        raw.setLanguages(List.of("English"));
        raw.setExperience(List.of(
                new RawLinkedInProfile.RawExperience("SDE", "Acme", "Bangalore", "2022", "Present", "desc")));
        raw.setEducation(List.of(
                new RawLinkedInProfile.RawEducation("Example Univ", "B.Tech", "CS", "2018", "2022")));
        raw.setCertifications(List.of(
                new RawLinkedInProfile.RawCertification("Cert A", "Issuer A", "2023")));

        ProfileResponse result = mapper.toProfileResponse(raw);

        assertThat(result.getName()).isEqualTo("Jane Doe");
        assertThat(result.getHeadline()).isEqualTo("Engineer");
        assertThat(result.getLocation().getCity()).isEqualTo("Bangalore");
        assertThat(result.getSkills()).containsExactly("Java", "Spring");
        assertThat(result.getExperience()).hasSize(1);
        assertThat(result.getEducation()).hasSize(1);
        assertThat(result.getCertifications()).hasSize(1);
    }

    @Test
    void handlesMissingOptionalFieldsGracefully() {
        RawLinkedInProfile raw = new RawLinkedInProfile();
        raw.setFirstName("Jane");
        raw.setLastName(null);

        ProfileResponse result = mapper.toProfileResponse(raw);

        assertThat(result.getName()).isEqualTo("Jane");
        assertThat(result.getExperience()).isEmpty();
        assertThat(result.getEducation()).isEmpty();
        assertThat(result.getSkills()).isEmpty();
        assertThat(result.getCertifications()).isEmpty();
        assertThat(result.getLanguages()).isEmpty();
    }

    @Test
    void handlesCompletelyEmptyNameGracefully() {
        RawLinkedInProfile raw = new RawLinkedInProfile();

        ProfileResponse result = mapper.toProfileResponse(raw);

        assertThat(result.getName()).isNull();
    }
}
