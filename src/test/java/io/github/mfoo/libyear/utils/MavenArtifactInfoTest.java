package io.github.mfoo.libyear.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import org.junit.jupiter.api.Test;

/**
 * Test class for MavenArtifactInfo.
 *
 * @author Marc Zottner
 */
class MavenArtifactInfoTest {

    /**
     * Test fetching the Last-Modified timestamp of a known Maven artifact.
     * @throws IOException
     */
    @Test
    void testGetLastModifiedTimestamp() throws IOException {
        // Given
        String groupId = "org.springframework";
        String artifactId = "spring-beans";
        String version = "7.0.1";

        // When
        long timestamp = MavenArtifactInfo.getLastModifiedTimestamp(groupId, artifactId, version);

        // Then
        // Expected: Thu, 20 Nov 2025 09:17:38 GMT = 1763630258000L
        assertEquals(1763630258000L, timestamp);
    }

    /**
     * Test fetching the Last-Modified timestamp of a known Maven artifact returns a positive value.
     * @throws IOException
     */
    @Test
    void testGetLastModifiedTimestampReturnsPositiveValue() throws IOException {
        // Given
        String groupId = "org.springframework";
        String artifactId = "spring-beans";
        String version = "7.0.1";

        // When
        long timestamp = MavenArtifactInfo.getLastModifiedTimestamp(groupId, artifactId, version);

        // Then
        assertTrue(timestamp > 0, "Timestamp should be positive");
        assertTrue(timestamp > 1700000000000L, "Timestamp should be after 2023");
    }

    /**
     * Test fetching the Last-Modified timestamp of a non-existing Maven artifact throws IOException.
     */
    @Test
    void testGetLastModifiedTimestampThrowsExceptionForInvalidArtifact() {
        // Given
        String groupId = "org.invalid";
        String artifactId = "invalid-artifact";
        String version = "99.99.99";

        // When & Then
        assertThrows(IOException.class, () -> {
            MavenArtifactInfo.getLastModifiedTimestamp(groupId, artifactId, version);
        });
    }

    /**
     * Test fetching the Last-Modified timestamp of an AndroidX artifact from Google Maven.
     * @throws IOException
     */
    @Test
    void testGetLastModifiedTimestampForAndroidXArtifact() throws IOException {
        // Given
        String groupId = "androidx.test.espresso";
        String artifactId = "espresso-intents";
        String version = "3.4.0";

        // When
        long timestamp = MavenArtifactInfo.getLastModifiedTimestamp(groupId, artifactId, version);

        // Then
        assertTrue(timestamp > 0, "Timestamp should be positive");
        assertTrue(timestamp > 1600000000000L, "Timestamp should be after 2020 (3.4.0 was released in 2021)");
    }

    /**
     * Test fetching the Last-Modified timestamp of a newer AndroidX artifact from Google Maven.
     * @throws IOException
     */
    @Test
    void testGetLastModifiedTimestampForNewerAndroidXArtifact() throws IOException {
        // Given
        String groupId = "androidx.test.espresso";
        String artifactId = "espresso-intents";
        String version = "3.7.0";

        // When
        long timestamp = MavenArtifactInfo.getLastModifiedTimestamp(groupId, artifactId, version);

        // Then
        assertTrue(timestamp > 0, "Timestamp should be positive");
        assertTrue(timestamp > 1700000000000L, "Timestamp should be after 2023 (3.7.0 was released in 2025)");
    }

    /**
     * Test fetching the Last-Modified timestamp of a Gradle plugin from Gradle Plugin Portal.
     * Note: Gradle Plugin Portal returns 303 redirects, so this test may behave differently.
     * @throws IOException
     */
    @Test
    void testGetLastModifiedTimestampForGradlePlugin() throws IOException {
        // Given - using a popular Gradle plugin that's in Maven Central
        String groupId = "com.diffplug.spotless";
        String artifactId = "spotless-plugin-gradle";
        String version = "6.23.3";

        // When
        long timestamp = MavenArtifactInfo.getLastModifiedTimestamp(groupId, artifactId, version);

        // Then
        assertTrue(timestamp > 0, "Timestamp should be positive");
        assertTrue(timestamp > 1700000000000L, "Timestamp should be after 2023");
    }

    /**
     * Test fetching the Last-Modified timestamp of a JitPack artifact.
     * @throws IOException
     */
    @Test
    void testGetLastModifiedTimestampForJitPackArtifact() throws IOException {
        // Given - a real JitPack dependency (GitHub-only project)
        String groupId = "com.github.PhilJay";
        String artifactId = "MPAndroidChart";
        String version = "v3.1.0";

        // When
        long timestamp = MavenArtifactInfo.getLastModifiedTimestamp(groupId, artifactId, version);

        // Then
        assertTrue(timestamp > 0, "Timestamp should be positive");
        assertTrue(timestamp > 1500000000000L, "Timestamp should be after 2017 (v3.1.0 was released around 2018)");
    }
}
