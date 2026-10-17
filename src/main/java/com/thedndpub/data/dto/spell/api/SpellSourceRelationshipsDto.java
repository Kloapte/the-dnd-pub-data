package com.thedndpub.data.dto.spell.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceIdentity;
import com.thedndpub.data.dto.source.SourceReference;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SpellSourceRelationshipsDto(
        List<SourceReference> additionalSources,
        List<SourceReference> otherSources,
        List<SourceIdentity> referenceSources,
        List<SpellReprintDto> reprintedAs) {
    public SpellSourceRelationshipsDto {
        additionalSources = List.copyOf(additionalSources);
        otherSources = List.copyOf(otherSources);
        referenceSources = List.copyOf(referenceSources);
        reprintedAs = List.copyOf(reprintedAs);
    }
}
