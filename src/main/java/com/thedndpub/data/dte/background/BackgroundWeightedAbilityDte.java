package com.thedndpub.data.dte.background;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record BackgroundWeightedAbilityDte(List<String> from, List<Integer> weights) {
    public BackgroundWeightedAbilityDte {
        from = List.copyOf(from);
        weights = List.copyOf(weights);
    }
}
