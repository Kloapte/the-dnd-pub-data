package com.thedndpub.data.dto.nametable;

import java.util.List;
import java.util.Objects;

public record NameTableOption(String option, String diceExpression, List<NameTableRow> rows) {
    public NameTableOption {
        Objects.requireNonNull(option, "option");
        Objects.requireNonNull(diceExpression, "diceExpression");
        rows = List.copyOf(rows);
    }
}
