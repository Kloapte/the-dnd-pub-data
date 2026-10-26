package com.thedndpub.data.dto.corerules;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SourceFlags(
        Boolean srd,
        Boolean srd52,
        Boolean basicRules,
        Boolean basicRules2024) {
    @JsonIgnore
    public boolean isEmpty() {
        return srd == null && srd52 == null && basicRules == null && basicRules2024 == null;
    }
}
