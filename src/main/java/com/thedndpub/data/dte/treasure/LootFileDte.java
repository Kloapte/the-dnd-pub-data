package com.thedndpub.data.dte.treasure;

import java.util.List;

public record LootFileDte(
        List<IndividualTreasureDte> individual,
        List<TreasureHoardDte> hoard,
        List<DragonTreasureDte> dragon,
        List<GemTableDte> gems,
        List<ArtObjectTableDte> artObjects,
        List<MagicItemTableDte> magicItems,
        List<DragonMundaneItemRowDte> dragonMundaneItems) {
    public LootFileDte {
        individual = copy(individual);
        hoard = copy(hoard);
        dragon = copy(dragon);
        gems = copy(gems);
        artObjects = copy(artObjects);
        magicItems = copy(magicItems);
        dragonMundaneItems = copy(dragonMundaneItems);
    }

    private static <T> List<T> copy(List<T> values) {
        return values == null ? null : List.copyOf(values);
    }
}
