package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind")
@JsonSubTypes({
        @JsonSubTypes.Type(value = FixedAbility.class, name = "FIXED"),
        @JsonSubTypes.Type(value = AbilityChoice.class, name = "CHOICE"),
        @JsonSubTypes.Type(value = UnknownAbility.class, name = "UNKNOWN")
})
public sealed interface AbilitySelection permits
        FixedAbility,
        AbilityChoice,
        UnknownAbility {

}
