package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.enums.RuleMechanicKind;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleProficiencyBonusProgression(RuleMechanicKind kind, String subtype, String componentId,
        String progressionSource, List<RuleProficiencyLevelBand> levelBands,
        List<RuleProficiencyChallengeRatingBand> challengeRatingBands) implements RuleMechanic {
    public RuleProficiencyBonusProgression {
        levelBands = levelBands == null ? List.of() : List.copyOf(levelBands);
        challengeRatingBands = challengeRatingBands == null ? List.of() : List.copyOf(challengeRatingBands);
    }
}
