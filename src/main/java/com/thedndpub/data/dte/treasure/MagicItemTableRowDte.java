package com.thedndpub.data.dte.treasure;

import java.util.List;

public record MagicItemTableRowDte(
        Integer min,
        Integer max,
        String item,
        Integer spellLevel,
        MagicItemChooseDte choose,
        List<NestedMagicItemRowDte> table) {
    public MagicItemTableRowDte {
        table = table == null ? null : List.copyOf(table);
    }
}
