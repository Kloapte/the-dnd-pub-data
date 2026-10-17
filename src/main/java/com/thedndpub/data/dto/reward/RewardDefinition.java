package com.thedndpub.data.dto.reward;

import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dte.common.entry.EntryDte;
import com.thedndpub.data.dte.reward.RewardReprintDte;
import com.thedndpub.data.dte.reward.RewardSourceReferenceDte;
import java.util.List;
import java.util.Objects;

public record RewardDefinition(
        RewardId id,
        String name,
        SourceReference source,
        String type,
        String rarity,
        List<RewardSpellBenefit> additionalSpells,
        List<String> seeAlsoFacility,
        List<RewardReprintDte> reprintedAs,
        List<RewardSourceReferenceDte> otherSources,
        List<EntryDte> authoredEntries) {
    public RewardDefinition {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(source, "source");
        Objects.requireNonNull(type, "type");
        additionalSpells = copy(additionalSpells);
        seeAlsoFacility = copy(seeAlsoFacility);
        reprintedAs = copy(reprintedAs);
        otherSources = copy(otherSources);
        authoredEntries = copy(authoredEntries);
    }

    private static <T> List<T> copy(List<T> values) {
        return values == null ? List.of() : List.copyOf(values);
    }
}
