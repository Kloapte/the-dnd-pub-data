package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleBurrowingMaterial;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleBurrowingMovement(String kind, List<RuleBurrowingMaterial> permittedMaterials,
        RuleSolidRockRequirement solidRock) implements RuleMovementDetails {
    public RuleBurrowingMovement {
        permittedMaterials = permittedMaterials == null ? List.of() : List.copyOf(permittedMaterials);
    }
}
