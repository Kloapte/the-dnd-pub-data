package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleCreatureSize;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleSizeCapacityMultiplier(List<RuleCreatureSize> sizes, Double carryMultiplier,
        Double dragLiftPushMultiplier) {
    public RuleSizeCapacityMultiplier {
        sizes = sizes == null ? List.of() : List.copyOf(sizes);
    }
}
