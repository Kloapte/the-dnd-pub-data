package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind")
@JsonSubTypes({
        @JsonSubTypes.Type(value = FixedDamageType.class, name = "FIXED"),
        @JsonSubTypes.Type(value = DamageTypeChoice.class, name = "CHOICE"),
        @JsonSubTypes.Type(value = UnknownDamageType.class, name = "UNKNOWN")
})
public sealed interface DamageTypeSelection permits
        FixedDamageType,
        DamageTypeChoice,
        UnknownDamageType {

}
