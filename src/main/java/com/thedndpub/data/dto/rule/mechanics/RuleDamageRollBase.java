package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = RuleDiceDamageBase.class, name = "DICE"),
        @JsonSubTypes.Type(value = RuleFixedDamageBase.class, name = "FIXED")
})
public sealed interface RuleDamageRollBase permits RuleDiceDamageBase, RuleFixedDamageBase {
    String kind();
}
