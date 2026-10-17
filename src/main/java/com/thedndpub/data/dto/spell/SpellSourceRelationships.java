package com.thedndpub.data.dto.spell;

import com.thedndpub.data.dto.source.SourceIdentity;
import com.thedndpub.data.dto.source.SourceReference;

import java.util.List;

public record SpellSourceRelationships(
        List<SourceReference> additionalSources,
        List<SourceReference> otherSources,
        List<SourceIdentity> referenceSources,
        List<SpellReprintReference> reprintedAs) {
    public SpellSourceRelationships {
        additionalSources = List.copyOf(additionalSources);
        otherSources = List.copyOf(otherSources);
        referenceSources = List.copyOf(referenceSources);
        reprintedAs = List.copyOf(reprintedAs);
    }
}
