package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind")
@JsonSubTypes({
        @JsonSubTypes.Type(value = IntegerAmount.class, name = "INTEGER"),
        @JsonSubTypes.Type(value = DiceAmount.class, name = "DICE"),
        @JsonSubTypes.Type(value = SymbolicAmount.class, name = "SYMBOLIC"),
        @JsonSubTypes.Type(value = SumAmount.class, name = "SUM"),
        @JsonSubTypes.Type(value = UnknownAmount.class, name = "UNKNOWN")
})
public sealed interface AmountExpression permits
        IntegerAmount,
        DiceAmount,
        SymbolicAmount,
        SumAmount,
        UnknownAmount {

}
