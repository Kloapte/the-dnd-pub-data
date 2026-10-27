package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.enums.RuleMechanicKind;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleDeathProcedure;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleDeath(RuleMechanicKind kind, RuleMechanicSubtype subtype, RuleDeathProcedure procedure, Double threshold,
        String componentId, RuleComponentApplicability appliesWhen) implements RuleMechanic {
    public RuleDeath {
        if (kind != RuleMechanicKind.DAMAGE_HEALTH_STATE || subtype != RuleMechanicSubtype.DEATH) {
            throw new IllegalArgumentException("Death mechanic discriminators are invalid");
        }
        Objects.requireNonNull(procedure, "Death procedure is required");
        Objects.requireNonNull(componentId, "Death componentId is required");
    }
}
