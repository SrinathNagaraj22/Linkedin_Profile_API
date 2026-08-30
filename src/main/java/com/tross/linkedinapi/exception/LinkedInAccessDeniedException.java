package com.tross.linkedinapi.exception;

public class LinkedInAccessDeniedException extends RuntimeException {
    public LinkedInAccessDeniedException(String message) {
        super(message);
    }
}
