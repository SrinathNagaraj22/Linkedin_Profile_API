package com.tross.linkedinapi.dto;

public class MetadataDto {

    private String source;
    private String fetchedAt;
    private boolean cached;

    public MetadataDto() {
    }

    public MetadataDto(String source, String fetchedAt, boolean cached) {
        this.source = source;
        this.fetchedAt = fetchedAt;
        this.cached = cached;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getFetchedAt() {
        return fetchedAt;
    }

    public void setFetchedAt(String fetchedAt) {
        this.fetchedAt = fetchedAt;
    }

    public boolean isCached() {
        return cached;
    }

    public void setCached(boolean cached) {
        this.cached = cached;
    }
}
