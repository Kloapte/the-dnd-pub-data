package com.thedndpub.data.dto.spell;

import com.thedndpub.data.mechanics.syntax.LabelSyntaxObservation;
import com.thedndpub.data.mechanics.syntax.ModifierSyntaxObservation;
import com.thedndpub.data.mechanics.syntax.SyntaxObservation;

import java.util.Objects;

/** Internal label or modifier syntax retained separately from formula observations. */
public record SpellSyntaxAnnotation(
        SpellId spellId,
        int occurrenceIndex,
        SpellFormulaSection section,
        SyntaxObservation observation) {
    public SpellSyntaxAnnotation {
        Objects.requireNonNull(spellId, "spellId");
        Objects.requireNonNull(section, "section");
        Objects.requireNonNull(observation, "observation");
        if (!(observation instanceof LabelSyntaxObservation)
                && !(observation instanceof ModifierSyntaxObservation)) {
            throw new IllegalArgumentException(
                    "Spell syntax annotations must preserve a label or modifier observation");
        }
        if (occurrenceIndex < 0) {
            throw new IllegalArgumentException("Syntax occurrence index must not be negative");
        }
    }
}
