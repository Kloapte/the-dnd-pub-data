package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleSizeCapacityMultiplier(List<String> sizes, Double carryMultiplier, Double dragLiftPushMultiplier) {
    public RuleSizeCapacityMultiplier {
        sizes = sizes == null ? List.of() : List.copyOf(sizes);
    }
}
