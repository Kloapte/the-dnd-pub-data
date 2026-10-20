package com.thedndpub.data.mechanics.benchmark;

import com.thedndpub.data.mechanics.model.MechanicFamily;
import com.thedndpub.data.mechanics.model.MechanicsEvidence;
import com.thedndpub.data.mechanics.model.ModelChecks;
import com.thedndpub.data.mechanics.model.UnsupportedReason;

import java.util.List;

/** Reviewable bounded assertion for a concept not yet representable as a complete canonical payload. */
public record DeferredExpectation(
        String id,
        MechanicFamily candidateFamily,
        String concept,
        String proposedConstraint,
        String adjudicationQuestion,
        UnsupportedReason expectedReason,
        List<MechanicsEvidence> evidence) {
    public DeferredExpectation {
        ModelChecks.text(id, "id");
        ModelChecks.required(candidateFamily, "candidateFamily");
        ModelChecks.text(concept, "concept");
        ModelChecks.text(proposedConstraint, "proposedConstraint");
        ModelChecks.text(adjudicationQuestion, "adjudicationQuestion");
        ModelChecks.required(expectedReason, "expectedReason");
        evidence = ModelChecks.list(evidence);
        ModelChecks.require(!evidence.isEmpty(), "Deferred expectations require evidence");
    }
}
