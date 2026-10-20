package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CharacterLevelInstanceCount.class, name = "CHARACTER_LEVEL"),
        @JsonSubTypes.Type(value = SpellSlotInstanceCount.class, name = "SPELL_SLOT")
})
public sealed interface InstanceCountProgression permits CharacterLevelInstanceCount, SpellSlotInstanceCount {
}
