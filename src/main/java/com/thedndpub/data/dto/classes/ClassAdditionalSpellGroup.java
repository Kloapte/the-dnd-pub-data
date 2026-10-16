package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ClassAdditionalSpellGroup(
        String name,
        String ability,
        String resourceName,
        List<ClassSpellGrant> grants) {
    public ClassAdditionalSpellGroup {
        grants = grants == null ? List.of() : List.copyOf(grants);
    }
}
