package com.tross.linkedinapi.cache;

import com.tross.linkedinapi.dto.ProfileResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Minimal in-memory, TTL-based cache keyed by LinkedIn public identifier.
 * Deliberately dependency-free (no Redis) — profile data changes rarely,
 * and a single-instance deployment doesn't need a shared cache. If this
 * ever needs to run across multiple instances, swap this implementation
 * for a Redis-backed one behind the same class; nothing else changes.
 */
@Component
public class ProfileCache {

    private final Map<String, CacheEntry> store = new ConcurrentHashMap<>();
    private final long ttlMillis;

    public ProfileCache(@Value("${profile-cache.ttl-minutes:60}") long ttlMinutes) {
        this.ttlMillis = ttlMinutes * 60_000L;
    }

    public ProfileResponse get(String key) {
        CacheEntry entry = store.get(key);
        if (entry == null) {
            return null;
        }
        if (Instant.now().isAfter(entry.expiresAt())) {
            store.remove(key);
            return null;
        }
        return entry.value();
    }

    public void put(String key, ProfileResponse value) {
        store.put(key, new CacheEntry(value, Instant.now().plusMillis(ttlMillis)));
    }

    public void evict(String key) {
        store.remove(key);
    }

    public int size() {
        return store.size();
    }

    private record CacheEntry(ProfileResponse value, Instant expiresAt) {
    }
}
