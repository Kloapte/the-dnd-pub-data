package com.thedndpub.data.dto.action;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ActionDuration(String endsAt, List<ActionConditionPredicate> endsIf) {

    public ActionDuration {
        endsIf = endsIf == null ? List.of() : List.copyOf(endsIf);
    }
}
