package com.thedndpub.data.dte.spell;

/**
 * Normalized SRD field: true indicates inclusion; a string is an alternate spell name.
 */
public record SpellCatalogReferenceDte(Boolean listed, String alternateName) {
}
