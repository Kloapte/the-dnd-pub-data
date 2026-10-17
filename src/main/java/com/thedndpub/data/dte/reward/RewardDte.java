package com.thedndpub.data.dte.reward;

import com.thedndpub.data.dte.common.entry.EntryDte;
import java.util.List;

public record RewardDte(
        String name,
        String source,
        Integer page,
        String type,
        List<EntryDte> entries,
        List<RewardAdditionalSpellDte> additionalSpells,
        List<String> seeAlsoFacility,
        List<RewardReprintDte> reprintedAs,
        String rarity,
        List<RewardSourceReferenceDte> otherSources) {
    public RewardDte {
        entries = entries == null ? null : List.copyOf(entries);
        additionalSpells = additionalSpells == null ? null : List.copyOf(additionalSpells);
        seeAlsoFacility = seeAlsoFacility == null ? null : List.copyOf(seeAlsoFacility);
        reprintedAs = reprintedAs == null ? null : List.copyOf(reprintedAs);
        otherSources = otherSources == null ? null : List.copyOf(otherSources);
    }
}
