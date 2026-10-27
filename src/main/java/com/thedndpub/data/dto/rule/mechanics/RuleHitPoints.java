package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.enums.RuleMechanicKind;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleHitPointReplacementOrder;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleHitPointRule;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleHitPoints(RuleMechanicKind kind, RuleMechanicSubtype subtype, RuleHitPointRule rule,
        RuleHitPointReplacementOrder replacementOrder,
        String componentId, RuleComponentApplicability appliesWhen) implements RuleMechanic {
}
