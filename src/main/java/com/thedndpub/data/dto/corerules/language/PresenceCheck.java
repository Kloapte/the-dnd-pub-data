package com.thedndpub.data.dto.corerules.language;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.corerules.CanonicalReference;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PresenceCheck(
        String kind,
        String ability,
        CanonicalReference skill,
        int dc,
        String outcome) {
}
