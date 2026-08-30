package com.tross.linkedinapi.config;

import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

/**
 * Provides a single RestClient bean, pre-configured with sane connect/read
 * timeouts, for any outbound HTTP call this service makes (currently just
 * the LinkedIn client). Centralizing this means we never accidentally let
 * an upstream call hang indefinitely.
 */
@Configuration
public class RestClientConfig {

    @Bean
    public RestClient linkedInRestClient(LinkedInConfig config) {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofMillis(config.getConnectTimeoutMs()))
                .withReadTimeout(Duration.ofMillis(config.getReadTimeoutMs()));

        ClientHttpRequestFactory requestFactory = ClientHttpRequestFactories.get(settings);

        return RestClient.builder()
                .baseUrl(config.getBaseUrl())
                .requestFactory(requestFactory)
                .build();
    }
}
