package com.thedndpub.data.dte.nametable;

import java.util.List;

public record NameTableOptionDte(String option, String diceExpression, List<NameTableRowDte> table) {
    public NameTableOptionDte {
        table = table == null ? null : List.copyOf(table);
    }
}
