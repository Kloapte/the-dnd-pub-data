package com.thedndpub.data.dto.encounter;

import java.util.List;

public record EncounterTable(Integer minLevel, Integer maxLevel, String diceExpression,
        List<EncounterRow> rows, String caption, String captionPrefix, String captionSuffix,
        List<String> footnotes, Integer page, Boolean rollAttitude) {
    public EncounterTable {
        rows = List.copyOf(rows);
        footnotes = List.copyOf(footnotes);
    }
}
