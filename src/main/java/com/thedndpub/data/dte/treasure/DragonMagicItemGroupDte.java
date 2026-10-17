package com.thedndpub.data.dte.treasure;

import java.util.List;

public record DragonMagicItemGroupDte(String amount, List<DragonMagicItemTypeRowDte> typeTable) {
    public DragonMagicItemGroupDte {
        typeTable = typeTable == null ? null : List.copyOf(typeTable);
    }
}
