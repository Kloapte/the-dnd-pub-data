package com.thedndpub.data.dte.legendarygroup;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thedndpub.data.dte.common.entry.EntryDte;

import java.util.List;

public record LegendaryGroupSourceDte(
        String name,
        String source,
        Integer page,
        List<LegendaryGroupSourceReferenceDte> additionalSources,
        List<EntryDte> lairActions,
        List<EntryDte> regionalEffects,
        List<EntryDte> mythicEncounter,
        @JsonProperty("_copy") LegendaryGroupCopyDte copy,
        @JsonProperty("_versions") List<LegendaryGroupVersionDte> versions) {
    public LegendaryGroupSourceDte {
        additionalSources = additionalSources == null ? null : List.copyOf(additionalSources);
        lairActions = LegendaryGroupEntryCopies.copy(lairActions);
        regionalEffects = LegendaryGroupEntryCopies.copy(regionalEffects);
        mythicEncounter = LegendaryGroupEntryCopies.copy(mythicEncounter);
        versions = versions == null ? null : List.copyOf(versions);
    }
}
