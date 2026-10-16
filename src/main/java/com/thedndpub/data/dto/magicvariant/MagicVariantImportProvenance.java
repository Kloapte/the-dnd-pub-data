package com.thedndpub.data.dto.magicvariant;

import com.thedndpub.data.dte.magicvariant.MagicVariantDte;

import java.util.List;

public record MagicVariantImportProvenance(
        MagicVariantDte sourceRecord,
        ResolvedMagicVariantDte resolvedRecord,
        boolean sourceSpecificCopy) {
    public List<String> copyLineage() {
        return resolvedRecord.copyLineage();
    }
}
