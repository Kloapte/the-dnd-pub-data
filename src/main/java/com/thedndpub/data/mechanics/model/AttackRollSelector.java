package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ReferencedAttackRollSelector.class, name = "REFERENCED_ATTACK_ROLL"),
        @JsonSubTypes.Type(value = FutureAttackRollSelector.class, name = "FUTURE_ATTACK_ROLLS")
})
public sealed interface AttackRollSelector permits
        ReferencedAttackRollSelector,
        FutureAttackRollSelector {

}
