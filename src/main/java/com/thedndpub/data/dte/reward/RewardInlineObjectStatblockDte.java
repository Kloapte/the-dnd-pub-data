package com.thedndpub.data.dte.reward;

import java.util.List;

public record RewardInlineObjectStatblockDte(
        String name,
        Boolean isNpc,
        String source,
        Integer page,
        List<String> size,
        String objectType,
        String creatureType,
        Integer hp,
        RewardInlineMovementDte speed,
        List<String> immune,
        List<String> conditionImmune) {
    public RewardInlineObjectStatblockDte {
        size = size == null ? null : List.copyOf(size);
        immune = immune == null ? null : List.copyOf(immune);
        conditionImmune = conditionImmune == null ? null : List.copyOf(conditionImmune);
    }
}
