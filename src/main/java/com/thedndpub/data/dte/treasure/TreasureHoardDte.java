package com.thedndpub.data.dte.treasure;

import java.util.List;

public record TreasureHoardDte(
        String name,
        String source,
        Integer page,
        Integer crMin,
        Integer crMax,
        CoinAmountsDte coins,
        List<HoardTreasureRowDte> table) {
    public TreasureHoardDte {
        table = table == null ? null : List.copyOf(table);
    }
}
