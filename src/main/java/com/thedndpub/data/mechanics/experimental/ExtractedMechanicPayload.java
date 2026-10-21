package com.thedndpub.data.mechanics.experimental;

import com.thedndpub.data.mechanics.model.MechanicContext;
import com.thedndpub.data.mechanics.model.MechanicPayload;
import com.thedndpub.data.mechanics.model.ModelChecks;

public record ExtractedMechanicPayload(MechanicPayload payload, MechanicContext context, Integer textStart, Integer textEnd) {
    public ExtractedMechanicPayload {
        ModelChecks.required(payload, "payload");
        ModelChecks.require((textStart == null) == (textEnd == null), "Source spans require both bounds");
        if (textStart != null) {
            ModelChecks.require(textStart >= 0 && textEnd >= textStart, "Invalid source span");
        }
    }

    public ExtractedMechanicPayload(MechanicPayload payload, MechanicContext context) {
        this(payload, context, null, null);
    }
}
