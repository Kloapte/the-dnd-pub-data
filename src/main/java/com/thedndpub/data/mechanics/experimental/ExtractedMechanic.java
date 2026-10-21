package com.thedndpub.data.mechanics.experimental;

import com.thedndpub.data.mechanics.model.MechanicContext;
import com.thedndpub.data.mechanics.model.MechanicPayload;
import com.thedndpub.data.mechanics.model.ModelChecks;

public record ExtractedMechanic(
        String id,
        MechanicType type,
        MechanicPayload payload,
        MechanicContext context,
        String sourceFragmentId,
        Integer textStart,
        Integer textEnd) {
    public ExtractedMechanic {
        ModelChecks.text(id, "id");
        ModelChecks.required(type, "type");
        ModelChecks.required(payload, "payload");
        ModelChecks.text(sourceFragmentId, "sourceFragmentId");
        ModelChecks.require((textStart == null) == (textEnd == null), "Source spans require both bounds");
        if (textStart != null) {
            ModelChecks.require(textStart >= 0 && textEnd >= textStart, "Invalid source span");
        }
    }

    public ExtractedMechanic(String id, MechanicType type, MechanicPayload payload,
            MechanicContext context, String sourceFragmentId) {
        this(id, type, payload, context, sourceFragmentId, null, null);
    }
}
