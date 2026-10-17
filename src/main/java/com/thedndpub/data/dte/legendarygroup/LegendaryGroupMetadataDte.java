package com.thedndpub.data.dte.legendarygroup;

import java.util.List;

public record LegendaryGroupMetadataDte(List<String> internalCopies) {
    public LegendaryGroupMetadataDte {
        internalCopies = internalCopies == null ? List.of() : List.copyOf(internalCopies);
    }
}
