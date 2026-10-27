package com.thedndpub.data.dto.rule;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleSourceFlags(Boolean srd, Boolean srd52, Boolean basicRules, Boolean basicRules2024) {
    public RuleSourceFlags {
        if (srd == null && srd52 == null && basicRules == null && basicRules2024 == null) {
            throw new IllegalArgumentException("Source flags require at least one supplied flag");
        }
    }
}
