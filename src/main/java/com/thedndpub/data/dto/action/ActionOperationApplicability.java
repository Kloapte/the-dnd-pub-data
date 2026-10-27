package com.thedndpub.data.dto.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.action.enums.ActionActor;
import com.thedndpub.data.dto.action.enums.ActionApplicabilityKind;
import com.thedndpub.data.dto.action.enums.ActionExcludedMovementSource;
import com.thedndpub.data.dto.action.enums.ActionMovementOrigin;
import com.thedndpub.data.dto.action.enums.ActionTriggerTarget;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ActionOperationApplicability(
        ActionApplicabilityKind kind,
        String operationId,
        String choiceGroupId,
        ActionConditionPredicate condition,
        ActionTrigger event,
        ActionActor actor,
        ActionTriggerTarget target,
        Boolean requiresVisibility,
        Boolean requiresHostility,
        List<ActionMovementOrigin> movementOrigins,
        List<ActionExcludedMovementSource> excludedMovementSources,
        String resolutionTiming,
        String volume) {

    public ActionOperationApplicability {
        movementOrigins = movementOrigins == null ? List.of() : List.copyOf(movementOrigins);
        excludedMovementSources = excludedMovementSources == null
                ? List.of()
                : List.copyOf(excludedMovementSources);
    }
}
