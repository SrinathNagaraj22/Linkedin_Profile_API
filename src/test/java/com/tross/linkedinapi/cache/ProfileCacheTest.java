package com.tross.linkedinapi.cache;

import com.tross.linkedinapi.dto.ProfileResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProfileCacheTest {

    @Test
    void putThenGetReturnsSameValue() {
        ProfileCache cache = new ProfileCache(60);
        ProfileResponse response = new ProfileResponse();
        response.setName("Jane Doe");

        cache.put("jane-doe", response);

        assertThat(cache.get("jane-doe")).isEqualTo(response);
    }

    @Test
    void getReturnsNullForMissingKey() {
        ProfileCache cache = new ProfileCache(60);
        assertThat(cache.get("missing")).isNull();
    }

    @Test
    void entryExpiresAfterTtl() throws InterruptedException {
        // ttl of 0 minutes -> effectively expires almost immediately
        ProfileCache cache = new ProfileCache(0);
        ProfileResponse response = new ProfileResponse();
        cache.put("jane-doe", response);

        Thread.sleep(5);

        assertThat(cache.get("jane-doe")).isNull();
    }

    @Test
    void evictRemovesEntry() {
        ProfileCache cache = new ProfileCache(60);
        ProfileResponse response = new ProfileResponse();
        cache.put("jane-doe", response);

        cache.evict("jane-doe");

        assertThat(cache.get("jane-doe")).isNull();
    }
}
