package com.thedndpub.data.dto.corerules.language;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CreatureTagSelector(String kind, String value, String source) {
    public CreatureTagSelector {
        if (!"TAG".equals(kind) || value == null || value.isBlank()) {
            throw new IllegalArgumentException("Creature tag selector requires its exact source tag");
        }
    }
}
