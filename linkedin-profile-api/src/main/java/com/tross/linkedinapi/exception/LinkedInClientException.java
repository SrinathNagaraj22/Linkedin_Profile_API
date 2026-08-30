package com.tross.linkedinapi.exception;

public class LinkedInClientException extends RuntimeException {
    public LinkedInClientException(String message) {
        super(message);
    }

    public LinkedInClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
