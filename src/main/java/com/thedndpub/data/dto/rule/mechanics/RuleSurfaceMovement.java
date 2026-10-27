package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleSurfaceMovement(String kind, String mode, Integer additionalFeetPerFoot,
        Integer difficultTerrainAdditionalFeetPerFoot, String difficultTerrainCostRelationship,
        String waivedWhenUsingSpeedType, RuleSurfaceMovementCheck optionalCheck) implements RuleMovementDetails {
}
