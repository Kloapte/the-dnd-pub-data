package com.thedndpub.data.dto.feat;

import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dte.feat.FeatSourceReferenceDte;
import com.thedndpub.data.dte.common.entry.EntryDte;

import java.util.List;

public record FeatDefinition(
        FeatId id,
        String name,
        SourceReference source,
        String category,
        List<FeatPrerequisiteDto> prerequisites,
        List<FeatAbilityIncreaseDto> abilityIncreases,
        List<FeatSourceReferenceDte> additionalSources,
        List<FeatReferenceDto> reprintedAs,
        Boolean repeatable,
        List<FeatAdditionalSpell> additionalSpells,
        List<EntryDte> entries,
        boolean variant,
        FeatId parentId) {
    public FeatDefinition {
        prerequisites = List.copyOf(prerequisites);
        abilityIncreases = List.copyOf(abilityIncreases);
        additionalSources = List.copyOf(additionalSources);
        reprintedAs = List.copyOf(reprintedAs);
        additionalSpells = List.copyOf(additionalSpells);
        entries = List.copyOf(entries);
    }
}
