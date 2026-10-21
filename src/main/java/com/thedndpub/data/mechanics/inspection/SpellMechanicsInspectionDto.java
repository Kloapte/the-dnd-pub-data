package com.thedndpub.data.mechanics.inspection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.thedndpub.data.mechanics.model.MechanicsCoverage;
import com.thedndpub.data.mechanics.model.MechanicsProcessing;
import com.thedndpub.data.mechanics.model.ModelChecks;
import com.thedndpub.data.mechanics.model.ProcessingState;
import com.thedndpub.data.mechanics.model.UnsupportedMechanicsConstruct;
import com.thedndpub.data.mechanics.review.MechanicsReviewSummary;
import com.thedndpub.data.mechanics.experimental.ExperimentalMechanicsV2Dto;

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
        List<MechanicsInspectionDiagnostic> diagnostics,
        Boolean readyForHumanReviewWithinScope,
        ExperimentalSpellMechanicsDto experimentalInterpretation,
        String origin,
        ExperimentalMechanicsV2Dto experimentalInterpretationV2) {
    public SpellMechanicsInspectionDto(int schemaVersion, MechanicsInspectionProjection projection,
                                       MechanicsInspectionAvailability availability, MechanicsProcessing processing,
                                       List<MechanicsSourceRevisionDto> sourceRevisions,
                                       List<MechanicInspectionDto> interpretations,
                                       List<MechanicsObservationDto> observations,
                                       List<UnsupportedMechanicsConstruct> unsupported,
                                       List<EvidenceLocatorDto> evidence, MechanicsCoverage coverage,
                                       MechanicsReviewSummary reviewSummary,
                                       List<MechanicsInspectionDiagnostic> diagnostics) {
        this(schemaVersion, projection, availability, processing, sourceRevisions, interpretations, observations,
                unsupported, evidence, coverage, reviewSummary, diagnostics, null, null, "CANONICAL", null);
    }

    public SpellMechanicsInspectionDto(int schemaVersion, MechanicsInspectionProjection projection,
                                       MechanicsInspectionAvailability availability, MechanicsProcessing processing,
                                       List<MechanicsSourceRevisionDto> sourceRevisions,
                                       List<MechanicInspectionDto> interpretations,
                                       List<MechanicsObservationDto> observations,
                                       List<UnsupportedMechanicsConstruct> unsupported,
                                       List<EvidenceLocatorDto> evidence, MechanicsCoverage coverage,
                                       MechanicsReviewSummary reviewSummary,
                                       List<MechanicsInspectionDiagnostic> diagnostics,
                                       Boolean readyForHumanReviewWithinScope) {
        this(schemaVersion, projection, availability, processing, sourceRevisions, interpretations, observations,
                unsupported, evidence, coverage, reviewSummary, diagnostics, readyForHumanReviewWithinScope,
                null, "CANONICAL", null);
    }

    public SpellMechanicsInspectionDto(int schemaVersion, MechanicsInspectionProjection projection,
                                       MechanicsInspectionAvailability availability, MechanicsProcessing processing,
                                       List<MechanicsSourceRevisionDto> sourceRevisions,
                                       List<MechanicInspectionDto> interpretations,
                                       List<MechanicsObservationDto> observations,
                                       List<UnsupportedMechanicsConstruct> unsupported,
                                       List<EvidenceLocatorDto> evidence, MechanicsCoverage coverage,
                                       MechanicsReviewSummary reviewSummary,
                                       List<MechanicsInspectionDiagnostic> diagnostics,
                                       Boolean readyForHumanReviewWithinScope,
                                       ExperimentalSpellMechanicsDto experimentalInterpretation,
                                       String origin) {
        this(schemaVersion, projection, availability, processing, sourceRevisions, interpretations, observations,
                unsupported, evidence, coverage, reviewSummary, diagnostics, readyForHumanReviewWithinScope,
                experimentalInterpretation, origin, null);
    }

    public SpellMechanicsInspectionDto {
        ModelChecks.require(schemaVersion == 1, "Unsupported inspection schema version");
        ModelChecks.require("CANONICAL".equals(origin), "Canonical inspection origin must be explicit");
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
