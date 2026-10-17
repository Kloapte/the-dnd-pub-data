package com.thedndpub.data.dto.spell;

/** Source curation flags retained internally, not interpreted as spell mechanics. */
public record SpellImportMetadata(
        Boolean basicRules,
        Boolean basicRules2024,
        Boolean hasFluff,
        Boolean hasFluffImages,
        SpellCatalogReference srd,
        SpellCatalogReference srd52) {
}
