package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.thedndpub.data.dto.source.SourceReference;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind")
@JsonSubTypes({
        @JsonSubTypes.Type(value = TextMechanicsEvidence.class, name = "TEXT_SPAN"),
        @JsonSubTypes.Type(value = StructuredFieldMechanicsEvidence.class, name = "STRUCTURED_FIELD"),
        @JsonSubTypes.Type(value = TransformedMechanicsEvidence.class, name = "TRANSFORMED"),
        @JsonSubTypes.Type(value = UnavailableOriginMechanicsEvidence.class, name = "UNAVAILABLE_ORIGIN")
})
public sealed interface MechanicsEvidence permits
        TextMechanicsEvidence,
        StructuredFieldMechanicsEvidence,
        TransformedMechanicsEvidence,
        UnavailableOriginMechanicsEvidence {
    String id();

    SourceReference source();
}
