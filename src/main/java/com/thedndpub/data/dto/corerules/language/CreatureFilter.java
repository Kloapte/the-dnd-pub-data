package com.thedndpub.data.dto.corerules.language;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CreatureFilter(
        String targetType,
        List<String> creatureTypes,
        List<CreatureTag> tags,
        List<String> sources,
        CreatureTagSelector selector) {
    public CreatureFilter {
        if (!"creature".equals(targetType)) throw new IllegalArgumentException("Creature filter target type is invalid");
        creatureTypes = creatureTypes == null ? null : List.copyOf(creatureTypes);
        tags = tags == null ? null : List.copyOf(tags);
        sources = sources == null ? null : List.copyOf(sources);
    }
}
