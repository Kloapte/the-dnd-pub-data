package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind")
@JsonSubTypes({
        @JsonSubTypes.Type(value = StructuredDeclarationReference.class, name = "STRUCTURED_DECLARATION"),
        @JsonSubTypes.Type(value = FormulaObservationReference.class, name = "FORMULA_SYNTAX"),
        @JsonSubTypes.Type(value = OtherSyntaxObservationReference.class, name = "OTHER_SYNTAX")
})
public sealed interface MechanicsObservationReference permits
        StructuredDeclarationReference,
        FormulaObservationReference,
        OtherSyntaxObservationReference {
    String id();
}
