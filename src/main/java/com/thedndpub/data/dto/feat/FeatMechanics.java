package com.thedndpub.data.dto.feat;

import java.util.List;

/** Internal, source-authored mechanics, without applying any character effects. */
public record FeatMechanics(
        FeatProficiencies proficiencies,
        FeatDefenses defenses,
        List<FeatSenseValues> senses,
        List<FeatSenseValues> bonusSenses,
        List<FeatOptionalFeatureProgression> optionalFeatureProgressions) {
    public FeatMechanics {
        senses = copy(senses);
        bonusSenses = copy(bonusSenses);
        optionalFeatureProgressions = copy(optionalFeatureProgressions);
    }

    private static <T> List<T> copy(List<T> values) {
        return values == null ? null : List.copyOf(values);
    }
}
