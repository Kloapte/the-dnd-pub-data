package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Comparator;

/** Canonical mechanics hash protocols, independent of application Jackson configuration. */
public final class MechanicsHashes {
    public static final int LEGACY_DOCUMENT_HASH_PROTOCOL = 1;
    public static final int DOCUMENT_HASH_PROTOCOL = 2;
    private static final String DOCUMENT_HASH_V2_PREFIX = "the-dnd-pub-mechanics-document-v2\n";
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
        return documentHash(content, DOCUMENT_HASH_PROTOCOL);
    }

    static String documentHash(MechanicsDocumentContent content, int protocolVersion) {
        try {
            if (protocolVersion == LEGACY_DOCUMENT_HASH_PROTOCOL) {
                return sha256(MAPPER.writeValueAsBytes(content));
            }
            ModelChecks.require(protocolVersion == DOCUMENT_HASH_PROTOCOL,
                    "Unsupported mechanics document hash protocol");
            MechanicsDocumentContent canonical = new MechanicsDocumentContent(content.owner(), content.sources(),
                    content.processing(), content.interpretations().stream()
                            .sorted(Comparator.comparing(record -> record.id().value())).toList(),
                    content.observations(), content.unsupported(), content.evidence(), content.coverage(),
                    content.reviewSummary());
            byte[] canonicalBytes = MAPPER.writeValueAsBytes(canonical);
            byte[] prefix = DOCUMENT_HASH_V2_PREFIX.getBytes(StandardCharsets.UTF_8);
            byte[] versioned = new byte[prefix.length + canonicalBytes.length];
            System.arraycopy(prefix, 0, versioned, 0, prefix.length);
            System.arraycopy(canonicalBytes, 0, versioned, prefix.length, canonicalBytes.length);
            return sha256(versioned);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("Cannot fingerprint mechanics document", exception);
        }
    }
}
