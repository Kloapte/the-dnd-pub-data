package com.thedndpub.data.dte.optionalfeature;

import java.util.List;

public record OptionalFeaturePrerequisiteDte(
        OptionalFeatureLevelPrerequisiteDte level,
        List<String> item,
        String pact,
        List<String> optionalfeature,
        List<OptionalFeatureSpellPrerequisiteDte> spell,
        OptionalFeatureOtherSummaryDte otherSummary) {
}
