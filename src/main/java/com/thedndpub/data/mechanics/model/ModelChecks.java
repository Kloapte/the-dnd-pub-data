package com.thedndpub.data.mechanics.model;

import java.net.URI;
import java.util.List;
import java.util.Objects;

/** Small scalar invariants shared by the canonical values; no source interpretation. */
public final class ModelChecks {
    private ModelChecks() {
    }

    public static <T> T required(T value, String label) {
        return Objects.requireNonNull(value, label);
    }

    /** Canonical optional collections equate omission and empty; source DTEs retain authored distinctions. */
    public static <T> List<T> list(List<T> values) {
        return values == null ? List.of() : List.copyOf(values);
    }

    public static String text(String value, String label) {
        require(value != null && !value.isBlank(), label + " is required");
        return value;
    }

    public static String digest(String value) {
        require(value != null && value.matches("[0-9a-f]{64}"), "A lowercase SHA-256 fingerprint is required");
        return value;
    }

    public static void require(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }

    public static String pointer(String value) {
        required(value, "pointer");
        require(value.isEmpty() || value.startsWith("/"), "An RFC 6901 pointer is required");
        for (int index = 0; index < value.length(); index++) {
            if (value.charAt(index) == '~') {
                index++;
                require(index < value.length() && (value.charAt(index) == '0' || value.charAt(index) == '1'),
                        "Invalid pointer escape");
            }
        }
        return value;
    }

    public static void resource(String value) {
        text(value, "resourcePath");
        require(value.startsWith("data/") && !value.contains("..") && !value.contains("\\"),
                "A pinned classpath data resource is required");
    }

    public static void https(String value) {
        URI uri = URI.create(text(value, "URI"));
        require("https".equals(uri.getScheme()) && uri.getHost() != null, "An absolute HTTPS URI is required");
    }
}
