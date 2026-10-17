package com.thedndpub.data.dte.encounter;

import java.util.List;

public record EncounterTableDte(Integer minlvl, Integer maxlvl, String diceExpression,
        List<EncounterRowDte> table, String caption, String captionPrefix, String captionSuffix,
        List<String> footnotes, Integer page, Boolean rollAttitude) {
    public EncounterTableDte {
        table = table == null ? null : List.copyOf(table);
        footnotes = footnotes == null ? null : List.copyOf(footnotes);
    }
}
