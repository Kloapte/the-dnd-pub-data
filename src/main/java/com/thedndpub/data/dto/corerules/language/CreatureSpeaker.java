package com.thedndpub.data.dto.corerules.language;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CreatureSpeaker(String targetType, String id) implements LanguageSpeaker {
    public CreatureSpeaker {
        if (!"creature".equals(targetType) || id == null || !id.startsWith("creature:")) {
            throw new IllegalArgumentException("Creature speaker requires a source-qualified Creature identity");
        }
    }
}
