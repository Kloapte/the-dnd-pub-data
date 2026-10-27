package com.thedndpub.data.dto.action;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ActionInfluenceBranch(
        String willingness,
        Boolean checkRequired,
        String outcome,
        String attitudeInput,
        List<ActionInfluenceCheckOption> checkOptions,
        ActionDifficultyClass dc,
        String onSuccess,
        String onFailure,
        Integer retryAfterHours,
        String retryAlternative) {

    public ActionInfluenceBranch {
        checkOptions = checkOptions == null ? List.of() : List.copyOf(checkOptions);
    }
}
