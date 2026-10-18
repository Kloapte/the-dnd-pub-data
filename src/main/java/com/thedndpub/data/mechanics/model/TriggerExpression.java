package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind")
@JsonSubTypes({
        @JsonSubTypes.Type(value = AnyTrigger.class, name = "ANY"),
        @JsonSubTypes.Type(value = AllTriggers.class, name = "ALL"),
        @JsonSubTypes.Type(value = HitByAttackTrigger.class, name = "HIT_BY_ATTACK"),
        @JsonSubTypes.Type(value = TargetedBySpellTrigger.class, name = "TARGETED_BY_SPELL")
})
public sealed interface TriggerExpression permits
        AnyTrigger,
        AllTriggers,
        HitByAttackTrigger,
        TargetedBySpellTrigger {

}
