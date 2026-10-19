package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record AppliesWhen(
        ApplicabilityCondition condition,
        List<String> evidenceIds) implements MechanicContextBinding {
    public AppliesWhen {
        ModelChecks.required(condition, "condition");
        evidenceIds = ModelChecks.list(evidenceIds);
        ModelChecks.require(!evidenceIds.isEmpty(), "Applicability conditions require source evidence");
    }
}
