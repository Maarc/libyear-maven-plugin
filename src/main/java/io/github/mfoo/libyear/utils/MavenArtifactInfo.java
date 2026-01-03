package io.github.mfoo.libyear.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Utility class to fetch Maven artifact information from various Maven repositories.
 *
 * @author Marc Zottner
 */
public final class MavenArtifactInfo {

    /** Maven Central base URL. */
    private static final String MAVEN_CENTRAL = "https://repo1.maven.org/maven2/";

    /** Google Maven Repository base URL. */
    private static final String GOOGLE_MAVEN = "https://dl.google.com/dl/android/maven2/";

    /** Gradle Plugin Portal base URL. */
    private static final String GRADLE_PLUGIN_PORTAL = "https://plugins.gradle.org/m2/";

    /** JitPack base URL. */
    private static final String JITPACK = "https://jitpack.io/";

    /**
     * List of known repository base URLs to try in order.
     * Order optimized for coverage: Maven Central (85-90%), Google Maven (+8%),
     * Gradle Plugin Portal (+1-2%), JitPack (+1-2%).
     */
    private static final List<String> KNOWN_REPOSITORIES =
            Arrays.asList(MAVEN_CENTRAL, GOOGLE_MAVEN, GRADLE_PLUGIN_PORTAL, JITPACK);

    /** DateTimeFormatter for parsing HTTP date strings. */
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);

    /**
     * Private constructor to prevent instantiation.
     */
    private MavenArtifactInfo() {}

    /**
     * Parses an HTTP date string to a timestamp in milliseconds.
     *
     * @param httpDate http date string
     * @return timestamp in milliseconds
     */
    public static long parseHttpDate(String httpDate) {
        ZonedDateTime dateTime = ZonedDateTime.parse(httpDate.replace(" GMT", " +0000"), FORMATTER);
        return dateTime.toInstant().toEpochMilli();
    }

    /**
     * Fetches the Last-Modified timestamp of a Maven artifact from Maven repositories.
     * Tries all known repositories in order: Maven Central, Google Maven, Gradle Plugin Portal, JitPack.
     *
     * @param groupId groupId
     * @param artifactId artifactId
     * @param version version
     * @return last modified timestamp in milliseconds
     * @throws IOException if an I/O error occurs or artifact is not found in any repository
     */
    public static long getLastModifiedTimestamp(String groupId, String artifactId, String version) throws IOException {
        final String path = groupId.replace('.', '/') + "/" + artifactId + "/" + version + "/" + artifactId + "-"
                + version + ".pom";

        IOException lastException = null;

        // Try all repositories in order
        for (String repositoryUrl : KNOWN_REPOSITORIES) {
            try {
                return getLastModifiedFromRepository(repositoryUrl, path);
            } catch (IOException e) {
                lastException = e;
                // Continue to next repository
            }
        }

        // If all repositories fail, throw the last exception
        throw new IOException("Artifact not found in any repository. Last error: " + lastException.getMessage());
    }

    /**
     * Fetches the Last-Modified timestamp from a specific repository.
     *
     * @param repositoryUrl the base URL of the repository
     * @param path the path to the artifact
     * @return last modified timestamp in milliseconds
     * @throws IOException if an I/O error occurs
     */
    private static long getLastModifiedFromRepository(String repositoryUrl, String path) throws IOException {
        final HttpURLConnection connection = (HttpURLConnection) new URL(repositoryUrl + path).openConnection();

        connection.setRequestMethod("HEAD");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.connect();

        final int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            connection.disconnect();
            throw new IOException("Artifact not found at " + repositoryUrl + ". HTTP response code: " + responseCode);
        }

        final String lastModified = connection.getHeaderField("Last-Modified");
        connection.disconnect();

        if (lastModified == null) {
            throw new IOException("No Last-Modified header found at " + repositoryUrl);
        }

        return parseHttpDate(lastModified);
    }
}
