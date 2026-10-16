package com.thedndpub.data.dte.race;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thedndpub.data.dte.background.BackgroundGrantGroupDte;
import com.thedndpub.data.dte.common.entry.EntryDte;
import com.thedndpub.data.dte.feat.FeatAdditionalSpellDte;
import com.thedndpub.data.dte.feat.FeatResistanceDte;

import java.util.List;

public record RaceDte(
        String name,
        String source,
        Integer page,
        List<String> size,
        RaceSpeedDte speed,
        List<RaceAbilityDte> ability,
        List<String> traitTags,
        List<BackgroundGrantGroupDte> languageProficiencies,
        List<EntryDte> entries,
        List<RaceSourceReferenceDte> otherSources,
        List<RaceSourceReferenceDte> additionalSources,
        List<RaceReprintDte> reprintedAs,
        RaceAgeDte age,
        Boolean hasFluff,
        Boolean hasFluffImages,
        RaceLineageDte lineage,
        List<FeatAdditionalSpellDte> additionalSpells,
        Integer darkvision,
        Integer blindsight,
        List<FeatResistanceDte> resist,
        @JsonProperty("_versions") List<RaceVersionDte> versions,
        RaceHeightAndWeightDte heightAndWeight,
        String edition,
        List<String> creatureTypes,
        EntryDte sizeEntry,
        List<BackgroundGrantGroupDte> skillProficiencies,
        List<String> creatureTypeTags,
        List<BackgroundGrantGroupDte> toolProficiencies,
        List<BackgroundGrantGroupDte> weaponProficiencies,
        List<BackgroundGrantGroupDte> armorProficiencies,
        List<BackgroundGrantGroupDte> skillToolLanguageProficiencies,
        List<BackgroundGrantGroupDte> feats,
        List<String> conditionImmune,
        List<String> immune,
        List<String> vulnerable,
        List<String> alias,
        Boolean srd,
        Boolean srd52,
        Boolean basicRules,
        Boolean basicRules2024,
        RaceSoundClipDte soundClip,
        @JsonProperty("_copy") RaceCopyDte copy) {
    public RaceDte {
        size = immutable(size);
        ability = immutable(ability);
        traitTags = immutable(traitTags);
        languageProficiencies = immutable(languageProficiencies);
        entries = immutable(entries);
        otherSources = immutable(otherSources);
        additionalSources = immutable(additionalSources);
        reprintedAs = immutable(reprintedAs);
        additionalSpells = immutable(additionalSpells);
        resist = immutable(resist);
        versions = immutable(versions);
        creatureTypes = immutable(creatureTypes);
        skillProficiencies = immutable(skillProficiencies);
        creatureTypeTags = immutable(creatureTypeTags);
        toolProficiencies = immutable(toolProficiencies);
        weaponProficiencies = immutable(weaponProficiencies);
        armorProficiencies = immutable(armorProficiencies);
        skillToolLanguageProficiencies = immutable(skillToolLanguageProficiencies);
        feats = immutable(feats);
        conditionImmune = immutable(conditionImmune);
        immune = immutable(immune);
        vulnerable = immutable(vulnerable);
        alias = immutable(alias);
    }

    private static <T> List<T> immutable(List<T> values) {
        return values == null ? null : List.copyOf(values);
    }
}
