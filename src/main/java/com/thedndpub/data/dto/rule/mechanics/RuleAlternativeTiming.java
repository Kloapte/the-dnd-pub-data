package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleTurnTimingKind;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleAlternativeTiming(RuleTurnTimingKind kind, List<RuleTurnTiming> options) implements RuleTurnTiming {
    public RuleAlternativeTiming {
        if (kind != RuleTurnTimingKind.ALTERNATIVES) {
            throw new IllegalArgumentException("Alternative timing kind is required");
        }
        Objects.requireNonNull(options, "Alternative timing options are required");
        if (options.isEmpty() || new HashSet<>(options).size() != options.size()) {
            throw new IllegalArgumentException("Alternative timing options must be nonempty and unique");
        }
        options = List.copyOf(options);
    }
}
