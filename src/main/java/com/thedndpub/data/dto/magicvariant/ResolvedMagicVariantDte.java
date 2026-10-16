package com.thedndpub.data.dto.magicvariant;

import com.thedndpub.data.dte.magicvariant.MagicVariantCopyOperationDte;
import com.thedndpub.data.dte.magicvariant.MagicVariantDte;

import java.util.List;

public record ResolvedMagicVariantDte(
        MagicVariantDte source,
        String sourceCode,
        String baseItemName,
        String baseItemSource,
        String baseItemId,
        List<String> copyLineage,
        List<MagicVariantCopyOperationDte> resolvedOperations,
        boolean copyChainResolved,
        boolean baseItemResolved) {
    public ResolvedMagicVariantDte {
        copyLineage = List.copyOf(copyLineage);
        resolvedOperations = List.copyOf(resolvedOperations);
    }
}
