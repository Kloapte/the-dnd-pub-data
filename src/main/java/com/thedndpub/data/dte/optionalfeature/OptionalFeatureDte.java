package com.thedndpub.data.dte.optionalfeature;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thedndpub.data.dte.common.entry.EntryDte;

import java.util.List;

public record OptionalFeatureDte(
        String name,
        String source,
        Integer page,
        List<String> featureType,
        List<OptionalFeaturePrerequisiteDte> prerequisite,
        List<OptionalFeatureSourceReferenceDte> otherSources,
        List<OptionalFeatureReprintDte> reprintedAs,
        List<EntryDte> entries,
        OptionalFeatureConsumeDte consumes,
        List<OptionalFeatureSpellGrantDte> additionalSpells,
        List<OptionalFeatureProficiencyDte> skillProficiencies,
        List<OptionalFeatureSenseGrantDte> senses,
        List<OptionalFeatureProgressionDte> featProgression,
        List<OptionalFeatureProgressionDte> optionalfeatureProgression,
        Boolean isClassFeatureVariant,
        Boolean srd,
        Boolean srd52,
        Boolean hasFluffImages) {
}
