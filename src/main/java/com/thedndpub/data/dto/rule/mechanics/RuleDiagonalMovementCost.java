package com.thedndpub.data.dto.rule.mechanics;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleDiagonalMovementCost(String kind, Integer gridUnitFeet, List<Integer> diagonalPatternFeet,
        Boolean patternContinuesAcrossStraightMovement, Boolean ruleOptional) implements RuleMovementDetails {
    public RuleDiagonalMovementCost { diagonalPatternFeet = diagonalPatternFeet == null ? List.of() : List.copyOf(diagonalPatternFeet); }
}
