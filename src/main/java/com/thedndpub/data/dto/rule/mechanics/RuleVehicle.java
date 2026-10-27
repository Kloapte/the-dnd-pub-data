package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.enums.RuleMechanicKind;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleVehicleProcedure;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleVehicleUnit;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleVehicle(RuleMechanicKind kind, RuleMechanicSubtype subtype, RuleVehicleProcedure procedure,
        RuleAbilityCheck check, Double distance, RuleVehicleUnit unit, String componentId,
        RuleComponentApplicability appliesWhen)
        implements RuleMechanic {
}
