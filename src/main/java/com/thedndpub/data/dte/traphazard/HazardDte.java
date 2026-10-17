package com.thedndpub.data.dte.traphazard;

import com.thedndpub.data.dte.common.entry.EntryDte;

import java.util.List;

public record HazardDte(
        String name,
        String source,
        List<TrapRatingDte> rating,
        List<EntryDte> entries,
        Integer page,
        HazardTypeDte trapHazType,
        List<String> reprintedAs,
        Boolean srd52,
        Boolean hasFluffImages,
        List<TrapSourceReferenceDte> otherSources) {
    public HazardDte {
        rating = copy(rating);
        entries = copy(entries);
        reprintedAs = copy(reprintedAs);
        otherSources = copy(otherSources);
    }

    private static <T> List<T> copy(List<T> values) {
        return values == null ? null : List.copyOf(values);
    }
}
