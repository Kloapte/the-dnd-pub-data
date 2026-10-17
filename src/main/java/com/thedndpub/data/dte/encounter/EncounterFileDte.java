package com.thedndpub.data.dte.encounter;

import java.util.List;

public record EncounterFileDte(List<EncounterDte> encounter) {
    public EncounterFileDte {
        encounter = encounter == null ? null : List.copyOf(encounter);
    }
}
