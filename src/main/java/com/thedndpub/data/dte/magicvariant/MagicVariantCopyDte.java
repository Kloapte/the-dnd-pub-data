package com.thedndpub.data.dte.magicvariant;

import java.util.List;

public record MagicVariantCopyDte(
        String name,
        String source,
        Boolean preservePage,
        List<MagicVariantCopyOperationDte> operations) {
    public MagicVariantCopyDte {
        operations = List.copyOf(operations);
    }
}
