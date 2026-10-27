package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleSurfaceMovementMode;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleSurfaceSpeedType;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleSurfaceMovement(String kind, RuleSurfaceMovementMode mode, Integer additionalFeetPerFoot,
        Integer difficultTerrainAdditionalFeetPerFoot, String difficultTerrainCostRelationship,
        RuleSurfaceSpeedType waivedWhenUsingSpeedType, RuleSurfaceMovementCheck optionalCheck)
        implements RuleMovementDetails {
}
