package com.tross.linkedinapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Binds the `linkedin.*` properties from application.yml, which in turn
 * read from environment variables (LINKEDIN_COOKIE, LINKEDIN_CSRF_TOKEN).
 * Never hardcode real values here or anywhere else in source control.
 */
@Component
@ConfigurationProperties(prefix = "linkedin")
public class LinkedInConfig {

    private String baseUrl;
    private String cookie;
    private String csrfToken;
    private int connectTimeoutMs = 5000;
    private int readTimeoutMs = 8000;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public String getCsrfToken() {
        return csrfToken;
    }

    public void setCsrfToken(String csrfToken) {
        this.csrfToken = csrfToken;
    }

    public int getConnectTimeoutMs() {
        return connectTimeoutMs;
    }

    public void setConnectTimeoutMs(int connectTimeoutMs) {
        this.connectTimeoutMs = connectTimeoutMs;
    }

    public int getReadTimeoutMs() {
        return readTimeoutMs;
    }

    public void setReadTimeoutMs(int readTimeoutMs) {
        this.readTimeoutMs = readTimeoutMs;
    }
}
