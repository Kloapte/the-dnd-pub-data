package com.thedndpub.data.dte.treasure;

import java.util.List;

public record DragonTreasureAmountDte(String amount, List<DragonTreasureTypeRowDte> typeTable) {
    public DragonTreasureAmountDte {
        typeTable = typeTable == null ? null : List.copyOf(typeTable);
    }
}
