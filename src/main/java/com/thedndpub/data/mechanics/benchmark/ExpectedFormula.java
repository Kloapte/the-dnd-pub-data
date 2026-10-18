package com.thedndpub.data.mechanics.benchmark;

import com.thedndpub.data.mechanics.model.FormulaObservationReference;
import com.thedndpub.data.mechanics.model.ModelChecks;

/** Syntax control only; never scored as an interpreted mechanic. */
public record ExpectedFormula(FormulaObservationReference reference, String exactToken) {
    public ExpectedFormula {
        ModelChecks.required(reference, "reference");
        ModelChecks.text(exactToken, "exactToken");
    }
}
