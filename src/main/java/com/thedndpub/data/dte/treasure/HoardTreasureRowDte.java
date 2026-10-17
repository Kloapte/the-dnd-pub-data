package com.thedndpub.data.dte.treasure;

import java.util.List;

public record HoardTreasureRowDte(
        Integer min,
        Integer max,
        TreasureAmountDte gems,
        TreasureAmountDte artObjects,
        List<MagicItemAmountDte> magicItems) {
    public HoardTreasureRowDte {
        magicItems = magicItems == null ? null : List.copyOf(magicItems);
    }
}
