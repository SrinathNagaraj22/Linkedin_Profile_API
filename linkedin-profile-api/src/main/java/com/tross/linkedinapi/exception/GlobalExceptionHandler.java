package com.tross.linkedinapi.exception;

import com.tross.linkedinapi.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InvalidProfileUrlException.class)
    public ResponseEntity<ErrorResponse> handleInvalidUrl(InvalidProfileUrlException ex) {
        return build(HttpStatus.BAD_REQUEST, "INVALID_PROFILE_URL", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .orElse("Invalid request body.");
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", message);
    }

    @ExceptionHandler(LinkedInAuthException.class)
    public ResponseEntity<ErrorResponse> handleAuth(LinkedInAuthException ex) {
        return build(HttpStatus.UNAUTHORIZED, "LINKEDIN_AUTH_FAILED", ex.getMessage());
    }

    @ExceptionHandler(LinkedInAccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(LinkedInAccessDeniedException ex) {
        return build(HttpStatus.FORBIDDEN, "LINKEDIN_ACCESS_DENIED", ex.getMessage());
    }

    @ExceptionHandler(ProfileNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ProfileNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "PROFILE_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(LinkedInRateLimitException.class)
    public ResponseEntity<ErrorResponse> handleRateLimit(LinkedInRateLimitException ex) {
        return build(HttpStatus.TOO_MANY_REQUESTS, "LINKEDIN_RATE_LIMITED", ex.getMessage());
    }

    @ExceptionHandler(LinkedInClientException.class)
    public ResponseEntity<ErrorResponse> handleUpstream(LinkedInClientException ex) {
        log.error("Upstream LinkedIn error", ex);
        return build(HttpStatus.BAD_GATEWAY, "LINKEDIN_UPSTREAM_ERROR", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        log.error("Unhandled exception", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR",
                "Something went wrong while processing your request.");
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String error, String message) {
        ErrorResponse body = new ErrorResponse(Instant.now().toString(), status.value(), error, message);
        return ResponseEntity.status(status).body(body);
    }
}
