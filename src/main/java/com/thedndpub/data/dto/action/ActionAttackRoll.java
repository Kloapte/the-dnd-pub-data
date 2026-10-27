package com.thedndpub.data.dto.action;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.action.enums.ActionAttackForm;
import com.thedndpub.data.dto.action.enums.ActionAttackTarget;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ActionAttackRoll(
        String kind,
        List<ActionAttackForm> forms,
        ActionAttackTarget target,
        Double reachFeet,
        ActionAttackBonus bonus,
        Integer criticalOn,
        Boolean criticalHitAutomaticallyHits) {

    public ActionAttackRoll {
        forms = forms == null ? List.of() : List.copyOf(forms);
    }
}
