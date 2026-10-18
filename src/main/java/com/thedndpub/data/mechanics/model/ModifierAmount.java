package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind")
@JsonSubTypes({
        @JsonSubTypes.Type(value = IntegerModifier.class, name = "INTEGER_MODIFIER"),
        @JsonSubTypes.Type(value = SymbolicModifier.class, name = "SYMBOLIC_MODIFIER")
})
public sealed interface ModifierAmount permits
        IntegerModifier,
        SymbolicModifier {

}
