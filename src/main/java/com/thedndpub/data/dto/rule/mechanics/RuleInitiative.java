package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.enums.RuleMechanicKind;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleInitiative(String componentId, RuleMechanicKind kind, String subtype, String scoreConstruction,
        Integer base, String ability, Integer advantageAdjustment, Integer disadvantageAdjustment, String roll,
        Boolean higherRollActsFirst, String tieResolution, Boolean membersChooseOrder,
        Boolean roundEndsAfterAllSidesAct, Boolean multipleSidesOrderedByRoll) implements RuleMechanic {
}
