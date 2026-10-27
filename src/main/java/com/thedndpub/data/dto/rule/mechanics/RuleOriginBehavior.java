package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleOriginBehavior(String kind, List<String> followsOriginUnless) {
    public RuleOriginBehavior {
        followsOriginUnless = followsOriginUnless == null ? List.of() : List.copyOf(followsOriginUnless);
    }
}
