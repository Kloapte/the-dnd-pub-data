package com.thedndpub.data.dto.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.action.enums.ActionReferenceType;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ActionReference(
        ActionReferenceType targetType,
        String id,
        String relationship,
        String operation) {
}
