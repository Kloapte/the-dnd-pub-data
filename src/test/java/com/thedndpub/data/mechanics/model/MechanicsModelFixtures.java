package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.mechanics.review.HumanMechanicsReviewDecision;
import com.thedndpub.data.mechanics.review.MechanicsReviewAction;
import com.thedndpub.data.mechanics.review.MechanicsReviewerAuthority;
import com.thedndpub.data.mechanics.review.MechanicsReviewPolicy;
import com.thedndpub.data.mechanics.review.MechanicsReviewState;
import com.thedndpub.data.mechanics.review.ValidatedHumanMechanicsReview;
import com.thedndpub.data.mechanics.syntax.SyntaxContext;
import com.thedndpub.data.mechanics.syntax.SyntaxEvidence;
import com.thedndpub.data.mechanics.syntax.SyntaxOrigin;
import com.thedndpub.data.mechanics.syntax.SyntaxSourceOwner;
import com.thedndpub.data.mechanics.syntax.SyntaxSpan;
import com.thedndpub.data.mechanics.syntax.SyntaxTextLocation;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;

/** Manually constructed model examples only. These are not extraction results or reviewed benchmarks. */
final class MechanicsModelFixtures {
    static final String HASH = MechanicsHashes.sha256("test-only-evidence");
    static final MechanicsOwnerReference PHB = owner("PHB", "shield", 275);
    static final MechanicsOwnerReference XPHB = owner("XPHB", "shield", 316);
    static final MechanicsReviewPolicy REVIEW_POLICY = new MechanicsReviewPolicy(List.of(
            new MechanicsReviewerAuthority("fixture-reviewer", "test-only-mechanics-mandate", HASH)));

    private MechanicsModelFixtures() {
    }

    static MechanicsOwnerReference owner(String source, String name, int page) {
        return new MechanicsOwnerReference(CatalogDomain.SPELL,
                "spell:" + source.toLowerCase() + ":" + name, new SourceReference(source, page));
    }

    static SourceArtifactRevision artifact(String source) {
        String path = "data/spells/spells-" + source.toLowerCase() + ".json";
        try {
            return new SourceArtifactRevision("5eTools v2.37.0", path,
                    MechanicsHashes.sha256(new ClassPathResource(path).getContentAsByteArray()));
        } catch (IOException exception) {
            throw new IllegalStateException(exception);
        }
    }

    static TextMechanicsEvidence body(MechanicsOwnerReference owner, int rootIndex) {
        SourceArtifactRevision artifact = artifact(owner.source().identity().code());
        String root = "/spell/" + rootIndex;
        String pointer = root + "/entries/0";
        try {
            var source = new ObjectMapper().readTree(new ClassPathResource(artifact.resourcePath()).getContentAsByteArray());
            String text = source.at(pointer).textValue();
            SyntaxSourceOwner authored = new SyntaxSourceOwner(artifact.resourcePath(), artifact.resourceSha256(), "spell", root);
            SyntaxTextLocation location = new SyntaxTextLocation(authored, pointer,
                    List.of(new SyntaxContext(root, null), new SyntaxContext(root + "/entries", null)), SyntaxOrigin.AUTHORED);
            return new TextMechanicsEvidence("body", owner.source(),
                    new SyntaxEvidence(location, new SyntaxSpan(0, text.length())),
                    MechanicsHashes.sha256(text), MechanicsOrigin.AUTHORED, owner);
        } catch (IOException exception) {
            throw new IllegalStateException(exception);
        }
    }

    static TextMechanicsEvidence shieldBody() {
        return body(PHB, 294);
    }

    static MechanicDerivation derivation(MechanicsOwnerReference owner) {
        return new MechanicDerivation(DerivationMethod.PROSE_INTERPRETATION,
                "model-construction-only", "v1", "test-only", List.of(),
                List.of(artifact(owner.source().identity().code())));
    }

    static MechanicsAssessment complete() {
        return new MechanicsAssessment(MechanicsCompleteness.COMPLETE_WITHIN_CLAIM,
                MechanicsSupport.PROSE_INTERPRETATION, List.of(), List.of());
    }

    static MechanicRecord ac() {
        return proposal(PHB, "ac-bonus", new ArmorClassModifierMechanic(new IntegerModifier(5, "+5")),
                new MechanicContext(new CasterSubject(), List.of()), complete(), shieldBody());
    }

    static MechanicRecord proposal(
            MechanicsOwnerReference owner, String slot, MechanicPayload payload, MechanicContext context,
            MechanicsAssessment assessment, MechanicsEvidence evidence) {
        return MechanicRecord.propose(MechanicId.of(owner, slot), payload, context,
                List.of(MechanicEvidenceAssociation.of(evidence, "/payload", EvidenceRole.EFFECT_ROLE),
                        MechanicEvidenceAssociation.of(evidence, "/context", EvidenceRole.QUALIFIER)),
                derivation(owner), assessment);
    }

    static MechanicsDocument document(List<MechanicRecord> records) {
        return document(PHB, records, List.of(shieldBody()), List.of(), List.of());
    }

    static MechanicsDocument document(
            MechanicsOwnerReference owner, List<MechanicRecord> records, List<MechanicsEvidence> evidence,
            List<MechanicsObservationReference> observations, List<UnsupportedMechanicsConstruct> unsupported) {
        return MechanicsDocument.create(owner, 1,
                List.of(artifact(owner.source().identity().code())),
                new MechanicsProcessing(ProcessingState.PROCESSED, "model-construction-only", "v1"),
                records, observations, unsupported, evidence,
                new MechanicsCoverage(records.isEmpty() ? CoverageStatus.NONE_SUPPORTED : CoverageStatus.PARTIAL,
                        "model-construction-only", "v1", List.of(), List.of(), unsupported.size()));
    }

    static MechanicRecord reviewed(MechanicRecord record, MechanicsReviewAction action, boolean partialScope) {
        var decision = new HumanMechanicsReviewDecision("fixture-decision", record.revision().reference(record.id()),
                "fixture-reviewer", "test-only-mechanics-mandate", action,
                "Synthetic model-construction decision; no real mechanics verified",
                "2026-10-18T00:00:00Z", partialScope, null);
        var validated = ValidatedHumanMechanicsReview.validate(decision, REVIEW_POLICY);
        return withReview(record, new MechanicsReviewState(record.revision().reference(record.id()), validated, List.of()));
    }

    static MechanicRecord withReview(MechanicRecord record, MechanicsReviewState review) {
        return new MechanicRecord(record.id(), record.revision(), record.payload(), record.context(),
                record.evidenceAssociations(), record.derivation(), record.assessment(), review, record.licensing());
    }

    static MechanicRecord withLicensing(MechanicRecord record, MechanicsLicensing licensing) {
        return new MechanicRecord(record.id(), record.revision(), record.payload(), record.context(),
                record.evidenceAssociations(), record.derivation(), record.assessment(), record.review(), licensing);
    }

    static MechanicRecord revise(MechanicRecord record, MechanicPayload payload) {
        var content = new MechanicRevisionContent(record.id(), payload, record.context(),
                record.evidenceAssociations(), record.derivation(), record.assessment());
        var revision = new MechanicRevision(record.revision().number() + 1, MechanicsHashes.claimHash(content),
                record.revision().reference(record.id()));
        var review = new MechanicsReviewState(revision.reference(record.id()), record.review().humanDecision(), List.of());
        return new MechanicRecord(record.id(), revision, payload, record.context(), record.evidenceAssociations(),
                record.derivation(), record.assessment(), review, record.licensing());
    }

    static LicenseCoverage coverage(MechanicRecord record, List<String> fields) {
        return new LicenseCoverage(new ClaimRightsScope(record.revision().reference(record.id()), fields),
                new LicensedDocumentReference("SRD-5.1", "5.1", "https://example.invalid/srd", HASH),
                new LicenseReference("CC-BY-4.0", "4.0", "https://creativecommons.org/licenses/by/4.0/"),
                List.of(new RightsEvidenceReference("fixture-license-evidence", HASH, "test-only-record")),
                List.of(new AttributionReference("fixture-notice", "v1", HASH)),
                new RightsDecisionReference("fixture-rights-decision", "test-only-rights-mandate", HASH));
    }
}
