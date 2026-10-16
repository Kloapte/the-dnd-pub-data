package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ClassSpellGrant(
        ClassSpellGrantCategory category,
        ClassSpellGate gate,
        ClassSpellUsage usage,
        List<ClassSpellSelection> selections) {
    public ClassSpellGrant {
        selections = selections == null ? List.of() : List.copyOf(selections);
    }
}
