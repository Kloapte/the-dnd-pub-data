package com.thedndpub.data.mechanics.experimental;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.mechanics.model.ModelChecks;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ExperimentalDiagnosticDto(String code, String severity, String mechanicId, String pointer, String explanation) {
    public ExperimentalDiagnosticDto {
        ModelChecks.text(code, "code");
        ModelChecks.text(severity, "severity");
        if (pointer != null) ModelChecks.pointer(pointer);
        ModelChecks.text(explanation, "explanation");
    }
}
