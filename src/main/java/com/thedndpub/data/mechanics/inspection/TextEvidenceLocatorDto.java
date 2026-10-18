package com.thedndpub.data.mechanics.inspection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.mechanics.model.MechanicsOrigin;
import com.thedndpub.data.mechanics.model.ModelChecks;
import com.thedndpub.data.mechanics.syntax.SyntaxSpan;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record TextEvidenceLocatorDto(
        String id,
        SourceReference source,
        String sourceRevisionId,
        String ownerPointer,
        String jsonPointer,
        MechanicsOrigin origin,
        SyntaxSpan span) implements EvidenceLocatorDto {
    public TextEvidenceLocatorDto {
        ModelChecks.text(id, "id");
        ModelChecks.text(sourceRevisionId, "sourceRevisionId");
        ModelChecks.pointer(ownerPointer);
        ModelChecks.pointer(jsonPointer);
        ModelChecks.required(origin, "origin");
        ModelChecks.required(span, "span");
    }
}
