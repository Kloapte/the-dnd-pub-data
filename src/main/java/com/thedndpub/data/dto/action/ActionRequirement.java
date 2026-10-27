package com.thedndpub.data.dto.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.action.enums.ActionAbility;
import com.thedndpub.data.dto.action.enums.ActionRequirementKind;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ActionRequirement(
        ActionRequirementKind kind,
        ActionReference reference,
        ActionAbility ability) {
}
