package com.thedndpub.data.publication.spell;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/** Dedicated canonical serializer; never inherits application JSON omission or formatting settings. */
public final class SpellPublicationHashes {
    private static final JsonMapper CANONICAL = JsonMapper.builder()
            .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
            .enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
            .build();

    private SpellPublicationHashes() {
    }

    public static String sha256(byte[] bytes) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 unavailable", exception);
        }
    }

    public static String sha256(String text) {
        return sha256(text.getBytes(StandardCharsets.UTF_8));
    }

    public static String decisionHash(SpellPublicationDecisionDte decision) {
        try {
            return sha256(CANONICAL.writeValueAsBytes(decision));
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Cannot canonicalize publication decision", exception);
        }
    }

    public static String policyHash(SpellPublicationPolicyDte policy) {
        try {
            return sha256(CANONICAL.writeValueAsBytes(policy.withoutReleaseApproval()));
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Cannot canonicalize publication policy", exception);
        }
    }

    public static String noticesHash(SpellPublicationNoticesDte notices) {
        try {
            return sha256(CANONICAL.writeValueAsBytes(notices.withoutApproval()));
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Cannot canonicalize publication notices", exception);
        }
    }
}
