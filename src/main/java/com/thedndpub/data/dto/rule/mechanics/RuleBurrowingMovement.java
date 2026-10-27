package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleBurrowingMovement(String kind, List<String> permittedMaterials,
        RuleSolidRockRequirement solidRock) implements RuleMovementDetails {
    public RuleBurrowingMovement { permittedMaterials = permittedMaterials == null ? List.of() : List.copyOf(permittedMaterials); }
}
