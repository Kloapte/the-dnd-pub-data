package com.thedndpub.data.dto.item;

import com.thedndpub.data.dto.source.SourceReference;

/** Preserves an authored recharge field that has no normalized charge count or amount. */
public record ItemRechargeDeclaration(
        String authoredValue,
        String authoredOwnerName,
        SourceReference authoredSource) {
    public ItemRechargeDeclaration {
        if (authoredOwnerName == null || authoredOwnerName.isBlank()) {
            throw new IllegalArgumentException("An Item recharge declaration requires its authored owner");
        }
        if (authoredSource == null) {
            throw new IllegalArgumentException("An Item recharge declaration requires its authored source");
        }
    }
}
