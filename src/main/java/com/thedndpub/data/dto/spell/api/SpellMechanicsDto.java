package com.thedndpub.data.dto.spell.api;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SpellMechanicsDto(
        List<String> abilityChecks,
        List<String> affectsCreatureTypes,
        List<String> areaTags,
        List<String> conditionImmunities,
        List<String> conditionsInflicted,
        List<String> damageImmunities,
        List<String> damageTypes,
        List<String> damageResistances,
        List<String> damageVulnerabilities,
        List<String> miscellaneousTags,
        List<String> savingThrows,
        List<String> spellAttacks) {
    public SpellMechanicsDto {
        abilityChecks = List.copyOf(abilityChecks);
        affectsCreatureTypes = List.copyOf(affectsCreatureTypes);
        areaTags = List.copyOf(areaTags);
        conditionImmunities = List.copyOf(conditionImmunities);
        conditionsInflicted = List.copyOf(conditionsInflicted);
        damageImmunities = List.copyOf(damageImmunities);
        damageTypes = List.copyOf(damageTypes);
        damageResistances = List.copyOf(damageResistances);
        damageVulnerabilities = List.copyOf(damageVulnerabilities);
        miscellaneousTags = List.copyOf(miscellaneousTags);
        savingThrows = List.copyOf(savingThrows);
        spellAttacks = List.copyOf(spellAttacks);
    }
}
