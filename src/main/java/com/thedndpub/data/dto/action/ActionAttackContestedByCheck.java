package com.thedndpub.data.dto.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.action.enums.ActionActor;
import com.thedndpub.data.dto.action.enums.ActionOperationKind;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ActionAttackContestedByCheck(
        ActionOperationKind kind,
        String operationId,
        ActionActor attacker,
        ActionAttackRoll attack,
        String target,
        List<ActionAbilitySkillOption> defenderOptions,
        Boolean defenderChooses,
        String successOutcome,
        Boolean attackDamageOnSuccess,
        String defenderCheckSizeModifier,
        Integer attackDisadvantageWhenItemHeldWithAtLeastHands) implements ActionOperation {

    public ActionAttackContestedByCheck {
        defenderOptions = defenderOptions == null ? List.of() : List.copyOf(defenderOptions);
    }
}
