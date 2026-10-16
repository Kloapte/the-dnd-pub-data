package com.thedndpub.data.dte.magicvariant;

import com.thedndpub.data.dte.item.ItemMetadataDte;

import java.util.List;
import java.util.Map;

public record MagicVariantsFileDte(
        ItemMetadataDte _meta,
        List<MagicVariantDte> magicvariant,
        Map<String, Map<String, List<String>>> linkedLootTables) {
}
