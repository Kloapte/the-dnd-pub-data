package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleFailedSaveChooseOne(String kind, List<RuleFailedSaveOutcome> outcomes) implements RuleFailedSave {
    public RuleFailedSaveChooseOne {
        outcomes = outcomes == null ? List.of() : List.copyOf(outcomes);
    }
}
