package com.thedndpub.data.dte.spell;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.thedndpub.data.dte.common.entry.EntryDte;

import java.util.List;

/**
 * Faithful typed import representation of a 5eTools spell.
 */
public record SpellDte(
        List<String> abilityCheck,
        List<String> affectsCreatureType,
        List<String> alias,
        List<String> areaTags,
        Boolean basicRules,
        Boolean basicRules2024,
        SpellComponentsDte components,
        List<String> conditionImmune,
        List<String> conditionInflict,
        List<String> damageImmune,
        List<String> damageInflict,
        List<String> damageResist,
        List<String> damageVulnerable,
        List<SpellDurationDte> duration,
        List<EntryDte> entries,
        List<EntryDte> entriesHigherLevel,
        Boolean hasFluff,
        Boolean hasFluffImages,
        Integer level,
        SpellMetaDte meta,
        List<String> miscTags,
        String name,
        List<SpellSourceDte> additionalSources,
        List<SpellSourceDte> otherSources,
        Integer page,
        SpellRangeDte range,
        List<String> referenceSources,
        List<String> reprintedAs,
        List<String> savingThrow,
        @JsonProperty("scalingLevelDice")
        @JsonDeserialize(using = ScalingLevelDiceDeserializer.class)
        List<ScalingLevelDiceDte> scalingLevelDice,
        String school,
        String source,
        List<String> spellAttack,
        @JsonDeserialize(using = SpellCatalogReferenceDeserializer.class)
        SpellCatalogReferenceDte srd,
        @JsonDeserialize(using = SpellCatalogReferenceDeserializer.class)
        SpellCatalogReferenceDte srd52,
        List<SpellCastingTimeDte> time
) {
}
