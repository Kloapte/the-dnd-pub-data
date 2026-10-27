package com.thedndpub.data.dto.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.action.enums.ActionChoiceTriggerKind;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ActionChoiceTrigger(
        ActionChoiceTriggerKind kind,
        String operationId,
        ActionTrigger event) {
}
