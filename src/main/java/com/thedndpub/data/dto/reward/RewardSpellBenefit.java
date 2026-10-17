package com.thedndpub.data.dto.reward;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public record RewardSpellBenefit(
        String ability,
        List<String> abilityChoices,
        List<String> knownSpellIds,
        List<String> innateSpellIds,
        Map<String, List<String>> daily,
        Map<String, List<String>> limited,
        Map<String, List<String>> rest,
        List<String> ritualSpellIds,
        String resourceName) {
    public RewardSpellBenefit {
        abilityChoices = copy(abilityChoices);
        knownSpellIds = copy(knownSpellIds);
        innateSpellIds = copy(innateSpellIds);
        daily = copyMap(daily);
        limited = copyMap(limited);
        rest = copyMap(rest);
        ritualSpellIds = copy(ritualSpellIds);
    }

    private static List<String> copy(List<String> values) {
        return values == null ? List.of() : List.copyOf(values);
    }

    private static Map<String, List<String>> copyMap(Map<String, List<String>> values) {
        return values == null ? Map.of() : values.entrySet().stream().collect(Collectors.toUnmodifiableMap(
                Map.Entry::getKey, entry -> List.copyOf(entry.getValue())));
    }
}
