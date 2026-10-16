package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ClassPublicationFlags(
        Boolean srd,
        Boolean srd52,
        Boolean basicRules,
        Boolean basicRules2024) {
}
