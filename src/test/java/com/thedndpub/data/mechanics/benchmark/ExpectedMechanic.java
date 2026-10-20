package com.thedndpub.data.mechanics.benchmark;

import com.thedndpub.data.mechanics.model.MechanicFamily;
import com.thedndpub.data.mechanics.model.MechanicPayload;
import com.thedndpub.data.mechanics.model.MechanicsCompleteness;
import com.thedndpub.data.mechanics.model.MechanicsJson;
import com.thedndpub.data.mechanics.model.ModelChecks;

import java.util.List;

public record ExpectedMechanic(
        String key,
        MechanicFamily family,
        String patternId,
        MechanicPayload payload,
        ExpectedContext context,
        MechanicsCompleteness completeness,
        List<ExpectedEvidence> evidence) {
    public ExpectedMechanic {
        ModelChecks.text(key, "key");
        ModelChecks.text(patternId, "patternId");
        ModelChecks.required(payload, "payload");
        ModelChecks.required(context, "context");
        ModelChecks.require(family == BenchmarkSemantics.family(payload), "Expected payload/family mismatch");
        ModelChecks.require(completeness != null && completeness != MechanicsCompleteness.UNINTERPRETED,
                "Expected claims must distinguish complete and partial claims");
        evidence = ModelChecks.list(evidence);
        ModelChecks.require(!evidence.isEmpty(), "Expected claim requires evidence");
        var payloadFields = MechanicsJson.mapper().valueToTree(payload);
        var contextFields = MechanicsJson.mapper().valueToTree(context);
        for (ExpectedEvidence association : evidence) {
            String path = association.claimPath();
            boolean exists = path.startsWith("/payload")
                    ? !payloadFields.at(path.substring("/payload".length())).isMissingNode()
                    : !contextFields.at(path.substring("/context".length())).isMissingNode();
            ModelChecks.require(exists, "Expected evidence addresses a nonexistent assertion field");
        }
    }
}
