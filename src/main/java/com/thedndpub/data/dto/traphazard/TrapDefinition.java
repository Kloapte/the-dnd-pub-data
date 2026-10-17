package com.thedndpub.data.dto.traphazard;

import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dte.common.entry.EntryDte;
import com.thedndpub.data.dte.traphazard.TrapDurationDte;
import com.thedndpub.data.dte.traphazard.TrapRatingDte;
import com.thedndpub.data.dte.traphazard.TrapStageDte;

import java.util.List;

public record TrapDefinition(
        TrapId id,
        String name,
        SourceReference source,
        String type,
        List<TrapRatingDto> rating,
        List<EntryDte> entries,
        Boolean hasFluffImages,
        List<String> effect,
        List<String> trigger,
        List<TrapStageDte> countermeasures,
        Boolean srd,
        List<String> reprintedAs,
        Boolean srd52,
        List<TrapDurationDte> duration,
        String hauntBonus,
        Integer initiative,
        List<TrapStageDte> activeStages,
        List<TrapStageDte> dynamicStages,
        List<TrapStageDte> constantStages,
        String initiativeNote) {
    public TrapDefinition {
        rating = copy(rating);
        entries = copy(entries);
        effect = copy(effect);
        trigger = copy(trigger);
        countermeasures = copy(countermeasures);
        reprintedAs = copy(reprintedAs);
        duration = copy(duration);
        activeStages = copy(activeStages);
        dynamicStages = copy(dynamicStages);
        constantStages = copy(constantStages);
    }

    public List<String> durationTypes() {
        return duration.stream().map(TrapDurationDte::type).toList();
    }

    private static <T> List<T> copy(List<T> values) {
        return values == null ? List.of() : List.copyOf(values);
    }
}
