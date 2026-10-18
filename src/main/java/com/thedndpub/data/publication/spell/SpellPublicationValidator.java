package com.thedndpub.data.publication.spell;

import com.thedndpub.data.dto.spell.SpellDefinition;
import com.thedndpub.data.dto.spell.SpellFormulaAnnotation;
import com.thedndpub.data.dto.spell.SpellFormulaAnnotationIndex;
import com.thedndpub.data.dto.spell.SpellId;
import com.thedndpub.data.mechanics.syntax.FormulaSyntaxObservation;
import com.thedndpub.data.mechanics.syntax.SyntaxExpressionKind;
import com.thedndpub.data.mechanics.syntax.SyntaxOrigin;
import com.thedndpub.data.services.SourceRegistry;

import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/** Validates evidence independently of permission. Only final, explicitly authorized events can grant access. */
public final class SpellPublicationValidator {
    private static final String SCOPE_ID = "spell-formula-observations-v1-literals";
    private static final String SCOPE_SHA256 =
            "434d1f4f8fdb64eb6df5af0dce44dca5ffffe00b2434713301ef01da545476dd";

    private final SourceRegistry sources;

    public SpellPublicationValidator(SourceRegistry sources) {
        this.sources = Objects.requireNonNull(sources, "sources");
    }

    public SpellPublicationIndex validate(
            List<SpellDefinition> spells,
            SpellFormulaAnnotationIndex formulas,
            SpellPublicationResources resources) {
        validateResources(resources);
        Map<SpellId, SpellDefinition> definitions = new LinkedHashMap<>();
        for (SpellDefinition spell : spells) {
            require(definitions.putIfAbsent(spell.id(), spell) == null, "Duplicate Spell identity");
        }
        require(definitions.keySet().equals(formulas.bySpellId().keySet()), "Spell/index owner mismatch");

        Map<String, SpellLicensedDocumentDte> documents = validateDocuments(resources.evidence());
        Map<String, SpellPublicationNoticeDte> notices = validateNotices(resources.notices(), documents);
        Map<String, SpellLicensedFormulaEvidenceDte> evidence = new LinkedHashMap<>();
        for (SpellLicensedFormulaEvidenceDte match : resources.evidence().matches()) {
            require(evidence.putIfAbsent(match.id(), match) == null, "Duplicate licensed evidence");
            require(match.classification() == SpellLicenseEvidenceClassification.VERIFIED_LICENSED_MATCH,
                    "Evidence is not a verified licensed match");
            require(documents.containsKey(match.documentId()), "Missing licensed document");
            text(match.spellHeading(), "Licensed heading");
            require(match.pdfPage() > 0, "Invalid licensed page");
            require(Set.of("left", "right").contains(match.column()), "Invalid licensed column");
            digest(match.matchSha256(), "Licensed match fingerprint");
        }

        Map<String, SpellPublicationCandidateDte> candidates = new LinkedHashMap<>();
        Map<String, SpellFormulaAnnotation> annotations = new LinkedHashMap<>();
        Map<String, SpellPublicationState> states = new LinkedHashMap<>();
        for (SpellPublicationCandidateDte candidate : resources.candidates().candidates()) {
            String occurrenceId = candidate.occurrenceId();
            require(candidates.putIfAbsent(occurrenceId, candidate) == null, "Duplicate candidate: " + occurrenceId);
            SpellFormulaAnnotation annotation = validateCandidate(candidate, definitions, formulas);
            validateMatch(candidate, evidence.get(candidate.evidenceId()), notices);
            annotations.put(occurrenceId, annotation);
            states.put(occurrenceId, SpellPublicationState.PENDING_REVIEW);
        }
        require(evidence.size() == candidates.size(), "Orphan or missing licensed evidence");
        require(new HashSet<>(resources.candidates().candidates().stream()
                .map(SpellPublicationCandidateDte::evidenceId).toList()).equals(evidence.keySet()),
                "Candidate/evidence identity mismatch");

        Map<String, SpellPublicationAuthorityDte> authorities = validateAuthorities(resources.policy());
        if (resources.notices().approval() != null) {
            validateApproval(resources.notices().approval(), SpellPublicationRole.ATTRIBUTION_APPROVAL,
                    SpellPublicationHashes.noticesHash(resources.notices()), authorities);
        }
        if (resources.policy().releaseApproval() != null) {
            require(resources.decisions() != null, "Release approval without decision artifact");
            validateApproval(resources.policy().releaseApproval(), SpellPublicationRole.RELEASE_APPROVAL,
                    SpellPublicationHashes.policyHash(resources.policy()), authorities);
        }

        Map<String, SpellPublicationDecisionDte> latest = applyDecisions(resources, candidates, states, authorities);
        List<SpellPublicationAuthorization> authorized = new ArrayList<>();
        for (SpellPublicationCandidateDte candidate : candidates.values()) {
            if (states.get(candidate.occurrenceId()) == SpellPublicationState.AUTHORIZED) {
                require(resources.notices().approval() != null, "Unapproved attribution configuration");
                require(resources.policy().releaseApproval() != null, "Missing release approval");
                authorized.add(new SpellPublicationAuthorization(
                        annotations.get(candidate.occurrenceId()), candidate.evidenceId(), candidate.noticeId(),
                        latest.get(candidate.occurrenceId()).eventId()));
            }
        }
        return new SpellPublicationIndex(resources.candidates().candidates(), states, authorized);
    }

    private void validateResources(SpellPublicationResources resources) {
        SpellPublicationPolicyDte policy = resources.policy();
        require(policy.schemaVersion() == 1 && resources.candidates().schemaVersion() == 1
                && resources.evidence().schemaVersion() == 1 && resources.notices().schemaVersion() == 1,
                "Unsupported publication schema");
        require("5eTools v2.37.0".equals(policy.datasetVersion()), "Unsupported source revision");
        same(SCOPE_ID, policy.scopeId(), "Unsupported publication scope");
        same(SCOPE_SHA256, policy.scopeSha256(), "Publication scope fingerprint mismatch");
        digest(policy.candidateManifestSha256(), "Candidate manifest fingerprint");
        digest(policy.attributionProposalSha256(), "Attribution proposal fingerprint");
        same(policy.attributionProposalSha256(), resources.notices().attributionProposalSha256(),
                "Attribution proposal mismatch");
        digest(resources.evidence().crosswalkSha256(), "Crosswalk fingerprint");
        pinned(policy.candidatesSha256(), resources.candidatesSha256(), "Candidates");
        pinned(policy.evidenceSha256(), resources.evidenceSha256(), "Evidence");
        pinned(policy.noticesSha256(), resources.noticesSha256(), "Notices");
        require(policy.candidateCount() > 0 && policy.candidateCount() == resources.candidates().candidates().size(),
                "Candidate count mismatch");
        if (resources.decisions() == null) {
            require(policy.decisionsSha256() == null && resources.decisionsSha256() == null,
                    "Missing pinned decision artifact");
        } else {
            require(resources.decisions().schemaVersion() == 1, "Unsupported decision schema");
            pinned(policy.decisionsSha256(), resources.decisionsSha256(), "Decisions");
        }
    }

    private Map<String, SpellLicensedDocumentDte> validateDocuments(SpellPublicationEvidenceDte evidence) {
        Map<String, SpellLicensedDocumentDte> documents = new LinkedHashMap<>();
        for (SpellLicensedDocumentDte document : evidence.documents()) {
            text(document.id(), "Licensed document ID");
            require(documents.putIfAbsent(document.id(), document) == null, "Duplicate licensed document");
            require(Set.of("SRD-5.1", "SRD-5.2.1").contains(document.id()), "Unreviewed licensed document");
            same("SRD-" + document.version(), document.id(), "Licensed version mismatch");
            same("CC-BY-4.0", document.licenseId(), "Unsupported license");
            same("https://creativecommons.org/licenses/by/4.0/legalcode.en", document.licenseUrl(),
                    "License URL mismatch");
            https(document.url(), "Licensed document URL");
            digest(document.sha256(), "Licensed document fingerprint");
        }
        return documents;
    }

    private Map<String, SpellPublicationNoticeDte> validateNotices(
            SpellPublicationNoticesDte configuration,
            Map<String, SpellLicensedDocumentDte> documents) {
        Map<String, SpellPublicationNoticeDte> notices = new LinkedHashMap<>();
        for (SpellPublicationNoticeDte notice : configuration.notices()) {
            text(notice.id(), "Notice ID");
            require(notices.putIfAbsent(notice.id(), notice) == null, "Duplicate notice");
            require(documents.containsKey(notice.documentId()), "Notice has unknown document");
            text(notice.requiredAttribution(), "Required attribution");
            pinned(notice.requiredAttributionSha256(), SpellPublicationHashes.sha256(notice.requiredAttribution()),
                    "Attribution text");
            text(notice.changesDisclosure(), "Change disclosure");
            pinned(notice.changesDisclosureSha256(), SpellPublicationHashes.sha256(notice.changesDisclosure()),
                    "Change disclosure text");
            https(notice.disclaimerReference(), "Disclaimer reference");
        }
        return notices;
    }

    private SpellFormulaAnnotation validateCandidate(
            SpellPublicationCandidateDte candidate,
            Map<SpellId, SpellDefinition> definitions,
            SpellFormulaAnnotationIndex formulas) {
        SpellId spellId = new SpellId(candidate.spellId());
        SpellDefinition definition = definitions.get(spellId);
        require(definition != null, "Candidate has unknown Spell ID: " + candidate.occurrenceId());
        var source = sources.find(candidate.sourceCode());
        require(source != null && source.identity().code().equals(candidate.sourceCode()), "Unknown/exact source mismatch");
        same(definition.identity().source().code(), candidate.sourceCode(), "Spell source mismatch");
        require(candidate.initialReviewState() == SpellPublicationState.PENDING_REVIEW, "Candidate seed is not pending");
        digest(candidate.reviewBindingSha256(), "Review binding");
        require(candidate.occurrenceIndex() >= 0
                && candidate.occurrenceIndex() < formulas.bySpellId().get(spellId).size(),
                "Missing referenced formula occurrence");
        SpellFormulaAnnotation annotation = formulas.bySpellId().get(spellId).get(candidate.occurrenceIndex());
        require(annotation.observation() instanceof FormulaSyntaxObservation, "Not a formula observation");
        FormulaSyntaxObservation formula = (FormulaSyntaxObservation) annotation.observation();
        require(candidate.notation() != null, "Missing formula notation");
        same(candidate.notation().tagName(), formula.tag().name(), "Formula notation mismatch");
        same(candidate.expression(), formula.expression(), "Formula expression mismatch");
        require(formula.expression().kind() != SyntaxExpressionKind.OPAQUE_OR_SYMBOLIC
                && formula.tag().arguments().size() == 1, "Expression outside literal publication scope");
        same(candidate.sourceEvidence(), formula.tag().evidence(), "Source locator or fingerprint mismatch");
        require(candidate.sourceEvidence().location().origin() == SyntaxOrigin.AUTHORED, "Unreviewed formula origin");
        digest(candidate.sourceEvidence().location().owner().resourceSha256(), "Source resource fingerprint");
        pinned(candidate.tagSha256(), SpellPublicationHashes.sha256(formula.tag().originalText()), "Formula tag");
        return annotation;
    }

    private void validateMatch(
            SpellPublicationCandidateDte candidate,
            SpellLicensedFormulaEvidenceDte match,
            Map<String, SpellPublicationNoticeDte> notices) {
        require(match != null, "Missing licensed evidence");
        same(candidate.occurrenceId(), match.occurrenceId(), "Licensed occurrence mismatch");
        same(candidate.reviewBindingSha256(), match.reviewBindingSha256(), "Licensed review binding mismatch");
        same(candidate.expression().authoredToken(), match.expression(), "Licensed expression mismatch");
        same(candidate.noticeId(), match.noticeId(), "Licensed notice mismatch");
        SpellPublicationNoticeDte notice = notices.get(candidate.noticeId());
        require(notice != null, "Missing required attribution");
        same(match.documentId(), notice.documentId(), "Notice/document mismatch");
    }

    private Map<String, SpellPublicationAuthorityDte> validateAuthorities(SpellPublicationPolicyDte policy) {
        Map<String, SpellPublicationAuthorityDte> authorities = new LinkedHashMap<>();
        for (SpellPublicationAuthorityDte authority : policy.authorities()) {
            text(authority.reviewerId(), "Reviewer identity");
            text(authority.authorityReference(), "Reviewer authority reference");
            require(!authority.roles().isEmpty()
                    && new HashSet<>(authority.roles()).size() == authority.roles().size(), "Invalid reviewer roles");
            require(authorities.putIfAbsent(authority.reviewerId(), authority) == null, "Duplicate reviewer identity");
        }
        return authorities;
    }

    private void validateApproval(
            SpellPublicationApprovalDte approval,
            SpellPublicationRole role,
            String contentHash,
            Map<String, SpellPublicationAuthorityDte> authorities) {
        authority(approval.reviewerId(), approval.authorityReference(), role, authorities);
        text(approval.rationale(), "Approval rationale");
        timestamp(approval.reviewedAt());
        digest(approval.approvalRecordSha256(), "Approval record fingerprint");
        pinned(approval.contentSha256(), contentHash, "Approval content");
    }

    private Map<String, SpellPublicationDecisionDte> applyDecisions(
            SpellPublicationResources resources,
            Map<String, SpellPublicationCandidateDte> candidates,
            Map<String, SpellPublicationState> states,
            Map<String, SpellPublicationAuthorityDte> authorities) {
        Map<String, SpellPublicationDecisionDte> latest = new LinkedHashMap<>();
        if (resources.decisions() == null) {
            return latest;
        }
        Set<String> eventIds = new HashSet<>();
        String previousHash = null;
        Instant previousTime = null;
        int sequence = 1;
        for (SpellPublicationDecisionEventDte event : resources.decisions().events()) {
            SpellPublicationDecisionDte decision = event.decision();
            text(decision.eventId(), "Decision event ID");
            require(eventIds.add(decision.eventId()), "Duplicate decision event");
            require(decision.sequence() == sequence++, "Non-sequential decision journal");
            same(previousHash, decision.previousEventSha256(), "Broken decision hash chain");
            pinned(event.sha256(), SpellPublicationHashes.decisionHash(decision), "Decision event");
            SpellPublicationCandidateDte candidate = candidates.get(decision.occurrenceId());
            require(candidate != null, "Decision outside reviewed candidate set");
            same(resources.policy().candidateManifestSha256(), decision.candidateManifestSha256(),
                    "Decision manifest mismatch");
            same(candidate.reviewBindingSha256(), decision.bindingSha256(), "Decision binding mismatch");
            same(resources.policy().scopeId(), decision.publicationScopeId(), "Decision scope mismatch");
            same(resources.policy().attributionProposalSha256(), decision.attributionProposalSha256(),
                    "Decision attribution mismatch");
            authority(decision.reviewerId(), decision.reviewerAuthorityRef(), SpellPublicationRole.OCCURRENCE_REVIEW,
                    authorities);
            text(decision.rationale(), "Decision rationale");
            Instant time = timestamp(decision.reviewedAt());
            require(previousTime == null || !time.isBefore(previousTime), "Decision timestamps move backwards");
            SpellPublicationDecisionDte predecessor = latest.get(decision.occurrenceId());
            same(predecessor == null ? null : predecessor.eventId(), decision.supersedesEventId(),
                    "Missing or invalid explicit supersession");
            states.put(decision.occurrenceId(), transition(states.get(decision.occurrenceId()), decision.action()));
            latest.put(decision.occurrenceId(), decision);
            previousHash = event.sha256();
            previousTime = time;
        }
        same(previousHash, resources.decisions().headEventSha256(), "Decision head mismatch");
        return latest;
    }

    private SpellPublicationState transition(SpellPublicationState state, SpellPublicationAction action) {
        require(action != null, "Missing decision action");
        return switch (action) {
            case AUTHORIZE_EXACT_SCOPE -> {
                require(state != SpellPublicationState.AUTHORIZED, "Duplicate active approval");
                yield SpellPublicationState.AUTHORIZED;
            }
            case REJECT -> {
                require(state == SpellPublicationState.PENDING_REVIEW || state == SpellPublicationState.WITHDRAWN,
                        "Invalid rejection transition; withdraw an active approval first");
                yield SpellPublicationState.REJECTED;
            }
            case WITHDRAW -> {
                require(state == SpellPublicationState.AUTHORIZED, "Cannot withdraw an inactive approval");
                yield SpellPublicationState.WITHDRAWN;
            }
        };
    }

    private void authority(
            String reviewerId, String reference, SpellPublicationRole role,
            Map<String, SpellPublicationAuthorityDte> authorities) {
        SpellPublicationAuthorityDte authority = authorities.get(reviewerId);
        require(authority != null && authority.roles().contains(role), "Unknown or unauthorized reviewer role");
        same(authority.authorityReference(), reference, "Reviewer authority mismatch");
    }

    private static Instant timestamp(String value) {
        text(value, "Review timestamp");
        require(value.endsWith("Z"), "Review timestamp must be UTC");
        return Instant.parse(value);
    }

    private static void https(String value, String label) {
        text(value, label);
        URI uri = URI.create(value);
        require("https".equals(uri.getScheme()) && uri.getHost() != null, label + " must be HTTPS");
    }

    private static void pinned(String expected, String actual, String label) {
        digest(expected, label + " fingerprint");
        same(expected, actual, label + " fingerprint mismatch");
    }

    private static void digest(String value, String label) {
        require(value != null && value.matches("[0-9a-f]{64}"), label + " must be a SHA-256 digest");
    }

    private static void text(String value, String label) {
        require(value != null && !value.isBlank(), label + " is required");
    }

    private static <T> void same(T expected, T actual, String message) {
        require(Objects.equals(expected, actual), message);
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException("Spell publication: " + message);
        }
    }
}
