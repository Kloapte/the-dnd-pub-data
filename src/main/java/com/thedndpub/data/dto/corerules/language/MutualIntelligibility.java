package com.thedndpub.data.dto.corerules.language;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.corerules.CanonicalReference;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record MutualIntelligibility(
        String kind,
        String mode,
        String directionality,
        CanonicalReference language) {
}
