package com.thedndpub.data.dto.rule.mechanics;
import com.fasterxml.jackson.annotation.JsonInclude;
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleSpecialSpeedAdjustment(String kind, String affectedSpeedTypes,
        String changeAmountRelationship, String durationRelationship) implements RuleMovementDetails { }
