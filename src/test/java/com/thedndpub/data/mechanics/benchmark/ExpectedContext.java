package com.thedndpub.data.mechanics.benchmark;

import com.thedndpub.data.mechanics.model.ModelChecks;
import com.thedndpub.data.mechanics.model.SubjectReference;

import java.util.List;

public record ExpectedContext(SubjectReference subject, List<ExpectedBinding> bindings) {
    public ExpectedContext {
        ModelChecks.required(subject, "subject");
        bindings = ModelChecks.list(bindings);
    }
}
