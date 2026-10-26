package com.thedndpub.data.dto.corerules.status;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.corerules.CanonicalReference;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record HitPointThreshold(String kind, double fraction, String comparison, CanonicalReference reference)
        implements StatusMechanic {
    public HitPointThreshold {
        if (!"HIT_POINT_THRESHOLD".equals(kind) || fraction <= 0 || fraction > 1 || comparison == null) {
            throw new IllegalArgumentException("Invalid hit point threshold mechanic");
        }
    }
}
