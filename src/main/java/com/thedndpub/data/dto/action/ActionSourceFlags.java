package com.thedndpub.data.dto.action;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ActionSourceFlags(
        Boolean srd,
        Boolean srd52,
        Boolean basicRules,
        Boolean basicRules2024) {
}
