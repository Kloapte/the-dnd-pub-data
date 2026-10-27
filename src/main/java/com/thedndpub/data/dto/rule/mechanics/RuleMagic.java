package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.action.ActionAttackRoll;
import com.thedndpub.data.dto.rule.RuleReference;
import com.thedndpub.data.dto.rule.enums.RuleMechanicKind;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleMagicResource;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleMagic(RuleMechanicKind kind, RuleMechanicSubtype subtype, RuleTurnTiming castingTime,
        RuleMagicResource resource,
        Boolean requiresConcentration, RuleReference reference, String componentId, ActionAttackRoll attackRoll,
        RuleComponentApplicability appliesWhen) implements RuleMechanic {
    public RuleMagic {
        if (kind != RuleMechanicKind.SPELL_MAGIC || subtype == null) {
            throw new IllegalArgumentException("Unsupported Spell/Magic mechanic discriminator");
        }
        Objects.requireNonNull(componentId, "Spell/Magic componentId is required");
    }
}
