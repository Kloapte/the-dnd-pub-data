package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleDamageDie;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleDiceDamageBase(String kind, Integer count, RuleDamageDie die) implements RuleDamageRollBase {
}
