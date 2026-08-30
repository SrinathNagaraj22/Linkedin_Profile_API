package com.tross.linkedinapi.exception;

public class LinkedInRateLimitException extends RuntimeException {
    public LinkedInRateLimitException(String message) {
        super(message);
    }
}
