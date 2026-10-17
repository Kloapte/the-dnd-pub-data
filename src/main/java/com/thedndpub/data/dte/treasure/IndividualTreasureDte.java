package com.thedndpub.data.dte.treasure;

import java.util.List;

public record IndividualTreasureDte(
        String name,
        String source,
        Integer page,
        Integer crMin,
        Integer crMax,
        List<IndividualTreasureRowDte> table) {
    public IndividualTreasureDte {
        table = table == null ? null : List.copyOf(table);
    }
}
