package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.RuleReference;
import com.thedndpub.data.dto.rule.enums.RuleMechanicKind;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleMovement(RuleMechanicKind kind, String subtype, String procedure, String speedType,
        Double distance, String unit, Double extraFeetPerFoot, Boolean cumulative, String ability,
        RuleReference check, String componentId, RuleComponentApplicability appliesWhen,
        List<RuleQualifyingSpace> qualifyingSpaces, RuleMovementDetails details) implements RuleMechanic {
    public RuleMovement {
        qualifyingSpaces = qualifyingSpaces == null ? List.of() : List.copyOf(qualifyingSpaces);
    }
}
