package com.thedndpub.data.dte.background;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thedndpub.data.dte.common.entry.EntryDte;

import java.util.List;

public record BackgroundDte(
        String name,
        String source,
        Integer page,
        String edition,
        List<BackgroundAbilityChoiceDte> ability,
        List<BackgroundGrantGroupDte> skillProficiencies,
        List<BackgroundGrantGroupDte> toolProficiencies,
        List<BackgroundGrantGroupDte> languageProficiencies,
        List<BackgroundGrantGroupDte> feats,
        List<BackgroundEquipmentPackageDte> startingEquipment,
        List<BackgroundGrantGroupDte> skillToolLanguageProficiencies,
        List<BackgroundPrerequisiteDte> prerequisite,
        List<BackgroundAdditionalSpellDte> additionalSpells,
        List<EntryDte> entries,
        List<String> reprintedAs,
        List<BackgroundSourceReferenceDte> additionalSources,
        List<BackgroundSourceReferenceDte> otherSources,
        BackgroundFeatureOriginDte fromFeature,
        Boolean hasFluff,
        Boolean hasFluffImages,
        Boolean srd,
        Boolean srd52,
        Boolean basicRules,
        Boolean basicRules2024,
        @JsonProperty("_copy") BackgroundCopyDte copy) {
    public BackgroundDte {
        ability = immutable(ability);
        skillProficiencies = immutable(skillProficiencies);
        toolProficiencies = immutable(toolProficiencies);
        languageProficiencies = immutable(languageProficiencies);
        feats = immutable(feats);
        startingEquipment = immutable(startingEquipment);
        skillToolLanguageProficiencies = immutable(skillToolLanguageProficiencies);
        prerequisite = immutable(prerequisite);
        additionalSpells = immutable(additionalSpells);
        entries = immutable(entries);
        reprintedAs = immutable(reprintedAs);
        additionalSources = immutable(additionalSources);
        otherSources = immutable(otherSources);
    }

    private static <T> List<T> immutable(List<T> values) {
        return values == null ? null : List.copyOf(values);
    }
}
