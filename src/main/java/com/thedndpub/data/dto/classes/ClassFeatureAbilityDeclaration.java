package com.thedndpub.data.dto.classes;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ClassFeatureAbilityDeclaration(
        String kind,
        String name,
        List<String> abilities) implements ClassFeatureNode {
    public ClassFeatureAbilityDeclaration {
        abilities = abilities == null ? List.of() : List.copyOf(abilities);
    }
}
