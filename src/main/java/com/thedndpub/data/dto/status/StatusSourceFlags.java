package com.thedndpub.data.dto.status;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record StatusSourceFlags(Boolean srd, Boolean srd52, Boolean basicRules, Boolean basicRules2024) {
    public StatusSourceFlags {
        if (srd == null && srd52 == null && basicRules == null && basicRules2024 == null) {
            throw new IllegalArgumentException("Status source flags require at least one flag");
        }
    }
}
