package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.enums.RuleMechanicKind;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleProficiencyDiceAlternative(RuleMechanicKind kind, String subtype, String componentId,
        String replaces, List<String> appliesTo, List<String> eligibleCreatureKinds,
        List<RuleProficiencyDieLevelBand> levelBands, Integer expertiseDiceCount) implements RuleMechanic {
    public RuleProficiencyDiceAlternative {
        appliesTo = appliesTo == null ? List.of() : List.copyOf(appliesTo);
        eligibleCreatureKinds = eligibleCreatureKinds == null ? List.of() : List.copyOf(eligibleCreatureKinds);
        levelBands = levelBands == null ? List.of() : List.copyOf(levelBands);
    }
}
