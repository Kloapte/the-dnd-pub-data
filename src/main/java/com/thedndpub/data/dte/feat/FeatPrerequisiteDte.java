package com.thedndpub.data.dte.feat;

import java.util.List;

/** One conjunctive requirement group; the containing source list is retained as alternatives. */
public record FeatPrerequisiteDte(
        List<FeatPrerequisiteAbilityDte> ability,
        List<FeatPrerequisiteBackgroundDte> background,
        List<String> campaign,
        List<String> exclusiveFeatCategory,
        List<String> feat,
        List<String> featCategory,
        List<String> feature,
        FeatLevelPrerequisiteDte level,
        String other,
        FeatOtherSummaryDte otherSummary,
        List<FeatPrerequisiteProficiencyDte> proficiency,
        List<FeatPrerequisiteRaceDte> race,
        Boolean spellcasting,
        Boolean spellcasting2020,
        Boolean spellcastingFeature) {
}
