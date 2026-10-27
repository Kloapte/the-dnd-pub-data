package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.enums.RuleMechanicKind;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleDeathProcedure;

import java.util.Objects;
import java.util.regex.Pattern;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleDeath(RuleMechanicKind kind, String subtype, RuleDeathProcedure procedure, Double threshold,
        String componentId, RuleComponentApplicability appliesWhen) implements RuleMechanic {
    private static final Pattern COMPONENT_ID = Pattern.compile("^[a-z][a-z0-9]*(?:-[a-z0-9]+)*$");

    public RuleDeath {
        if (kind != RuleMechanicKind.DAMAGE_HEALTH_STATE || !"DEATH".equals(subtype)) {
            throw new IllegalArgumentException("Death mechanic discriminators are invalid");
        }
        Objects.requireNonNull(procedure, "Death procedure is required");
        Objects.requireNonNull(componentId, "Death componentId is required");
        if (!COMPONENT_ID.matcher(componentId).matches()) throw new IllegalArgumentException("Invalid death componentId");
        if (threshold != null && threshold < 0) throw new IllegalArgumentException("Death threshold must be nonnegative");
    }
}
