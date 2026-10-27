package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.enums.RuleMechanicKind;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleSkillAbilityVariation(RuleMechanicKind kind, String subtype, String componentId,
        String decisionMaker, String eligibleRequester, String decision,
        List<RuleSkillAbilityPairing> pairings) implements RuleMechanic {
    public RuleSkillAbilityVariation {
        pairings = pairings == null ? List.of() : List.copyOf(pairings);
    }
}
