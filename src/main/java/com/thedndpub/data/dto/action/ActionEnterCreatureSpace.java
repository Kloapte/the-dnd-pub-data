package com.thedndpub.data.dto.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.action.enums.ActionActor;
import com.thedndpub.data.dto.action.enums.ActionOperationKind;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ActionEnterCreatureSpace(
        ActionOperationKind kind,
        String operationId,
        ActionActor actor,
        String target,
        String positioning,
        String entryCheckId,
        Boolean movesWithTarget,
        String movementCost,
        String movementWithinSpace,
        String attackModifier,
        String counterattack,
        ActionOperationApplicability appliesWhen,
        ActionDislodgingProcedure dislodgingProcedure) implements ActionOperation {
}
