package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record TargetedBySpellTrigger(
        SpellMentionReference spell) implements TriggerExpression {
    public TargetedBySpellTrigger {
        ModelChecks.required(spell, "spell");
    }
}
