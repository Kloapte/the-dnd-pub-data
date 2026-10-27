package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.enums.RuleMechanicKind;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleProficiencyBonusApplication(RuleMechanicKind kind, String subtype, String componentId,
        List<String> appliesTo, String adds) implements RuleMechanic {
    public RuleProficiencyBonusApplication {
        appliesTo = appliesTo == null ? List.of() : List.copyOf(appliesTo);
    }
}
