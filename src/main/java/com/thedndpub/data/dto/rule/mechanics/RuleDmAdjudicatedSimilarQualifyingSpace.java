package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleQualifyingSpaceKind;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleDmAdjudicatedSimilarQualifyingSpace(RuleQualifyingSpaceKind kind) implements RuleQualifyingSpace {
    public RuleDmAdjudicatedSimilarQualifyingSpace {
        if (kind != RuleQualifyingSpaceKind.DM_ADJUDICATED_SIMILAR) {
            throw new IllegalArgumentException("Expected qualifying-space kind DM_ADJUDICATED_SIMILAR");
        }
    }
}
