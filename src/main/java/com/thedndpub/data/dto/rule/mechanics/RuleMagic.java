package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.action.ActionAttackRoll;
import com.thedndpub.data.dto.rule.RuleReference;
import com.thedndpub.data.dto.rule.enums.RuleMechanicKind;

import java.util.Objects;
import java.util.regex.Pattern;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleMagic(RuleMechanicKind kind, String subtype, RuleTurnTiming castingTime, String resource,
        Boolean requiresConcentration, RuleReference reference, String componentId, ActionAttackRoll attackRoll,
        RuleComponentApplicability appliesWhen) implements RuleMechanic {
    private static final Pattern COMPONENT_ID = Pattern.compile("^[a-z][a-z0-9]*(?:-[a-z0-9]+)*$");

    public RuleMagic {
        if (kind != RuleMechanicKind.SPELL_MAGIC || subtype == null || !switch (subtype) {
            case "CASTING", "SPELL_ATTACK", "RESOURCE", "ATTUNEMENT", "MAGICAL_EFFECT", "POSSESSION",
                    "TRANSFORMATION" -> true;
            default -> false;
        }) {
            throw new IllegalArgumentException("Unsupported Spell/Magic mechanic discriminator");
        }
        Objects.requireNonNull(componentId, "Spell/Magic componentId is required");
        if (!COMPONENT_ID.matcher(componentId).matches()) {
            throw new IllegalArgumentException("Invalid Spell/Magic componentId: " + componentId);
        }
        if (resource != null && !switch (resource) {
            case "spell-slot", "spell-point", "charge" -> true;
            default -> false;
        }) {
            throw new IllegalArgumentException("Unsupported spell/magic resource: " + resource);
        }
    }
}
