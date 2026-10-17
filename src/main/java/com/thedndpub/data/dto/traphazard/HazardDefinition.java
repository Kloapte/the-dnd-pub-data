package com.thedndpub.data.dto.traphazard;

import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dte.common.entry.EntryDte;
import com.thedndpub.data.dte.traphazard.TrapRatingDte;
import com.thedndpub.data.dte.traphazard.TrapSourceReferenceDte;

import java.util.List;

public record HazardDefinition(
        HazardId id,
        String name,
        SourceReference source,
        String type,
        List<TrapRatingDto> rating,
        List<EntryDte> entries,
        Boolean hasFluffImages,
        List<String> reprintedAs,
        Boolean srd52,
        List<TrapSourceReferenceDte> otherSources) {
    public HazardDefinition {
        rating = copy(rating);
        entries = copy(entries);
        reprintedAs = copy(reprintedAs);
        otherSources = copy(otherSources);
    }

    private static <T> List<T> copy(List<T> values) {
        return values == null ? List.of() : List.copyOf(values);
    }
}
