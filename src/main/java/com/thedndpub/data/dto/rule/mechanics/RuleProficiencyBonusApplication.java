package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.enums.RuleMechanicKind;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleProficiencyBonusTarget;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleProficiencyBonusApplication(RuleMechanicKind kind, RuleMechanicSubtype subtype, String componentId,
        List<RuleProficiencyBonusTarget> appliesTo, String adds) implements RuleMechanic {
    public RuleProficiencyBonusApplication {
        appliesTo = appliesTo == null ? List.of() : List.copyOf(appliesTo);
    }
}
