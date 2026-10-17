package com.thedndpub.data.dto.spell;

import java.util.List;

/** Source-authored classifications; these values do not execute spell effects. */
public record SpellMechanics(
        List<String> abilityChecks,
        List<String> affectsCreatureTypes,
        List<String> areaTags,
        List<String> conditionImmunities,
        List<String> conditionsInflicted,
        List<String> damageImmunities,
        List<String> damageInflicted,
        List<String> damageResistances,
        List<String> damageVulnerabilities,
        List<String> miscellaneousTags,
        List<String> savingThrows,
        List<String> spellAttacks) {
    public SpellMechanics {
        abilityChecks = List.copyOf(abilityChecks);
        affectsCreatureTypes = List.copyOf(affectsCreatureTypes);
        areaTags = List.copyOf(areaTags);
        conditionImmunities = List.copyOf(conditionImmunities);
        conditionsInflicted = List.copyOf(conditionsInflicted);
        damageImmunities = List.copyOf(damageImmunities);
        damageInflicted = List.copyOf(damageInflicted);
        damageResistances = List.copyOf(damageResistances);
        damageVulnerabilities = List.copyOf(damageVulnerabilities);
        miscellaneousTags = List.copyOf(miscellaneousTags);
        savingThrows = List.copyOf(savingThrows);
        spellAttacks = List.copyOf(spellAttacks);
    }
}
