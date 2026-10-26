package com.thedndpub.data.dto.corerules.language;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CreatureFilterSpeaker(CreatureFilter filter) implements LanguageSpeaker {
    public CreatureFilterSpeaker {
        if (filter == null) throw new IllegalArgumentException("Creature filter speaker requires a filter");
    }
}
