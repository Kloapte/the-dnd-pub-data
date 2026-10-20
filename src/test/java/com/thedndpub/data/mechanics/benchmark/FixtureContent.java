package com.thedndpub.data.mechanics.benchmark;

import com.thedndpub.data.mechanics.model.MechanicsOwnerReference;
import com.thedndpub.data.mechanics.model.ModelChecks;
import com.thedndpub.data.mechanics.model.SourceArtifactRevision;

import java.util.List;

/** Exact review target. Review decisions are excluded; substantive expectation changes invalidate the hash. */
public record FixtureContent(
        String schemaVersion,
        String id,
        MechanicsOwnerReference owner,
        SourceArtifactRevision source,
        String ownerPointer,
        List<ExpectedMechanic> mechanics,
        List<ExpectedFormula> formulas,
        List<DeferredExpectation> deferred,
        List<BenchmarkProhibition> prohibitions) {
    public FixtureContent {
        ModelChecks.text(schemaVersion, "schemaVersion");
        ModelChecks.text(id, "id");
        ModelChecks.required(owner, "owner");
        ModelChecks.required(source, "source");
        ModelChecks.pointer(ownerPointer);
        mechanics = ModelChecks.list(mechanics);
        formulas = ModelChecks.list(formulas);
        deferred = ModelChecks.list(deferred);
        prohibitions = ModelChecks.list(prohibitions);
    }
}
