package com.thedndpub.data.dto.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.action.enums.ActionAbility;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ActionInfluenceCheckOption(
        String interaction,
        ActionAbility ability,
        ActionReference skill,
        List<String> targetCreatureTypes,
        Boolean requiresTargetUnderstands) {

    public ActionInfluenceCheckOption {
        targetCreatureTypes = targetCreatureTypes == null ? List.of() : List.copyOf(targetCreatureTypes);
    }
}
