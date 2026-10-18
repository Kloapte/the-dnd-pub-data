package com.thedndpub.data.dto.item.syntax;

import com.thedndpub.data.mechanics.syntax.SyntaxSourceOwner;

import java.util.Objects;

/** An authored Item root considered by the syntax index. */
public record ItemChargeSyntaxOwner(
        String itemId,
        String name,
        String source,
        SyntaxSourceOwner evidenceOwner) {
    public ItemChargeSyntaxOwner {
        if (itemId == null || itemId.isBlank()) {
            throw new IllegalArgumentException("Item ID is required");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Item name is required");
        }
        if (source == null || source.isBlank()) {
            throw new IllegalArgumentException("Item source is required");
        }
        Objects.requireNonNull(evidenceOwner, "evidenceOwner");
    }
}
