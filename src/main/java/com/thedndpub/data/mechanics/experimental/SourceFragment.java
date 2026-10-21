package com.thedndpub.data.mechanics.experimental;

import com.thedndpub.data.mechanics.model.ModelChecks;
import com.thedndpub.data.mechanics.model.SourceArtifactRevision;

import java.util.List;

public record SourceFragment(
        String id,
        String pointer,
        Kind kind,
        String text,
        List<String> hierarchy,
        List<MechanicType> signals,
        SourceArtifactRevision sourceRevision) {
    public enum Kind { STRUCTURED_FIELD, AUTHORED_TEXT }

    public SourceFragment {
        ModelChecks.text(id, "id");
        ModelChecks.pointer(pointer);
        ModelChecks.required(kind, "kind");
        ModelChecks.text(text, "text");
        hierarchy = List.copyOf(hierarchy);
        signals = signals.stream().distinct().sorted().toList();
    }

    public SourceFragment(String id, String pointer, Kind kind, String text,
            List<String> hierarchy, List<MechanicType> signals) {
        this(id, pointer, kind, text, hierarchy, signals, null);
    }
}
