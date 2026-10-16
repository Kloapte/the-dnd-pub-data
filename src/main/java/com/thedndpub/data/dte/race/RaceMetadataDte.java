package com.thedndpub.data.dte.race;

import java.util.List;

public record RaceMetadataDte(List<String> internalCopies) {
    public RaceMetadataDte {
        internalCopies = internalCopies == null ? List.of() : List.copyOf(internalCopies);
    }
}
