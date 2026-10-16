package com.thedndpub.data.dte.magicvariant;

import com.thedndpub.data.dte.common.entry.EntryDte;

public record MagicVariantCopyOperationDte(
        String targetPath,
        MagicVariantCopyOperationKind operation,
        String stringValue,
        Integer integerValue,
        EntryDte entryValue) {
}
