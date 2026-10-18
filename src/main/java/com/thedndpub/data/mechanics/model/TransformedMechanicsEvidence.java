package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record TransformedMechanicsEvidence(
        String id,
        SourceReference source,
        MechanicsOwnerReference effectiveOwner,
        String effectivePointer,
        String valueSha256,
        List<EvidenceRevisionReference> inputs,
        List<MechanicsTransformationReference> transformations) implements MechanicsEvidence {
    public TransformedMechanicsEvidence {
        ModelChecks.text(id, "id");
        ModelChecks.required(source, "source");
        ModelChecks.required(effectiveOwner, "effectiveOwner");
        ModelChecks.pointer(effectivePointer);
        ModelChecks.digest(valueSha256);
        inputs = ModelChecks.list(inputs);
        transformations = ModelChecks.list(transformations);
        ModelChecks.require(!inputs.isEmpty() && !transformations.isEmpty(), "Transformed evidence needs inputs and steps");
        for (MechanicsTransformationReference step : transformations) {
            ModelChecks.require(inputs.stream().anyMatch(input -> input.id().equals(step.instructionEvidenceId())),
                    "Transformation instruction must be a fingerprint-bound input");
        }
    }
}
