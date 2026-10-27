package com.thedndpub.data.dto.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.action.enums.ActionGameTimeUnit;
import com.thedndpub.data.dto.action.enums.ActionTimingKind;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ActionTiming(
        ActionTimingKind kind,
        ActionTrigger trigger,
        Double amount,
        ActionGameTimeUnit unit,
        List<ActionTiming> options) {

    public ActionTiming {
        options = options == null ? List.of() : List.copyOf(options);
    }
}
