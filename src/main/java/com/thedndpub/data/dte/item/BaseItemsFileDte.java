package com.thedndpub.data.dte.item;

import java.util.List;

public record BaseItemsFileDte(
        ItemMetadataDte _meta,
        List<ItemSourceDte> baseitem,
        List<ItemPropertyDte> itemProperty,
        List<ItemTypeDte> itemType,
        List<ItemTypeAdditionalEntriesDte> itemTypeAdditionalEntries,
        List<ItemEntryTemplateDte> itemEntry,
        List<ItemMasteryDte> itemMastery) {
}
