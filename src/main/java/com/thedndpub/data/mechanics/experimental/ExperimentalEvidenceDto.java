package com.thedndpub.data.mechanics.experimental;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.mechanics.model.ModelChecks;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ExperimentalEvidenceDto(
        String id, List<String> mechanicIds, List<String> relationshipIds,
        String sourceResource, String sourceRevision,
        String sourceSha256, String jsonPointer, Integer textStart, Integer textEnd,
        SourceReference source, String role, String recognizer, String recognizerVersion) {
    public ExperimentalEvidenceDto {
        ModelChecks.text(id, "id");
        mechanicIds = mechanicIds.stream().distinct().sorted().toList();
        relationshipIds = relationshipIds.stream().distinct().sorted().toList();
        ModelChecks.text(sourceResource, "sourceResource");
        ModelChecks.text(sourceRevision, "sourceRevision");
        ModelChecks.text(sourceSha256, "sourceSha256");
        ModelChecks.pointer(jsonPointer);
        ModelChecks.required(source, "source");
        ModelChecks.text(role, "role");
        ModelChecks.text(recognizer, "recognizer");
        ModelChecks.text(recognizerVersion, "recognizerVersion");
        ModelChecks.require((textStart == null) == (textEnd == null), "Evidence spans require both bounds");
        if (textStart != null) ModelChecks.require(textStart >= 0 && textEnd >= textStart, "Invalid text span");
    }
}
