package com.thedndpub.data.mechanics.spell;

import com.thedndpub.data.dto.spell.SpellDefinition;
import com.thedndpub.data.mechanics.model.ModelChecks;

import java.util.List;
import java.util.function.Predicate;

/** Ordered, immutable composition of the currently supported Spell extraction patterns. */
final class SpellMechanicsPatternRegistry {
    private final List<SpellMechanicsPattern> patterns;

    SpellMechanicsPatternRegistry(List<SpellMechanicsPattern> patterns) {
        this.patterns = ModelChecks.list(patterns);
        ModelChecks.require(!this.patterns.isEmpty(), "At least one Spell pattern is required");
        ModelChecks.require(this.patterns.stream().map(SpellMechanicsPattern::patternId).distinct().count()
                == this.patterns.size(), "Spell pattern IDs must be unique");
    }

    static SpellMechanicsPatternRegistry standard() {
        return new SpellMechanicsPatternRegistry(List.of(
                new ShieldMechanicsExtractor(), new PhbSpellMechanicsExtractor(),
                new XgeSteelWindStrikeRules()));
    }

    SpellMechanicsPattern find(SpellDefinition spell) {
        return find(spell, pattern -> pattern.supports(spell));
    }

    SpellMechanicsPattern find(SpellDefinition spell, Predicate<SpellMechanicsPattern> applicable) {
        List<SpellMechanicsPattern> matches = patterns.stream()
                .filter(applicable)
                .toList();
        ModelChecks.require(matches.size() <= 1, "Spell patterns overlap for " + spell.id().value());
        return matches.isEmpty() ? null : matches.getFirst();
    }

}
