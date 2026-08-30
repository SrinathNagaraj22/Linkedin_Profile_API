package com.tross.linkedinapi.util;

import com.tross.linkedinapi.exception.InvalidProfileUrlException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validates that a given string is a LinkedIn *personal profile* URL
 * (linkedin.com/in/...) and extracts the public identifier (vanity name)
 * from it dynamically. Company pages, random URLs, and malformed input
 * are all rejected.
 */
public final class ProfileUrlValidator {

    private static final Pattern PROFILE_URL_PATTERN = Pattern.compile(
            "^https?://([a-z]{2,3}\\.)?(www\\.)?linkedin\\.com/in/([a-zA-Z0-9\\-%._]+)/?.*$"
    );

    private ProfileUrlValidator() {
    }

    public static String extractPublicIdentifier(String profileUrl) {
        if (profileUrl == null || profileUrl.isBlank()) {
            throw new InvalidProfileUrlException("Profile URL must not be empty.");
        }

        Matcher matcher = PROFILE_URL_PATTERN.matcher(profileUrl.trim());
        if (!matcher.matches()) {
            throw new InvalidProfileUrlException(
                    "'" + profileUrl + "' is not a valid LinkedIn profile URL.");
        }

        return matcher.group(3);
    }
}
