package com.thedndpub.data.mechanics.inspection;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind")
@JsonSubTypes({
        @JsonSubTypes.Type(value = TextEvidenceLocatorDto.class, name = "TEXT_SPAN"),
        @JsonSubTypes.Type(value = StructuredEvidenceLocatorDto.class, name = "STRUCTURED_FIELD"),
        @JsonSubTypes.Type(value = TransformedEvidenceLocatorDto.class, name = "TRANSFORMED"),
        @JsonSubTypes.Type(value = UnavailableEvidenceLocatorDto.class, name = "ORIGIN_UNAVAILABLE")
})
public sealed interface EvidenceLocatorDto permits TextEvidenceLocatorDto,
        StructuredEvidenceLocatorDto,
        TransformedEvidenceLocatorDto,
        UnavailableEvidenceLocatorDto {
}
