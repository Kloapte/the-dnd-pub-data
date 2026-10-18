package com.thedndpub.data.mechanics.benchmark;

import com.thedndpub.data.mechanics.model.MechanicFamily;
import com.thedndpub.data.mechanics.model.ModelChecks;

public record ExtractionPattern(MechanicFamily family, String patternId) {
    public ExtractionPattern {
        ModelChecks.required(family, "family");
        ModelChecks.text(patternId, "patternId");
    }
}
