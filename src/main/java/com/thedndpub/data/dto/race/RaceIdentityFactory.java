package com.thedndpub.data.dto.race;

import java.text.Normalizer;
import java.util.Locale;

public final class RaceIdentityFactory {
    private RaceIdentityFactory() {
    }

    public static RaceId race(String name, String source) {
        return new RaceId("race:" + slug(source) + ":" + slug(name));
    }

    public static RaceId subrace(String name, String source, String parentName, String parentSource) {
        return new RaceId("race:" + slug(source) + ":" + slug(name)
                + ":parent-" + slug(parentSource) + "-" + slug(parentName));
    }

    private static String slug(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Race identity components must not be blank");
        }
        String normalized = Normalizer.normalize(value, Normalizer.Form.NFKD)
                .replaceAll("\\p{M}+", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
        if (normalized.isBlank()) {
            throw new IllegalArgumentException("Race identity component has no slug characters: " + value);
        }
        return normalized;
    }
}
