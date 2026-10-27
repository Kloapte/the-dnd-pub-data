package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.enums.RuleMechanicKind;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleUnarmedStrike(String componentId, RuleMechanicKind kind, RuleMechanicSubtype subtype) implements RuleMechanic {
}
