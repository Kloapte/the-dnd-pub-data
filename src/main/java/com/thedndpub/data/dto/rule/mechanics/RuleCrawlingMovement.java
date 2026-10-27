package com.thedndpub.data.dto.rule.mechanics;
import com.fasterxml.jackson.annotation.JsonInclude;
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleCrawlingMovement(String kind, Integer additionalFeetPerFoot,
        Integer difficultTerrainAdditionalFeetPerFoot, String difficultTerrainCostRelationship)
        implements RuleMovementDetails { }
