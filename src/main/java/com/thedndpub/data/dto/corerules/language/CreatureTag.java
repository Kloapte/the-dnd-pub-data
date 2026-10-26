package com.thedndpub.data.dto.corerules.language;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CreatureTag(String group, String value) {
    public CreatureTag {
        if (group == null || value == null) throw new IllegalArgumentException("Creature tag requires group and value");
    }
}
