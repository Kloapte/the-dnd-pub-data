package com.thedndpub.data.dto.reward;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RewardSpellGrantDto(
        String ability,
        List<String> abilityChoices,
        List<String> knownSpellIds,
        List<String> innateSpellIds,
        Map<String, List<String>> daily,
        Map<String, List<String>> limited,
        Map<String, List<String>> rest,
        List<String> ritualSpellIds,
        String resourceName) {
    public RewardSpellGrantDto {
        abilityChoices = abilityChoices == null ? List.of() : List.copyOf(abilityChoices);
        knownSpellIds = knownSpellIds == null ? List.of() : List.copyOf(knownSpellIds);
        innateSpellIds = innateSpellIds == null ? List.of() : List.copyOf(innateSpellIds);
        daily = copyMap(daily);
        limited = copyMap(limited);
        rest = copyMap(rest);
        ritualSpellIds = ritualSpellIds == null ? List.of() : List.copyOf(ritualSpellIds);
    }

    private static Map<String, List<String>> copyMap(Map<String, List<String>> values) {
        return values == null ? Map.of() : values.entrySet().stream().collect(Collectors.toUnmodifiableMap(
                Map.Entry::getKey, entry -> List.copyOf(entry.getValue())));
    }
}
