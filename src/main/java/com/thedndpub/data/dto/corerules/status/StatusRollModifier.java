package com.thedndpub.data.dto.corerules.status;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record StatusRollModifier(String kind, String roll, String direction, String actor) implements StatusMechanic {
    public StatusRollModifier {
        if (!"ROLL_MODIFIER".equals(kind) || roll == null || direction == null) {
            throw new IllegalArgumentException("Invalid status roll modifier");
        }
    }
}
