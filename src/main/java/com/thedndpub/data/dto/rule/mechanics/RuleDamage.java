package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.enums.RuleMechanicKind;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleDamage(RuleMechanicKind kind, String subtype, List<String> damageTypes,
        Integer damageThreshold, Boolean objectDamage, String componentId, RuleDamageRollValue damageRoll,
        RuleDamageModifier damageModifier, RuleDamageResolutionModifier resolutionModifier,
        RuleComponentApplicability appliesWhen) implements RuleMechanic {
    public RuleDamage {
        damageTypes = damageTypes == null ? List.of() : List.copyOf(damageTypes);
    }
}
