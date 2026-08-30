package com.tross.linkedinapi.service;

import com.tross.linkedinapi.cache.ProfileCache;
import com.tross.linkedinapi.client.LinkedInClient;
import com.tross.linkedinapi.client.dto.RawLinkedInProfile;
import com.tross.linkedinapi.dto.ProfileApiResponse;
import com.tross.linkedinapi.exception.InvalidProfileUrlException;
import com.tross.linkedinapi.exception.LinkedInAuthException;
import com.tross.linkedinapi.exception.LinkedInRateLimitException;
import com.tross.linkedinapi.exception.ProfileNotFoundException;
import com.tross.linkedinapi.mapper.LinkedInProfileMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    private static final String VALID_URL = "https://www.linkedin.com/in/jane-doe/";

    @Mock
    private LinkedInClient linkedInClient;

    private ProfileService profileService;

    @BeforeEach
    void setUp() {
        ProfileCache cache = new ProfileCache(60);
        profileService = new ProfileService(linkedInClient, new LinkedInProfileMapper(), cache);
    }

    @Test
    void returnsMappedProfileOnCacheMiss() {
        RawLinkedInProfile raw = new RawLinkedInProfile();
        raw.setFirstName("Jane");
        raw.setLastName("Doe");
        when(linkedInClient.fetchProfile("jane-doe")).thenReturn(raw);

        ProfileApiResponse response = profileService.getProfile(VALID_URL);

        assertThat(response.getProfile().getName()).isEqualTo("Jane Doe");
        assertThat(response.getMetadata().isCached()).isFalse();
        verify(linkedInClient, times(1)).fetchProfile("jane-doe");
    }

    @Test
    void returnsCachedProfileOnSecondCall() {
        RawLinkedInProfile raw = new RawLinkedInProfile();
        raw.setFirstName("Jane");
        raw.setLastName("Doe");
        when(linkedInClient.fetchProfile("jane-doe")).thenReturn(raw);

        profileService.getProfile(VALID_URL);
        ProfileApiResponse second = profileService.getProfile(VALID_URL);

        assertThat(second.getMetadata().isCached()).isTrue();
        verify(linkedInClient, times(1)).fetchProfile("jane-doe");
    }

    @Test
    void rejectsInvalidUrlBeforeCallingClient() {
        assertThatThrownBy(() -> profileService.getProfile("https://google.com"))
                .isInstanceOf(InvalidProfileUrlException.class);
        verifyNoInteractions(linkedInClient);
    }

    @Test
    void propagatesAuthExceptionFromClient() {
        when(linkedInClient.fetchProfile("jane-doe")).thenThrow(new LinkedInAuthException("no session"));

        assertThatThrownBy(() -> profileService.getProfile(VALID_URL))
                .isInstanceOf(LinkedInAuthException.class);
    }

    @Test
    void propagatesNotFoundExceptionFromClient() {
        when(linkedInClient.fetchProfile("jane-doe")).thenThrow(new ProfileNotFoundException("no profile"));

        assertThatThrownBy(() -> profileService.getProfile(VALID_URL))
                .isInstanceOf(ProfileNotFoundException.class);
    }

    @Test
    void propagatesRateLimitExceptionFromClient() {
        when(linkedInClient.fetchProfile("jane-doe")).thenThrow(new LinkedInRateLimitException("slow down"));

        assertThatThrownBy(() -> profileService.getProfile(VALID_URL))
                .isInstanceOf(LinkedInRateLimitException.class);
    }
}
