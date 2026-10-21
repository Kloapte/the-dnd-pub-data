package com.thedndpub.data.mechanics.experimental;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.mechanics.model.MechanicContext;
import com.thedndpub.data.mechanics.model.MechanicPayload;
import com.thedndpub.data.mechanics.model.ModelChecks;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ExperimentalMechanicDto(String id, String type, MechanicPayload properties,
                                      MechanicContext context) {
    public ExperimentalMechanicDto {
        ModelChecks.text(id, "id");
        ModelChecks.text(type, "type");
        ModelChecks.required(properties, "properties");
    }
}
