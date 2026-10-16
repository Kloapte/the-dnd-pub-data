package com.thedndpub.data.dte.magicvariant;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.thedndpub.data.dte.common.entry.EntryDte;
import com.thedndpub.data.dte.item.ItemBooleanTextDte;
import com.thedndpub.data.dte.item.ItemSourceLocatorDte;

import java.util.List;

public record MagicVariantDte(
        String name,
        String source,
        String type,
        String edition,
        List<String> referenceSources,
        List<MagicVariantRequirementDte> requires,
        MagicVariantRequirementDte excludes,
        Boolean ammo,
        MagicVariantInheritanceDte inherits,
        List<EntryDte> entries,
        Boolean hasFluffImages,
        @JsonProperty("_copy") @JsonDeserialize(using = MagicVariantCopyDeserializer.class)
        MagicVariantCopyDte copy) {
}
