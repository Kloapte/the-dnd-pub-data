package com.thedndpub.data.mechanics.benchmark;

import com.thedndpub.data.mechanics.model.MechanicFamily;
import com.thedndpub.data.mechanics.model.ModelChecks;

public record BenchmarkProhibition(
        String id, ProhibitionRule rule, MechanicFamily family, String rationale) {
    public BenchmarkProhibition {
        ModelChecks.text(id, "id");
        ModelChecks.required(rule, "rule");
        ModelChecks.require((rule == ProhibitionRule.NO_FAMILY) == (family != null),
                "Only a family prohibition carries a family");
        ModelChecks.text(rationale, "rationale");
    }
}
