package com.thedndpub.data.dto.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.action.enums.ActionActor;
import com.thedndpub.data.dto.action.enums.ActionPredicateKind;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ActionConditionPredicate(
        ActionPredicateKind kind,
        ActionActor actor,
        ActionReference condition,
        String range,
        ActionActor target) {
}
