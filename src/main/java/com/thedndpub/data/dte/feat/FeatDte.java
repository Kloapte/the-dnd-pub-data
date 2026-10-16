package com.thedndpub.data.dte.feat;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thedndpub.data.dte.common.entry.EntryDte;

import java.util.List;

public record FeatDte(
        String name,
        String source,
        Integer page,
        String category,
        List<FeatPrerequisiteDte> prerequisite,
        List<FeatAbilityIncreaseDte> ability,
        List<FeatSourceReferenceDte> additionalSources,
        List<FeatAdditionalSpellDte> additionalSpells,
        List<FeatGrantDte> skillProficiencies,
        List<FeatGrantDte> expertise,
        List<FeatGrantDte> languageProficiencies,
        List<FeatGrantDte> toolProficiencies,
        List<FeatGrantDte> weaponProficiencies,
        List<FeatGrantDte> armorProficiencies,
        List<FeatGrantDte> savingThrowProficiencies,
        List<FeatSkillToolLanguageChoiceDte> skillToolLanguageProficiencies,
        List<String> conditionImmune,
        List<String> immune,
        List<FeatResistanceDte> resist,
        List<FeatSenseGrantDte> senses,
        List<FeatSenseGrantDte> bonusSenses,
        List<FeatOptionalFeatureProgressionDte> optionalfeatureProgression,
        List<String> traitTags,
        List<EntryDte> entries,
        List<String> reprintedAs,
        Boolean repeatable,
        Boolean repeatableHidden,
        Boolean srd,
        Boolean srd52,
        Boolean basicRules2024,
        Boolean hasFluffImages,
        @JsonProperty("_versions") List<FeatVersionDte> versions) {
}
