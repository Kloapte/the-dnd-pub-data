package com.thedndpub.data.dto.corerules.status;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.corerules.CanonicalReference;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record StatusEndingCondition(
        String kind,
        String when,
        Boolean automatic,
        Boolean actionRequired,
        CanonicalReference reference) implements StatusMechanic {
    public StatusEndingCondition {
        if (!"ENDING_CONDITION".equals(kind) || when == null) {
            throw new IllegalArgumentException("Invalid status ending condition");
        }
    }
}
