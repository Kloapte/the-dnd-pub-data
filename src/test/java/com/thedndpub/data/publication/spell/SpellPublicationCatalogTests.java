package com.thedndpub.data.publication.spell;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.thedndpub.data.dto.spell.SpellDefinition;
import com.thedndpub.data.dto.spell.SpellFormulaAnnotationIndex;
import com.thedndpub.data.services.SourceRegistry;
import com.thedndpub.data.services.SpellCatalog;
import com.thedndpub.data.services.SpellFormulaAnnotationCatalog;
import com.thedndpub.data.services.SpellPublicationCatalog;
import com.thedndpub.data.services.SpellsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.AbstractResource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SpellPublicationCatalogTests {
    private static final Path REVIEW = Path.of("analysis/mechanics/public-v1/spells/p1c");
    private static final String TEST_REVIEWER = "test-only-reviewer";
    private static final String TEST_AUTHORITY = "test-fixture-only:not-a-production-approval";
    private static final String TEST_TIMESTAMP = "2026-10-18T00:00:00Z";

    @Autowired
    private ObjectMapper mapper;
    @Autowired
    private SpellCatalog spells;
    @Autowired
    private SpellFormulaAnnotationCatalog formulas;
    @Autowired
    private SourceRegistry sources;
    @Autowired
    private SpellPublicationCatalog publication;
    @Autowired
    private SpellsService service;

    private Map<String, ObjectNode> documents;

    @BeforeEach
    void loadUnapprovedProductionFixtures() throws Exception {
        documents = new HashMap<>();
        var loader = new DefaultResourceLoader();
        for (String name : List.of("policy", "candidates", "evidence", "notices")) {
            byte[] bytes = loader.getResource(SpellPublicationSourceLoader.PREFIX + name + ".json")
                    .getContentAsByteArray();
            documents.put(name, (ObjectNode) mapper.readTree(bytes));
        }
    }

    @Test
    void initialStartupHasOnlyPendingCandidatesAndNoPermission() {
        var index = publication.index();
        assertEquals(250, index.candidates().size());
        assertEquals(195, index.candidates().stream().map(SpellPublicationCandidateDte::spellId).distinct().count());
        assertTrue(index.authorizedObservations().isEmpty());
        assertEquals(250, index.states().size());
        assertTrue(index.states().values().stream().allMatch(state -> state == SpellPublicationState.PENDING_REVIEW));
        for (var candidate : index.candidates()) {
            assertEquals(SpellPublicationState.PENDING_REVIEW, candidate.initialReviewState());
            assertFalse(index.isAuthorized(candidate.occurrenceId()));
        }
        assertFalse(index.isAuthorized("unknown-occurrence"));
        var runtime = new SpellPublicationSourceLoader(mapper).load();
        assertNull(runtime.decisions());
        assertNull(runtime.policy().releaseApproval());
        assertNull(runtime.notices().approval());
        assertTrue(runtime.policy().authorities().isEmpty());
    }

    @Test
    void reconcilesEveryFrozenP1cCandidateAndKeepsAllExcludedOccurrencesOutsideTheGate() throws Exception {
        JsonNode manifest = mapper.readTree(Files.readAllBytes(REVIEW.resolve("candidate-manifest.json")));
        Map<String, SpellPublicationCandidateDte> candidates = new HashMap<>();
        publication.index().candidates().forEach(candidate -> candidates.put(candidate.occurrenceId(), candidate));
        for (JsonNode entry : manifest.path("entries")) {
            var candidate = candidates.get(entry.path("occurrenceId").asText());
            assertNotNull(candidate);
            assertEquals(entry.path("bindingSha256").asText(), candidate.reviewBindingSha256());
            assertEquals(entry.path("expression").path("authoredToken").asText(), candidate.expression().authoredToken());
            assertEquals(entry.path("sourceEvidence").path("resourceSha256").asText(),
                    candidate.sourceEvidence().location().owner().resourceSha256());
            assertEquals("PENDING_REVIEW", entry.path("review").path("status").asText());
        }
        JsonNode excluded = mapper.readTree(Files.readAllBytes(REVIEW.resolve("excluded-candidates.json")));
        assertEquals(458, excluded.path("entries").size());
        for (JsonNode entry : excluded.path("entries")) {
            assertFalse(candidates.containsKey(entry.path("occurrenceId").asText()));
            assertFalse(publication.index().isAuthorized(entry.path("occurrenceId").asText()));
        }
        assertEquals(708, candidates.size() + excluded.path("entries").size());
    }

    @Test
    void validTestOnlyApprovalRequiresOccurrenceNoticeAndReleaseApprovals() throws Exception {
        addTestAuthority();
        appendDecision(firstOccurrence(), SpellPublicationAction.AUTHORIZE_EXACT_SCOPE);
        approveNoticeFixture();
        approveReleaseFixture();
        var index = validate();
        assertEquals(1, index.authorizedObservations().size());
        assertTrue(index.isAuthorized(firstOccurrence()));
        var authorization = index.authorizedObservations().getFirst();
        var candidate = index.candidates().getFirst();
        assertSame(formulas.index().bySpellId().get(authorization.annotation().spellId())
                .get(candidate.occurrenceIndex()), authorization.annotation());
        assertEquals(candidate.evidenceId(), authorization.evidenceId());
        assertEquals(candidate.noticeId(), authorization.noticeId());
        assertEquals(249, index.states().values().stream()
                .filter(state -> state == SpellPublicationState.PENDING_REVIEW).count());
    }

    @Test
    void selfClaimedReviewerOrLicenseMatchNeverGrantsPermission() throws Exception {
        appendDecision(firstOccurrence(), SpellPublicationAction.AUTHORIZE_EXACT_SCOPE);
        assertRejected("Unknown or unauthorized reviewer");
        addTestAuthority();
        assertRejected("Unapproved attribution");
        approveNoticeFixture();
        assertRejected("Missing release approval");
        approveReleaseFixture();
        assertEquals(1, validate().authorizedObservations().size());
    }

    @Test
    void rejectedAndWithdrawnDecisionsRemainWithheld() throws Exception {
        addTestAuthority();
        appendDecision(firstOccurrence(), SpellPublicationAction.REJECT);
        assertEquals(SpellPublicationState.REJECTED, validate().states().get(firstOccurrence()));
        assertTrue(validate().authorizedObservations().isEmpty());
        appendDecision(firstOccurrence(), SpellPublicationAction.AUTHORIZE_EXACT_SCOPE);
        appendDecision(firstOccurrence(), SpellPublicationAction.WITHDRAW);
        assertEquals(SpellPublicationState.WITHDRAWN, validate().states().get(firstOccurrence()));
        assertTrue(validate().authorizedObservations().isEmpty());
        assertFalse(validate().isAuthorized(firstOccurrence()));
    }

    @Test
    void rejectsInvalidTransitionsAndMissingExplicitSupersession() throws Exception {
        addTestAuthority();
        appendDecision(firstOccurrence(), SpellPublicationAction.WITHDRAW);
        assertRejected("inactive approval");
        documents.remove("decisions");
        appendDecision(firstOccurrence(), SpellPublicationAction.AUTHORIZE_EXACT_SCOPE);
        appendDecision(firstOccurrence(), SpellPublicationAction.AUTHORIZE_EXACT_SCOPE);
        assertRejected("Duplicate active approval");
        documents.remove("decisions");
        appendDecision(firstOccurrence(), SpellPublicationAction.AUTHORIZE_EXACT_SCOPE);
        appendDecision(firstOccurrence(), SpellPublicationAction.REJECT);
        assertRejected("Invalid rejection transition");
        documents.remove("decisions");
        appendDecision(firstOccurrence(), SpellPublicationAction.REJECT);
        appendDecision(firstOccurrence(), SpellPublicationAction.AUTHORIZE_EXACT_SCOPE);
        decision(1).putNull("supersedesEventId");
        rehashJournal();
        assertRejected("supersession");
    }

    @Test
    void rejectsUnknownAndExcludedDecisionOccurrences() throws Exception {
        addTestAuthority();
        appendDecision(firstOccurrence(), SpellPublicationAction.REJECT);
        JsonNode excluded = mapper.readTree(Files.readAllBytes(REVIEW.resolve("excluded-candidates.json")));
        for (String occurrence : List.of("spell:phb:unknown#formula-0",
                excluded.path("entries").get(0).path("occurrenceId").asText())) {
            decision(0).put("occurrenceId", occurrence);
            rehashJournal();
            assertRejected("outside reviewed candidate set");
        }
    }

    @Test
    void rejectsEveryDecisionBindingMismatchEvenWithValidJournalHashes() throws Exception {
        addTestAuthority();
        appendDecision(firstOccurrence(), SpellPublicationAction.REJECT);
        for (String field : List.of("candidateManifestSha256", "bindingSha256", "publicationScopeId",
                "attributionProposalSha256", "reviewerId", "reviewerAuthorityRef", "rationale", "reviewedAt")) {
            JsonNode previous = decision(0).get(field).deepCopy();
            decision(0).put(field, "invalid");
            rehashJournal();
            assertThrows(RuntimeException.class, this::validate, field);
            decision(0).set(field, previous);
        }
    }

    @Test
    void rejectsDuplicateEventsBrokenHashesSequenceAndJournalHeads() throws Exception {
        addTestAuthority();
        appendDecision(firstOccurrence(), SpellPublicationAction.REJECT);
        var original = documents.get("decisions").deepCopy();
        ArrayNode events = (ArrayNode) documents.get("decisions").get("events");
        events.add(events.get(0).deepCopy());
        assertRejected("Duplicate decision event");
        documents.put("decisions", original.deepCopy());
        decision(0).put("sequence", 2);
        rehashJournal();
        assertRejected("Non-sequential");
        documents.put("decisions", original.deepCopy());
        decision(0).put("previousEventSha256", "0".repeat(64));
        rehashJournal();
        assertRejected("hash chain");
        documents.put("decisions", original.deepCopy());
        event(0).put("sha256", "0".repeat(64));
        assertRejected("Decision event fingerprint");
        documents.put("decisions", original.deepCopy());
        documents.get("decisions").put("headEventSha256", "0".repeat(64));
        assertRejected("Decision head");
    }

    @Test
    void emptyJournalIsSafeButCannotBeSilentlySubstitutedForAPinnedOrBrokenArtifact() throws Exception {
        documents.put("decisions", mapper.createObjectNode().put("schemaVersion", 1).putNull("headEventSha256"));
        documents.get("decisions").putArray("events");
        assertTrue(validate().authorizedObservations().isEmpty());
        var resources = load();
        documents.remove("decisions");
        documents.get("policy").put("decisionsSha256", resources.decisionsSha256());
        assertThrows(IllegalArgumentException.class, () -> validate(loadWithoutRepinning()));
    }

    @Test
    void rejectsMissingDuplicateMismatchedAndNonPendingCandidates() throws Exception {
        ObjectNode original = candidate().deepCopy();
        for (String field : List.of("spellId", "sourceCode", "notation", "tagSha256", "evidenceId",
                "noticeId", "reviewBindingSha256", "initialReviewState")) {
            candidate().put(field, "invalid");
            assertThrows(RuntimeException.class, this::validate, field);
            replaceFirstCandidate(original.deepCopy());
        }
        candidate().put("occurrenceIndex", 999);
        assertRejected("Missing referenced formula");
        replaceFirstCandidate(original.deepCopy());
        ((ObjectNode) candidate().path("expression")).put("authoredToken", "99d99");
        assertRejected("Formula expression mismatch");
        replaceFirstCandidate(original.deepCopy());
        candidate().put("initialReviewState", "AUTHORIZED");
        assertRejected("seed is not pending");
        replaceFirstCandidate(original.deepCopy());
        ((ArrayNode) documents.get("candidates").get("candidates")).add(original);
        documents.get("policy").put("candidateCount", 251);
        assertRejected("Duplicate candidate");
    }

    @Test
    void rejectsSourceFingerprintPointerAndSpanChanges() throws Exception {
        ObjectNode original = candidate().deepCopy();
        ObjectNode owner = (ObjectNode) candidate().path("sourceEvidence").path("location").path("owner");
        owner.put("resourceSha256", "0".repeat(64));
        assertRejected("Source locator or fingerprint mismatch");
        replaceFirstCandidate(original.deepCopy());
        ((ObjectNode) candidate().path("sourceEvidence").path("location")).put("jsonPointer", "/spell/0/entries/1");
        assertRejected("Source locator or fingerprint mismatch");
        replaceFirstCandidate(original.deepCopy());
        ((ObjectNode) candidate().path("sourceEvidence").path("span")).put("endExclusive", 232);
        assertRejected("Source locator or fingerprint mismatch");
        replaceFirstCandidate(original.deepCopy());
        candidate().put("sourceCode", "XPHB");
        assertRejected("Spell source mismatch");
    }

    @Test
    void rejectsMissingLicenseEvidenceAndMismatchedLicensedExpressions() throws Exception {
        var original = documents.get("evidence").deepCopy();
        ((ObjectNode) documents.get("evidence").path("matches").get(0)).put("classification", "AMBIGUOUS_MATCH");
        assertRejected("not a verified licensed match");
        documents.put("evidence", original.deepCopy());
        ((ArrayNode) documents.get("evidence").get("matches")).remove(0);
        assertRejected("Missing licensed evidence");
        documents.put("evidence", original.deepCopy());
        for (String field : List.of("occurrenceId", "reviewBindingSha256", "expression", "noticeId", "documentId")) {
            ObjectNode match = (ObjectNode) documents.get("evidence").path("matches").get(0);
            match.put(field, "invalid");
            assertThrows(RuntimeException.class, this::validate, field);
            documents.put("evidence", original.deepCopy());
        }
        ((ObjectNode) documents.get("evidence").path("documents").get(0)).put("licenseId", "MIT");
        assertRejected("Unsupported license");
        documents.put("evidence", original.deepCopy());
        ((ObjectNode) documents.get("evidence").path("documents").get(0)).put("version", "5.2.1");
        assertRejected("Licensed version mismatch");
    }

    @Test
    void rejectsMissingAttributionAndAlteredNoticeTextOrApprovalHashes() throws Exception {
        var original = documents.get("notices").deepCopy();
        ((ArrayNode) documents.get("notices").get("notices")).removeAll();
        assertRejected("Missing required attribution");
        documents.put("notices", original.deepCopy());
        ((ObjectNode) documents.get("notices").path("notices").get(0)).put("requiredAttribution", "altered");
        assertRejected("Attribution text fingerprint");
        documents.put("notices", original.deepCopy());
        addTestAuthority();
        approveNoticeFixture();
        ((ObjectNode) documents.get("notices").path("approval")).put("contentSha256", "0".repeat(64));
        assertRejected("Approval content fingerprint");
        approveNoticeFixture();
        appendDecision(firstOccurrence(), SpellPublicationAction.AUTHORIZE_EXACT_SCOPE);
        approveReleaseFixture();
        ((ObjectNode) documents.get("policy").path("releaseApproval")).put("contentSha256", "0".repeat(64));
        assertRejected("Approval content fingerprint");
    }

    @Test
    void resourceFingerprintsAndUnpinnedDecisionArtifactsFailClosed() throws Exception {
        var valid = load();
        candidate().put("tagSha256", "0".repeat(64));
        assertThrows(IllegalArgumentException.class, () -> validate(loadWithoutRepinning()));
        assertNotNull(valid);
        loadUnapprovedProductionFixtures();
        addTestAuthority();
        appendDecision(firstOccurrence(), SpellPublicationAction.REJECT);
        assertThrows(IllegalArgumentException.class, () -> validate(loadWithoutRepinning()));
    }

    @Test
    void malformedUnknownCoercedAndDuplicateJsonNeverBecomesAuthorization() throws Exception {
        for (String field : List.of("unknownProperty", "schemaVersion")) {
            var policy = documents.get("policy").deepCopy();
            documents.get("policy").put(field, "unknown");
            assertThrows(IllegalStateException.class, this::loadWithoutRepinning, field);
            documents.put("policy", policy);
        }
        for (String status : List.of("APPROVED", "VERIFIED_LICENSED_MATCH", "UNKNOWN")) {
            candidate().put("initialReviewState", status);
            assertThrows(IllegalStateException.class, this::loadWithoutRepinning, status);
        }
        candidate().put("initialReviewState", 0);
        assertThrows(IllegalStateException.class, this::loadWithoutRepinning);
        loadUnapprovedProductionFixtures();
        var bytes = resourceBytes();
        bytes.put("policy.json", "{\"schemaVersion\":1,\"schemaVersion\":1}".getBytes(StandardCharsets.UTF_8));
        var duplicateKeys = loader(bytes);
        assertThrows(IllegalStateException.class, duplicateKeys::load);
        bytes = resourceBytes();
        bytes.put("decisions.json", "{".getBytes(StandardCharsets.UTF_8));
        var broken = loader(bytes);
        assertThrows(IllegalStateException.class, broken::load);
    }

    @Test
    void unreadableExistingDecisionFileCannotMasqueradeAsAbsent() {
        ResourceLoader real = new DefaultResourceLoader();
        ResourceLoader inaccessible = new DefaultResourceLoader() {
            @Override
            public Resource getResource(String location) {
                if (!location.endsWith("decisions.json")) {
                    return real.getResource(location);
                }
                return new AbstractResource() {
                    @Override
                    public boolean exists() {
                        return true;
                    }

                    @Override
                    public String getDescription() {
                        return "test inaccessible decision resource";
                    }

                    @Override
                    public InputStream getInputStream() throws FileNotFoundException {
                        throw new FileNotFoundException("test read denied");
                    }
                };
            }
        };
        assertThrows(IllegalStateException.class, () -> new SpellPublicationSourceLoader(mapper, inaccessible).load());
    }

    @Test
    void missingOrMismatchedIndexOwnersNeverDefaultToAnEmptyObservationList() {
        var missing = new ArrayList<>(spells.findAll());
        missing.removeFirst();
        assertThrows(IllegalArgumentException.class,
                () -> new SpellPublicationValidator(sources).validate(missing, formulas.index(),
                        new SpellPublicationSourceLoader(mapper).load()));
        var empty = new SpellFormulaAnnotationIndex(spells.findAll(), 0, List.of(), List.of());
        assertThrows(IllegalArgumentException.class,
                () -> new SpellPublicationValidator(sources).validate(spells.findAll(), empty,
                        new SpellPublicationSourceLoader(mapper).load()));
    }

    @Test
    void canonicalDecisionHashIsIndependentOfApplicationJsonConfiguration() throws Exception {
        var decision = new SpellPublicationDecisionDte("test", 1, "test", "a", "b", "scope", "c",
                SpellPublicationAction.REJECT, TEST_REVIEWER, TEST_AUTHORITY, "Test fixture", TEST_TIMESTAMP, null, null);
        String canonical = "{\"action\":\"REJECT\",\"attributionProposalSha256\":\"c\",\"bindingSha256\":\"b\","
                + "\"candidateManifestSha256\":\"a\",\"eventId\":\"test\",\"occurrenceId\":\"test\","
                + "\"previousEventSha256\":null,\"publicationScopeId\":\"scope\",\"rationale\":\"Test fixture\","
                + "\"reviewedAt\":\"2026-10-18T00:00:00Z\",\"reviewerAuthorityRef\":\"" + TEST_AUTHORITY + "\","
                + "\"reviewerId\":\"" + TEST_REVIEWER + "\",\"sequence\":1,\"supersedesEventId\":null}";
        assertEquals(SpellPublicationHashes.sha256(canonical), SpellPublicationHashes.decisionHash(decision));
        assertEquals(SpellPublicationHashes.decisionHash(decision), SpellPublicationHashes.decisionHash(
                mapper.readValue(mapper.writeValueAsBytes(decision), SpellPublicationDecisionDte.class)));
    }

    @Test
    void unknownActionsNullOrCoercedIndexesAndInsufficientReviewerRolesFailClosed() throws Exception {
        addTestAuthority();
        appendDecision(firstOccurrence(), SpellPublicationAction.REJECT);
        decision(0).put("action", "APPROVE");
        assertThrows(IllegalStateException.class, this::load);
        decision(0).put("action", "REJECT");
        ((ObjectNode) documents.get("policy").path("authorities").get(0))
                .putArray("roles").add("RELEASE_APPROVAL");
        assertRejected("Unknown or unauthorized reviewer role");
        loadUnapprovedProductionFixtures();
        candidate().put("occurrenceIndex", 0.5);
        assertThrows(IllegalStateException.class, this::load);
        candidate().putNull("occurrenceIndex");
        assertThrows(IllegalStateException.class, this::load);
        candidate().remove("occurrenceIndex");
        assertThrows(IllegalStateException.class, this::load);
    }

    @Test
    void authorizationOrderFollowsCandidatesRatherThanJournalOrdering() throws Exception {
        addTestAuthority();
        String second = occurrenceId(documents.get("candidates").path("candidates").get(1));
        appendDecision(second, SpellPublicationAction.AUTHORIZE_EXACT_SCOPE);
        appendDecision(firstOccurrence(), SpellPublicationAction.AUTHORIZE_EXACT_SCOPE);
        approveNoticeFixture();
        approveReleaseFixture();
        var authorized = validate().authorizedObservations();
        assertEquals(List.of("test-event-1", "test-event-0"), authorized.stream()
                .map(SpellPublicationAuthorization::decisionId).toList());
        assertThrows(UnsupportedOperationException.class, authorized::clear);
    }

    @Test
    void immutableIndexIsDeterministicAndDoesNotChangeAnyPublicSpellProjection() throws Exception {
        var first = validate();
        var second = validate();
        assertEquals(first.candidates(), second.candidates());
        assertEquals(new ArrayList<>(first.states().entrySet()), new ArrayList<>(second.states().entrySet()));
        assertThrows(UnsupportedOperationException.class, () -> first.candidates().clear());
        assertThrows(UnsupportedOperationException.class, () -> first.states().clear());
        assertThrows(UnsupportedOperationException.class, () -> first.authorizedObservations().clear());
        assertThrows(UnsupportedOperationException.class,
                () -> first.candidates().getFirst().sourceEvidence().location().hierarchy().clear());
        Map<String, String> before = new HashMap<>();
        for (SpellDefinition spell : spells.findAll()) {
            before.put(spell.id().value(), mapper.writeValueAsString(service.findById(spell.id().value())));
        }
        addTestAuthority();
        appendDecision(firstOccurrence(), SpellPublicationAction.AUTHORIZE_EXACT_SCOPE);
        approveNoticeFixture();
        approveReleaseFixture();
        assertEquals(1, validate().authorizedObservations().size());
        Map<String, Integer> statuses = new HashMap<>();
        for (SpellDefinition spell : spells.findAll()) {
            var detail = service.findById(spell.id().value());
            assertEquals(before.get(spell.id().value()), mapper.writeValueAsString(detail));
            statuses.merge(detail.formulaProjectionStatus().name(), 1, Integer::sum);
        }
        assertEquals(Map.of("NONE_OBSERVED", 547, "WITHHELD", 422), statuses);
        assertEquals(969, spells.size());
        assertEquals(930, formulas.index().annotations().size());
    }

    private ObjectNode candidate() {
        return (ObjectNode) documents.get("candidates").path("candidates").get(0);
    }

    private void replaceFirstCandidate(ObjectNode replacement) {
        ((ArrayNode) documents.get("candidates").get("candidates")).set(0, replacement);
    }

    private String firstOccurrence() {
        return occurrenceId(candidate());
    }

    private String occurrenceId(JsonNode candidate) {
        return candidate.path("spellId").asText() + "#formula-" + candidate.path("occurrenceIndex").asInt();
    }

    private void addTestAuthority() {
        ArrayNode authorities = documents.get("policy").putArray("authorities");
        ObjectNode authority = authorities.addObject().put("reviewerId", TEST_REVIEWER)
                .put("authorityReference", TEST_AUTHORITY);
        ArrayNode roles = authority.putArray("roles");
        for (SpellPublicationRole role : SpellPublicationRole.values()) {
            roles.add(role.name());
        }
    }

    private void appendDecision(String occurrenceId, SpellPublicationAction action) throws Exception {
        ObjectNode journal = documents.computeIfAbsent("decisions", ignored -> {
            ObjectNode value = mapper.createObjectNode().put("schemaVersion", 1).putNull("headEventSha256");
            value.putArray("events");
            return value;
        });
        ArrayNode events = (ArrayNode) journal.get("events");
        String supersedes = null;
        for (JsonNode event : events) {
            if (occurrenceId.equals(event.path("decision").path("occurrenceId").asText())) {
                supersedes = event.path("decision").path("eventId").asText();
            }
        }
        String previousHash = events.isEmpty() ? null : events.get(events.size() - 1).path("sha256").asText();
        JsonNode reviewedCandidate = null;
        for (JsonNode candidate : documents.get("candidates").path("candidates")) {
            if (occurrenceId(candidate).equals(occurrenceId)) {
                reviewedCandidate = candidate;
            }
        }
        assertNotNull(reviewedCandidate, "Test event must target a review candidate");
        var decision = new SpellPublicationDecisionDte(
                "test-event-" + events.size(), events.size() + 1, occurrenceId,
                documents.get("policy").path("candidateManifestSha256").asText(),
                reviewedCandidate.path("reviewBindingSha256").asText(),
                documents.get("policy").path("scopeId").asText(),
                documents.get("policy").path("attributionProposalSha256").asText(),
                action, TEST_REVIEWER, TEST_AUTHORITY, "Synthetic fixture only", TEST_TIMESTAMP,
                supersedes, previousHash);
        String hash = SpellPublicationHashes.decisionHash(decision);
        events.add(mapper.valueToTree(new SpellPublicationDecisionEventDte(decision, hash)));
        journal.put("headEventSha256", hash);
    }

    private ObjectNode event(int index) {
        return (ObjectNode) documents.get("decisions").path("events").get(index);
    }

    private ObjectNode decision(int index) {
        return (ObjectNode) event(index).get("decision");
    }

    private void rehashJournal() throws Exception {
        for (int index = 0; index < documents.get("decisions").path("events").size(); index++) {
            var payload = mapper.treeToValue(decision(index), SpellPublicationDecisionDte.class);
            event(index).put("sha256", SpellPublicationHashes.decisionHash(payload));
        }
    }

    private void approveNoticeFixture() throws Exception {
        documents.get("notices").putNull("approval");
        var notices = mapper.treeToValue(documents.get("notices"), SpellPublicationNoticesDte.class);
        documents.get("notices").set("approval", mapper.valueToTree(fixtureApproval(
                SpellPublicationHashes.noticesHash(notices))));
    }

    private void approveReleaseFixture() throws Exception {
        repinResources();
        documents.get("policy").putNull("releaseApproval");
        var policy = mapper.treeToValue(documents.get("policy"), SpellPublicationPolicyDte.class);
        documents.get("policy").set("releaseApproval", mapper.valueToTree(fixtureApproval(
                SpellPublicationHashes.policyHash(policy))));
    }

    private SpellPublicationApprovalDte fixtureApproval(String contentHash) {
        return new SpellPublicationApprovalDte(TEST_REVIEWER, TEST_AUTHORITY, "Synthetic fixture only",
                TEST_TIMESTAMP, SpellPublicationHashes.sha256("test approval record"), contentHash);
    }

    private SpellPublicationIndex validate() throws Exception {
        return validate(load());
    }

    private SpellPublicationIndex validate(SpellPublicationResources resources) {
        return new SpellPublicationValidator(sources).validate(spells.findAll(), formulas.index(), resources);
    }

    private void assertRejected(String message) {
        var failure = assertThrows(IllegalArgumentException.class, this::validate);
        assertTrue(failure.getMessage().contains(message), failure.getMessage());
    }

    private SpellPublicationResources load() throws Exception {
        repinResources();
        return loadWithoutRepinning();
    }

    private SpellPublicationResources loadWithoutRepinning() throws Exception {
        return loader(resourceBytes()).load();
    }

    private void repinResources() throws Exception {
        for (String name : List.of("candidates", "evidence", "notices", "decisions")) {
            if (documents.containsKey(name)) {
                documents.get("policy").put(name + "Sha256", SpellPublicationHashes.sha256(
                        mapper.writeValueAsBytes(documents.get(name))));
            } else {
                documents.get("policy").putNull(name + "Sha256");
            }
        }
    }

    private Map<String, byte[]> resourceBytes() throws Exception {
        Map<String, byte[]> bytes = new HashMap<>();
        for (var entry : documents.entrySet()) {
            bytes.put(entry.getKey() + ".json", mapper.writeValueAsBytes(entry.getValue()));
        }
        return bytes;
    }

    private SpellPublicationSourceLoader loader(Map<String, byte[]> bytes) {
        ResourceLoader resources = new DefaultResourceLoader() {
            @Override
            public Resource getResource(String location) {
                String name = location.substring(SpellPublicationSourceLoader.PREFIX.length());
                byte[] content = bytes.get(name);
                if (content == null) {
                    return new org.springframework.core.io.ClassPathResource("test-only-nonexistent-resource");
                }
                return new ByteArrayResource(content);
            }
        };
        return new SpellPublicationSourceLoader(mapper, resources);
    }
}
