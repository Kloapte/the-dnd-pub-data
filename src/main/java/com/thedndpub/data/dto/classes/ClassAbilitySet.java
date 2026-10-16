package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ClassAbilitySet(
        List<String> abilities) {
    public ClassAbilitySet {
        abilities = abilities == null ? List.of() : List.copyOf(abilities);
    }
}
