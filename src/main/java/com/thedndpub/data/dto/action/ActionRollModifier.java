package com.thedndpub.data.dto.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.action.enums.ActionActor;
import com.thedndpub.data.dto.action.enums.ActionOperationKind;
import com.thedndpub.data.dto.action.enums.ActionRollModifierTarget;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ActionRollModifier(
        ActionOperationKind kind,
        ActionD20RollModifier modifier,
        ActionActor actor,
        ActionRollModifierTarget target,
        ActionTrigger establishedBy,
        ActionOperationApplicability appliesWhen,
        ActionDuration duration,
        ActionUsageLimit usageLimit,
        ActionReaction reaction,
        ActionConditionPredicate when,
        String operationId) implements ActionOperation {
}
