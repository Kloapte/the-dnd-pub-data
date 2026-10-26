package com.thedndpub.data.dto.corerules.status;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record StatusTurnRestriction(String kind, String restriction, String appliesWhen) implements StatusMechanic {
    public StatusTurnRestriction {
        if (!"TURN_RESTRICTION".equals(kind) || restriction == null || appliesWhen == null) {
            throw new IllegalArgumentException("Invalid turn restriction mechanic");
        }
    }
}
