package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ClassSpellUsage(
        ClassSpellUsageKind kind,
        Integer count,
        String ability,
        Boolean each) {
}
