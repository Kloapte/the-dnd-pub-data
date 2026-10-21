package com.thedndpub.data.mechanics.inspection;

import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.mechanics.model.EvidenceRevisionReference;
import com.thedndpub.data.mechanics.model.FormulaObservationReference;
import com.thedndpub.data.mechanics.model.LicenseCoverage;
import com.thedndpub.data.mechanics.model.MechanicDerivation;
import com.thedndpub.data.mechanics.model.MechanicRecord;
import com.thedndpub.data.mechanics.model.MechanicsDocument;
import com.thedndpub.data.mechanics.model.MechanicsEvidence;
import com.thedndpub.data.mechanics.model.MechanicsLicensing;
import com.thedndpub.data.mechanics.model.MechanicsObservationReference;
import com.thedndpub.data.mechanics.model.MechanicsOrigin;
import com.thedndpub.data.mechanics.model.MechanicsOwnerReference;
import com.thedndpub.data.mechanics.model.ModelChecks;
import com.thedndpub.data.mechanics.model.ObservationRevisionReference;
import com.thedndpub.data.mechanics.model.OpenMechanicsLicensing;
import com.thedndpub.data.mechanics.model.OtherMechanicsLicensing;
import com.thedndpub.data.mechanics.model.OtherSyntaxObservationReference;
import com.thedndpub.data.mechanics.model.ProcessingState;
import com.thedndpub.data.mechanics.model.SrdMechanicsLicensing;
import com.thedndpub.data.mechanics.model.StructuredDeclarationReference;
import com.thedndpub.data.mechanics.model.StructuredFieldMechanicsEvidence;
import com.thedndpub.data.mechanics.model.TextMechanicsEvidence;
import com.thedndpub.data.mechanics.model.TransformedMechanicsEvidence;
import com.thedndpub.data.mechanics.model.UnassessedMechanicsLicensing;
import com.thedndpub.data.mechanics.model.UnavailableOriginMechanicsEvidence;
import com.thedndpub.data.mechanics.model.UnknownValueReason;
import com.thedndpub.data.mechanics.syntax.SyntaxEvidence;
import com.thedndpub.data.mechanics.syntax.SyntaxOrigin;

import java.util.List;

/** Private inspection projection. It does not consult publication decisions or interpret source syntax. */
public final class SpellMechanicsInspectionMapper {
    public SpellMechanicsInspectionDto project(MechanicsDocument document) {
        ModelChecks.required(document, "document");
        boolean failed = document.processing().state() == ProcessingState.FAILED;
        return new SpellMechanicsInspectionDto(
                1,
                MechanicsInspectionProjection.PRIVATE_INSPECTION,
                failed ? MechanicsInspectionAvailability.UNAVAILABLE : MechanicsInspectionAvailability.AVAILABLE,
                document.processing(),
                document.sources().stream().map(source -> new MechanicsSourceRevisionDto(
                        source.resourcePath(), source.datasetVersion(), source.resourcePath(), source.resourceSha256()))
                        .toList(),
                document.interpretations().stream().map(this::projectClaim).toList(),
                document.observations().stream().map(observation -> observation(observation, document.owner())).toList(),
                document.unsupported(),
                document.evidence().stream().map(this::evidence).toList(),
                document.coverage(),
                document.reviewSummary(),
                failed ? List.of(MechanicsInspectionDiagnostic.INTERPRETATION_PROCESSING_FAILED) : List.of(),
                !failed && document.coverage().status()
                        == com.thedndpub.data.mechanics.model.CoverageStatus.COMPLETE_WITHIN_SCOPE ? Boolean.TRUE : null);
    }

    public MechanicInspectionDto projectClaim(MechanicRecord record) {
        MechanicDerivation derivation = record.derivation();
        return new MechanicInspectionDto(
                record.id(), record.revision().number(), record.payload(), record.context(),
                record.evidenceAssociations().stream().map(association -> new MechanicsEvidenceAssociationDto(
                        association.evidenceId(), association.claimPath(), association.role())).toList(),
                new MechanicsDerivationDto(derivation.method(), derivation.patternId(), derivation.patternVersion(),
                        derivation.extractorVersion(),
                        derivation.inputObservations().stream().map(ObservationRevisionReference::id).toList()),
                record.assessment(),
                new MechanicsReviewDto(record.review().recordedStatus(), record.review().effectiveStatus(),
                        record.review().findings()),
                licensing(record.licensing()));
    }

    private MechanicsLicensingDto licensing(MechanicsLicensing licensing) {
        return switch (licensing) {
            case UnassessedMechanicsLicensing unassessed -> new MechanicsLicensingDto(
                    licensing.status(), unassessed.reason(),
                    unassessed.partialCoverage().stream().map(this::licenseCoverage).toList(), null);
            case SrdMechanicsLicensing srd -> new MechanicsLicensingDto(
                    licensing.status(), null, srd.coverage().stream().map(this::licenseCoverage).toList(), null);
            case OtherMechanicsLicensing other -> new MechanicsLicensingDto(
                    licensing.status(), null, other.coverage().stream().map(this::licenseCoverage).toList(), null);
            case OpenMechanicsLicensing open -> new MechanicsLicensingDto(
                    licensing.status(), null, List.of(), open.basis().id());
        };
    }

    private MechanicsLicenseCoverageDto licenseCoverage(LicenseCoverage coverage) {
        return new MechanicsLicenseCoverageDto(
                coverage.scope().claimPaths(), coverage.document(), coverage.license(), coverage.attribution());
    }

    private MechanicsObservationDto observation(MechanicsObservationReference observation, MechanicsOwnerReference owner) {
        return switch (observation) {
            case FormulaObservationReference formula -> new FormulaObservationInspectionDto(
                    formula.id(), formula.spellId().value(), formula.occurrenceIndex(), formula.source().resourcePath(),
                    observationEvidence(formula.id(), owner, owner.source(), formula.evidence()));
            case StructuredDeclarationReference field -> new StructuredObservationInspectionDto(
                    field.id(), field.normalizedProperty(), field.source().resourcePath(), field.jsonPointer());
            case OtherSyntaxObservationReference syntax -> new SyntaxObservationInspectionDto(
                    syntax.id(), syntax.tagName(),
                    observationEvidence(syntax.id(), owner, null, syntax.evidence()));
        };
    }

    private EvidenceLocatorDto observationEvidence(
            String id, MechanicsOwnerReference owner, SourceReference source, SyntaxEvidence evidence) {
        if (evidence.location().origin() == SyntaxOrigin.AUTHORED_ORIGIN_UNAVAILABLE) {
            return new UnavailableEvidenceLocatorDto(id, owner.id(), evidence.location().jsonPointer(),
                    UnknownValueReason.PROVENANCE_UNAVAILABLE);
        }
        // A transform instruction is itself authored text, never an unchanged transformed result.
        return textLocator(id, source, evidence, MechanicsOrigin.AUTHORED);
    }

    private EvidenceLocatorDto evidence(MechanicsEvidence evidence) {
        return switch (evidence) {
            case TextMechanicsEvidence text -> textLocator(text.id(), text.source(), text.syntax(), text.origin());
            case StructuredFieldMechanicsEvidence field -> new StructuredEvidenceLocatorDto(
                    field.id(), field.source(), field.owner().resourcePath(), field.owner().jsonPointer(),
                    field.jsonPointer(), field.origin());
            case TransformedMechanicsEvidence transformed -> new TransformedEvidenceLocatorDto(
                    transformed.id(), transformed.effectiveOwner().id(), transformed.effectivePointer(),
                    transformed.inputs().stream().map(EvidenceRevisionReference::id).toList());
            case UnavailableOriginMechanicsEvidence unavailable -> new UnavailableEvidenceLocatorDto(
                    unavailable.id(), unavailable.effectiveOwner().id(), unavailable.effectivePointer(), unavailable.reason());
        };
    }

    private TextEvidenceLocatorDto textLocator(
            String id, SourceReference source, SyntaxEvidence evidence, MechanicsOrigin origin) {
        var location = evidence.location();
        return new TextEvidenceLocatorDto(id, source, location.owner().resourcePath(), location.owner().jsonPointer(),
                location.jsonPointer(), origin, evidence.span());
    }
}
