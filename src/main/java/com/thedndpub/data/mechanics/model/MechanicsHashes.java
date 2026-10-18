package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

/** Canonical mechanics hash protocol v1, independent of application Jackson configuration. */
public final class MechanicsHashes {
    private static final JsonMapper MAPPER = JsonMapper.builder()
            .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
            .enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
            .build();

    private MechanicsHashes() {
    }

    public static String sha256(byte[] value) {
        try {
            return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(value));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 unavailable", exception);
        }
    }

    public static String sha256(String value) {
        return sha256(value.getBytes(StandardCharsets.UTF_8));
    }

    public static String evidenceHash(MechanicsEvidence evidence) {
        try {
            return sha256(MAPPER.writerFor(MechanicsEvidence.class).writeValueAsBytes(evidence));
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Cannot fingerprint mechanics evidence", exception);
        }
    }

    public static String observationHash(MechanicsObservationReference observation) {
        try {
            return sha256(MAPPER.writerFor(MechanicsObservationReference.class).writeValueAsBytes(observation));
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Cannot fingerprint mechanics observation reference", exception);
        }
    }

    public static String claimHash(MechanicRevisionContent content) {
        return sha256(claimBytes(content));
    }

    static byte[] claimBytes(MechanicRevisionContent content) {
        try {
            return MAPPER.writeValueAsBytes(content);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Cannot fingerprint mechanics claim", exception);
        }
    }

    public static String documentHash(MechanicsDocumentContent content) {
        try {
            return sha256(MAPPER.writeValueAsBytes(content));
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Cannot fingerprint mechanics document", exception);
        }
    }
}
