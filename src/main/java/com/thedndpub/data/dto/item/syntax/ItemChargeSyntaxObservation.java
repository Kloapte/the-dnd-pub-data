package com.thedndpub.data.dto.item.syntax;

import com.thedndpub.data.mechanics.syntax.SyntaxObservation;

import java.util.Objects;

/** Lossless authored syntax with its source role and evidence location. */
public record ItemChargeSyntaxObservation(
        String authoredItemId,
        int occurrenceIndex,
        ItemChargeSyntaxCategory category,
        SyntaxObservation observation) {
    public ItemChargeSyntaxObservation {
        if (authoredItemId == null || authoredItemId.isBlank()) {
            throw new IllegalArgumentException("Authored Item ID is required");
        }
        if (occurrenceIndex < 0) {
            throw new IllegalArgumentException("Occurrence index must not be negative");
        }
        Objects.requireNonNull(category, "category");
        Objects.requireNonNull(observation, "observation");
    }
}
