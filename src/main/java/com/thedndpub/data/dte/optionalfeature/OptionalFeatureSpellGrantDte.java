package com.thedndpub.data.dte.optionalfeature;

import java.util.List;

public record OptionalFeatureSpellGrantDte(
        String ability,
        OptionalFeatureSpellSetDte known,
        OptionalFeatureSpellSetDte prepared,
        OptionalFeatureInnateSpellDte innate,
        String resourceName) {
}
