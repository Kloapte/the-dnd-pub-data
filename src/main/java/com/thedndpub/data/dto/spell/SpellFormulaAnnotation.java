package com.thedndpub.data.dto.spell;

import com.thedndpub.data.mechanics.syntax.SyntaxObservation;

import java.util.Objects;

/** Internal source-syntax observation associated with an existing Spell identity. */
public record SpellFormulaAnnotation(
        SpellId spellId,
        int occurrenceIndex,
        SpellFormulaSection section,
        SyntaxObservation observation) {
    public SpellFormulaAnnotation {
        Objects.requireNonNull(spellId, "spellId");
        Objects.requireNonNull(section, "section");
        Objects.requireNonNull(observation, "observation");
        if (occurrenceIndex < 0) {
            throw new IllegalArgumentException("Formula occurrence index must not be negative");
        }
    }
}
