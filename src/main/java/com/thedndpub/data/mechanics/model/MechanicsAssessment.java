package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record MechanicsAssessment(
        MechanicsCompleteness completeness,
        MechanicsSupport support,
        List<MechanicsFinding> findings,
        List<MechanicsBenchmarkReference> benchmarks) {
    public MechanicsAssessment {
        ModelChecks.required(completeness, "completeness");
        ModelChecks.required(support, "support");
        findings = ModelChecks.list(findings);
        benchmarks = ModelChecks.list(benchmarks);
    }
}
