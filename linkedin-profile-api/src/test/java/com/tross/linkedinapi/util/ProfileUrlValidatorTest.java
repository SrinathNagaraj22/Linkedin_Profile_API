package com.tross.linkedinapi.util;

import com.tross.linkedinapi.exception.InvalidProfileUrlException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProfileUrlValidatorTest {

    @Test
    void extractsIdentifierFromValidUrl() {
        String id = ProfileUrlValidator.extractPublicIdentifier(
                "https://www.linkedin.com/in/srinath-nagaraj-08b945301/");
        assertThat(id).isEqualTo("srinath-nagaraj-08b945301");
    }

    @Test
    void extractsIdentifierWithoutTrailingSlash() {
        String id = ProfileUrlValidator.extractPublicIdentifier("https://linkedin.com/in/jane-doe");
        assertThat(id).isEqualTo("jane-doe");
    }

    @Test
    void extractsIdentifierWithQueryParams() {
        String id = ProfileUrlValidator.extractPublicIdentifier(
                "https://www.linkedin.com/in/jane-doe/?originalSubdomain=in");
        assertThat(id).isEqualTo("jane-doe");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "https://google.com",
            "https://github.com/example",
            "not-a-url",
            "https://linkedin.com/company/example",
            ""
    })
    void rejectsNonProfileUrls(String url) {
        assertThatThrownBy(() -> ProfileUrlValidator.extractPublicIdentifier(url))
                .isInstanceOf(InvalidProfileUrlException.class);
    }

    @Test
    void rejectsNullUrl() {
        assertThatThrownBy(() -> ProfileUrlValidator.extractPublicIdentifier(null))
                .isInstanceOf(InvalidProfileUrlException.class);
    }
}
