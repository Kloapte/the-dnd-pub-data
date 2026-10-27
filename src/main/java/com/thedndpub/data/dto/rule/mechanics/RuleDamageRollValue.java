package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleDamageType;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleDamageRollValue(String kind, List<RuleDamageType> damageTypes, RuleDamageRollBase base,
        String abilityModifier) {
    public RuleDamageRollValue {
        damageTypes = damageTypes == null ? List.of() : List.copyOf(damageTypes);
    }
}
