package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleFlankingHexGrid(String relativePosition, String attackApplicability,
        List<RuleFlankingHexSeparation> targetSizeSeparations) {
    public RuleFlankingHexGrid {
        targetSizeSeparations = targetSizeSeparations == null ? List.of() : List.copyOf(targetSizeSeparations);
    }
}
