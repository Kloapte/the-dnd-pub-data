package com.thedndpub.data.publication.spell;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;

/** Fixed classpath inputs only. An absent decision journal is different from a broken journal. */
public final class SpellPublicationSourceLoader {
    public static final String PREFIX = "classpath:publication/spell-formulas/v1/";

    private final ObjectMapper mapper;
    private final ResourceLoader resources;

    public SpellPublicationSourceLoader(ObjectMapper mapper) {
        this(mapper, new DefaultResourceLoader());
    }

    public SpellPublicationSourceLoader(ObjectMapper mapper, ResourceLoader resources) {
        this.resources = resources;
        this.mapper = mapper.copy();
        this.mapper.enable(
                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                DeserializationFeature.FAIL_ON_TRAILING_TOKENS,
                DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS,
                DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES,
                DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES);
        this.mapper.disable(
                DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL,
                DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_USING_DEFAULT_VALUE,
                DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY,
                DeserializationFeature.UNWRAP_SINGLE_VALUE_ARRAYS,
                DeserializationFeature.ACCEPT_FLOAT_AS_INT,
                DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT,
                DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT);
        this.mapper.disable(
                MapperFeature.ALLOW_COERCION_OF_SCALARS,
                MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS,
                MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES);
        this.mapper.getFactory().enable(JsonParser.Feature.STRICT_DUPLICATE_DETECTION);
    }

    public SpellPublicationResources load() {
        try {
            byte[] policy = requiredBytes("policy.json");
            byte[] candidates = requiredBytes("candidates.json");
            byte[] evidence = requiredBytes("evidence.json");
            byte[] notices = requiredBytes("notices.json");
            Resource decisionsResource = resources.getResource(PREFIX + "decisions.json");
            byte[] decisions = null;
            if (decisionsResource.exists()) {
                decisions = decisionsResource.getContentAsByteArray();
            }
            return new SpellPublicationResources(
                    mapper.readValue(policy, SpellPublicationPolicyDte.class),
                    mapper.readValue(candidates, SpellPublicationCandidatesDte.class),
                    SpellPublicationHashes.sha256(candidates),
                    mapper.readValue(evidence, SpellPublicationEvidenceDte.class),
                    SpellPublicationHashes.sha256(evidence),
                    mapper.readValue(notices, SpellPublicationNoticesDte.class),
                    SpellPublicationHashes.sha256(notices),
                    decisions == null ? null : mapper.readValue(decisions, SpellPublicationDecisionsDte.class),
                    decisions == null ? null : SpellPublicationHashes.sha256(decisions));
        } catch (IOException | IllegalArgumentException exception) {
            throw new IllegalStateException("Cannot load strict Spell publication resources", exception);
        }
    }

    private byte[] requiredBytes(String name) throws IOException {
        return resources.getResource(PREFIX + name).getContentAsByteArray();
    }
}
