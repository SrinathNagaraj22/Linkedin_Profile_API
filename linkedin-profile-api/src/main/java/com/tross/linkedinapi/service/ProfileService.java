package com.tross.linkedinapi.service;

import com.tross.linkedinapi.cache.ProfileCache;
import com.tross.linkedinapi.client.LinkedInClient;
import com.tross.linkedinapi.client.dto.RawLinkedInProfile;
import com.tross.linkedinapi.dto.MetadataDto;
import com.tross.linkedinapi.dto.ProfileApiResponse;
import com.tross.linkedinapi.dto.ProfileResponse;
import com.tross.linkedinapi.mapper.LinkedInProfileMapper;
import com.tross.linkedinapi.util.ProfileUrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class ProfileService {

    private static final Logger log = LoggerFactory.getLogger(ProfileService.class);

    private final LinkedInClient linkedInClient;
    private final LinkedInProfileMapper mapper;
    private final ProfileCache cache;

    public ProfileService(LinkedInClient linkedInClient, LinkedInProfileMapper mapper, ProfileCache cache) {
        this.linkedInClient = linkedInClient;
        this.mapper = mapper;
        this.cache = cache;
    }

    public ProfileApiResponse getProfile(String profileUrl) {
        String publicIdentifier = ProfileUrlValidator.extractPublicIdentifier(profileUrl);

        ProfileResponse cached = cache.get(publicIdentifier);
        if (cached != null) {
            log.info("Cache hit for identifier={}", publicIdentifier);
            return new ProfileApiResponse(cached, metadata(true));
        }

        log.info("Cache miss for identifier={}. Calling LinkedInClient.", publicIdentifier);
        RawLinkedInProfile raw = linkedInClient.fetchProfile(publicIdentifier);
        ProfileResponse mapped = mapper.toProfileResponse(raw);

        cache.put(publicIdentifier, mapped);

        return new ProfileApiResponse(mapped, metadata(false));
    }

    private MetadataDto metadata(boolean cached) {
        return new MetadataDto("linkedin", Instant.now().toString(), cached);
    }
}
