package com.thedndpub.data.dto.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.action.enums.ActionGameTimeUnit;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ActionLongCastingWhen(String kind, Integer amount, ActionGameTimeUnit unit) {
}
