package com.thedndpub.data.mechanics.inspection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.thedndpub.data.mechanics.model.MechanicsCoverage;
import com.thedndpub.data.mechanics.model.MechanicsProcessing;
import com.thedndpub.data.mechanics.model.ModelChecks;
import com.thedndpub.data.mechanics.model.ProcessingState;
import com.thedndpub.data.mechanics.model.UnsupportedMechanicsConstruct;
import com.thedndpub.data.mechanics.review.MechanicsReviewSummary;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record SpellMechanicsInspectionDto(
        @JsonProperty(required = true) int schemaVersion,
        MechanicsInspectionProjection projection,
        MechanicsInspectionAvailability availability,
        MechanicsProcessing processing,
        List<MechanicsSourceRevisionDto> sourceRevisions,
        List<MechanicInspectionDto> interpretations,
        List<MechanicsObservationDto> observations,
        List<UnsupportedMechanicsConstruct> unsupported,
        List<EvidenceLocatorDto> evidence,
        MechanicsCoverage coverage,
        MechanicsReviewSummary reviewSummary,
        List<MechanicsInspectionDiagnostic> diagnostics) {
    public SpellMechanicsInspectionDto {
        ModelChecks.require(schemaVersion == 1, "Unsupported inspection schema version");
        ModelChecks.required(projection, "projection");
        ModelChecks.required(availability, "availability");
        ModelChecks.required(processing, "processing");
        ModelChecks.required(coverage, "coverage");
        ModelChecks.required(reviewSummary, "reviewSummary");
        sourceRevisions = ModelChecks.list(sourceRevisions);
        interpretations = ModelChecks.list(interpretations);
        observations = ModelChecks.list(observations);
        unsupported = ModelChecks.list(unsupported);
        evidence = ModelChecks.list(evidence);
        diagnostics = ModelChecks.list(diagnostics);
        boolean failed = processing.state() == ProcessingState.FAILED;
        ModelChecks.require(failed == (availability == MechanicsInspectionAvailability.UNAVAILABLE),
                "Only failed canonical processing is currently an unavailable inspection");
        ModelChecks.require(failed == !diagnostics.isEmpty(), "Failed processing requires a controlled diagnostic");
    }
}
