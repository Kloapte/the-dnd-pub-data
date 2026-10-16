package com.thedndpub.data.dte.race;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thedndpub.data.dte.background.BackgroundGrantGroupDte;
import com.thedndpub.data.dte.common.entry.EntryDte;
import com.thedndpub.data.dte.feat.FeatAdditionalSpellDte;
import com.thedndpub.data.dte.feat.FeatResistanceDte;

import java.util.List;
import java.util.Map;

public record SubraceDte(
        String name,
        String source,
        Integer page,
        String raceName,
        String raceSource,
        List<RaceReprintDte> reprintedAs,
        List<RaceSourceReferenceDte> otherSources,
        List<RaceAbilityDte> ability,
        List<EntryDte> entries,
        List<String> traitTags,
        List<BackgroundGrantGroupDte> skillProficiencies,
        List<BackgroundGrantGroupDte> languageProficiencies,
        List<BackgroundGrantGroupDte> toolProficiencies,
        List<BackgroundGrantGroupDte> weaponProficiencies,
        List<BackgroundGrantGroupDte> armorProficiencies,
        List<RaceSkillToolLanguageGrantDte> skillToolLanguageProficiencies,
        List<BackgroundGrantGroupDte> feats,
        List<FeatAdditionalSpellDte> additionalSpells,
        List<FeatResistanceDte> resist,
        Integer darkvision,
        RaceSpeedDte speed,
        RaceAgeDte age,
        RaceHeightAndWeightDte heightAndWeight,
        Map<String, Boolean> overwrite,
        Boolean hasFluff,
        Boolean hasFluffImages,
        Boolean srd,
        Boolean basicRules,
        List<String> alias,
        RaceSoundClipDte soundClip,
        @JsonProperty("_versions") List<RaceVersionDte> versions,
        @JsonProperty("_copy") SubraceCopyDte copy) {
    public SubraceDte {
        reprintedAs = immutable(reprintedAs);
        otherSources = immutable(otherSources);
        ability = immutable(ability);
        entries = immutable(entries);
        traitTags = immutable(traitTags);
        skillProficiencies = immutable(skillProficiencies);
        languageProficiencies = immutable(languageProficiencies);
        toolProficiencies = immutable(toolProficiencies);
        weaponProficiencies = immutable(weaponProficiencies);
        armorProficiencies = immutable(armorProficiencies);
        skillToolLanguageProficiencies = immutable(skillToolLanguageProficiencies);
        feats = immutable(feats);
        additionalSpells = immutable(additionalSpells);
        resist = immutable(resist);
        versions = immutable(versions);
        overwrite = overwrite == null ? null : Map.copyOf(overwrite);
        alias = immutable(alias);
    }

    private static <T> List<T> immutable(List<T> values) {
        return values == null ? null : List.copyOf(values);
    }
}
