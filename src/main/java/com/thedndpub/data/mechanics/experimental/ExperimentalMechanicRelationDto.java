package com.thedndpub.data.mechanics.experimental;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.mechanics.model.ModelChecks;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ExperimentalMechanicRelationDto(String mechanicId, String relationship) {
    public ExperimentalMechanicRelationDto {
        ModelChecks.text(mechanicId, "mechanicId");
        ModelChecks.text(relationship, "relationship");
    }
}
