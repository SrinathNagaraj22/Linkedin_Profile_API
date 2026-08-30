package com.tross.linkedinapi.client;

import com.tross.linkedinapi.client.dto.RawLinkedInProfile;

/**
 * Boundary between our service layer and LinkedIn. Nothing above this
 * interface (controller, service, mapper) knows or cares how the data
 * is actually retrieved.
 */
public interface LinkedInClient {

    /**
     * Fetches raw profile data for the given LinkedIn public identifier
     * (the vanity name segment of a profile URL, e.g. "jane-doe").
     *
     * @throws com.tross.linkedinapi.exception.LinkedInAuthException     if the session/credentials are missing or expired
     * @throws com.tross.linkedinapi.exception.LinkedInAccessDeniedException if LinkedIn denies access to the profile
     * @throws com.tross.linkedinapi.exception.ProfileNotFoundException if the profile does not exist
     * @throws com.tross.linkedinapi.exception.LinkedInRateLimitException if LinkedIn rate-limits the request
     * @throws com.tross.linkedinapi.exception.LinkedInClientException  for any other upstream failure (timeout, malformed response, etc.)
     */
    RawLinkedInProfile fetchProfile(String publicIdentifier);
}
