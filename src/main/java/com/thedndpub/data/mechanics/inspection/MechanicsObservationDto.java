package com.thedndpub.data.mechanics.inspection;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "kind")
@JsonSubTypes({
        @JsonSubTypes.Type(value = FormulaObservationInspectionDto.class, name = "FORMULA_REFERENCE"),
        @JsonSubTypes.Type(value = StructuredObservationInspectionDto.class, name = "STRUCTURED_DECLARATION"),
        @JsonSubTypes.Type(value = SyntaxObservationInspectionDto.class, name = "OTHER_SYNTAX")
})
public sealed interface MechanicsObservationDto permits FormulaObservationInspectionDto,
        StructuredObservationInspectionDto,
        SyntaxObservationInspectionDto {
}
