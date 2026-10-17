package com.thedndpub.data.dte.traphazard;

import com.thedndpub.data.dte.common.entry.EntryDte;

import java.util.List;

public record TrapDte(
        String name,
        String source,
        Integer page,
        TrapTypeDte trapHazType,
        List<TrapRatingDte> rating,
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
        List<TrapStageDte> eActive,
        List<TrapStageDte> eDynamic,
        List<TrapStageDte> eConstant,
        String initiativeNote) {
    public TrapDte {
        rating = copy(rating);
        entries = copy(entries);
        effect = copy(effect);
        trigger = copy(trigger);
        countermeasures = copy(countermeasures);
        reprintedAs = copy(reprintedAs);
        duration = copy(duration);
        eActive = copy(eActive);
        eDynamic = copy(eDynamic);
        eConstant = copy(eConstant);
    }

    private static <T> List<T> copy(List<T> values) {
        return values == null ? null : List.copyOf(values);
    }
}
