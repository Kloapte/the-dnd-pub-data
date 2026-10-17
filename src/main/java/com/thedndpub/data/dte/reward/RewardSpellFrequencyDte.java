package com.thedndpub.data.dte.reward;

import java.util.List;
import java.util.Map;

public record RewardSpellFrequencyDte(
        Map<String, List<String>> daily,
        Map<String, List<String>> limited,
        Map<String, List<String>> rest,
        List<String> ritual) implements RewardSpellCollectionDte {
    public RewardSpellFrequencyDte {
        daily = immutable(daily);
        limited = immutable(limited);
        rest = immutable(rest);
        ritual = ritual == null ? null : List.copyOf(ritual);
    }

    private static Map<String, List<String>> immutable(Map<String, List<String>> values) {
        if (values == null) {
            return null;
        }
        return values.entrySet().stream().collect(java.util.stream.Collectors.toUnmodifiableMap(
                Map.Entry::getKey, entry -> List.copyOf(entry.getValue())));
    }
}
