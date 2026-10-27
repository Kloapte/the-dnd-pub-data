package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleDamageRollValue(String kind, List<String> damageTypes, RuleDamageRollBase base,
        String abilityModifier) {
    public RuleDamageRollValue {
        damageTypes = damageTypes == null ? List.of() : List.copyOf(damageTypes);
    }
}
