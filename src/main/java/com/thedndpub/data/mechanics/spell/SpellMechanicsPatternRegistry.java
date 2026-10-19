package com.thedndpub.data.mechanics.spell;

import com.thedndpub.data.dto.spell.SpellDefinition;
import com.thedndpub.data.mechanics.model.MechanicsDocument;
import com.thedndpub.data.mechanics.model.ModelChecks;

import java.util.List;

/** Ordered, immutable composition of the currently supported Spell extraction patterns. */
final class SpellMechanicsPatternRegistry {
    private final List<SpellMechanicsPattern> patterns;

    SpellMechanicsPatternRegistry(List<SpellMechanicsPattern> patterns) {
        this.patterns = ModelChecks.list(patterns);
        ModelChecks.require(!this.patterns.isEmpty(), "At least one Spell pattern is required");
        ModelChecks.require(this.patterns.stream().map(SpellMechanicsPattern::patternId).distinct().count()
                == this.patterns.size(), "Spell pattern IDs must be unique");
    }

    SpellMechanicsPattern find(SpellDefinition spell) {
        return find(spell, false);
    }

    SpellMechanicsPattern findForInventory(SpellDefinition spell) {
        return find(spell, true);
    }

    private SpellMechanicsPattern find(SpellDefinition spell, boolean inventory) {
        List<SpellMechanicsPattern> matches = patterns.stream()
                .filter(pattern -> inventory ? pattern.supportsForInventory(spell) : pattern.supports(spell))
                .toList();
        ModelChecks.require(matches.size() <= 1, "Spell patterns overlap for " + spell.id().value());
        return matches.isEmpty() ? null : matches.getFirst();
    }

    MechanicsDocument extract(
            SpellDefinition spell,
            SpellMechanicsEvidenceSource source,
            MechanicsDocument original) {
        SpellMechanicsPattern pattern = find(spell);
        ModelChecks.require(pattern != null, "No Spell pattern supports " + spell.id().value());
        return pattern.extract(spell, source, original);
    }
}
