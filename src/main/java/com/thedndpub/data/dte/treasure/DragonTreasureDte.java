package com.thedndpub.data.dte.treasure;

import java.util.List;

public record DragonTreasureDte(
        String name,
        String source,
        Integer page,
        CoinAmountsDte coins,
        DragonMundaneItemAmountDte dragonMundaneItems,
        DragonTreasureAmountDte gems,
        DragonTreasureAmountDte artObjects,
        List<DragonMagicItemGroupDte> magicItems) {
    public DragonTreasureDte {
        magicItems = magicItems == null ? null : List.copyOf(magicItems);
    }
}
