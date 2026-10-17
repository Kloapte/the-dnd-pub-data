package com.thedndpub.data.dte.encounter;

import java.util.List;

public record EncounterDte(String name, String source, Integer page, List<EncounterTableDte> tables) {
    public EncounterDte {
        tables = tables == null ? null : List.copyOf(tables);
    }
}
