package com.thedndpub.data.dto.sense;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SenseSourceFlags(Boolean srd, Boolean srd52, Boolean basicRules, Boolean basicRules2024) {
    public SenseSourceFlags {
        if (srd == null && srd52 == null && basicRules == null && basicRules2024 == null) {
            throw new IllegalArgumentException("Sense source flags require at least one flag");
        }
    }
}
