package com.thedndpub.data.mechanics.experimental;

import com.thedndpub.data.mechanics.model.ModelChecks;

import java.util.List;

public record MechanicCandidate(
        MechanicType type,
        String fragmentId,
        String recognizerId,
        String recognizerVersion,
        List<String> signals,
        String context,
        boolean ambiguous) {
    public MechanicCandidate {
        ModelChecks.required(type, "type");
        ModelChecks.text(fragmentId, "fragmentId");
        ModelChecks.text(recognizerId, "recognizerId");
        ModelChecks.text(recognizerVersion, "recognizerVersion");
        signals = List.copyOf(signals);
        ModelChecks.text(context, "context");
    }

    public MechanicCandidate(MechanicType type, String fragmentId, String recognizerId,
            List<String> signals, String context, boolean ambiguous) {
        this(type, fragmentId, recognizerId, "1", signals, context, ambiguous);
    }
}
