package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.ContextAttributes;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.thedndpub.data.mechanics.review.MechanicsReviewPolicy;

/** Opt-in strict model codec. The application's ObjectMapper and endpoints remain untouched. */
public final class MechanicsJson {
    private MechanicsJson() {
    }

    public static ObjectMapper mapper() {
        return mapper(MechanicsReviewPolicy.none());
    }

    public static ObjectMapper mapper(MechanicsReviewPolicy reviewPolicy) {
        ObjectMapper mapper = JsonMapper.builder()
                .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                        DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES,
                        DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS,
                        DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
                .disable(MapperFeature.ALLOW_COERCION_OF_SCALARS)
                .disable(DeserializationFeature.ACCEPT_FLOAT_AS_INT,
                        DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .build();
        mapper.getFactory().enable(JsonParser.Feature.STRICT_DUPLICATE_DETECTION);
        mapper.setDefaultAttributes(ContextAttributes.getEmpty().withSharedAttribute(
                MechanicsReviewPolicy.CONTEXT_ATTRIBUTE, ModelChecks.required(reviewPolicy, "reviewPolicy")));
        return mapper;
    }
}
