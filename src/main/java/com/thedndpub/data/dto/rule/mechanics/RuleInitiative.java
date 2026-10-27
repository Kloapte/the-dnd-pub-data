package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.action.enums.ActionAbility;
import com.thedndpub.data.dto.rule.enums.RuleMechanicKind;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleInitiativeScoreConstruction;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleInitiativeTieResolution;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleInitiative(String componentId, RuleMechanicKind kind, RuleMechanicSubtype subtype,
        RuleInitiativeScoreConstruction scoreConstruction,
        Integer base, ActionAbility ability, Integer advantageAdjustment,
        Integer disadvantageAdjustment, String roll,
        Boolean higherRollActsFirst, RuleInitiativeTieResolution tieResolution, Boolean membersChooseOrder,
        Boolean roundEndsAfterAllSidesAct, Boolean multipleSidesOrderedByRoll) implements RuleMechanic {
}
