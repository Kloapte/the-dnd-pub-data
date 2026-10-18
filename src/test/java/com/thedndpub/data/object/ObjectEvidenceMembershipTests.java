package com.thedndpub.data.object;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.object.ObjectDefinitionMapper;
import com.thedndpub.data.dto.object.ObjectDetailDto;
import com.thedndpub.data.dto.object.evidence.ObjectEvidenceAnchor;
import com.thedndpub.data.dto.object.evidence.ObjectEvidenceMembership;
import com.thedndpub.data.dto.object.evidence.ObjectEvidenceMembershipIndex;
import com.thedndpub.data.dto.object.evidence.ObjectMembershipKind;
import com.thedndpub.data.dte.object.ObjectFileDte;
import com.thedndpub.data.importer.object.ObjectEvidenceMembershipAdapter;
import com.thedndpub.data.mechanics.syntax.SyntaxOrigin;
import com.thedndpub.data.services.ObjectCatalog;
import com.thedndpub.data.services.ObjectEvidenceMembershipCatalog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ObjectEvidenceMembershipTests {
    private static final Path REPORT = Path.of(
            "analysis/mechanics/batch-10a-object-membership/gold-verification.json");
    @Autowired private ObjectEvidenceMembershipCatalog memberships;
    @Autowired private ObjectCatalog objects;
    @Autowired private ObjectMapper mapper;

    @Test
    void indexesExactlySevenContainersAndAllSevenKindsWithoutPromotingOtherRoots() {
        var index = memberships.index();
        assertEquals(37, index.byObjectId().size());
        assertEquals(7, index.byObjectId().values().stream().filter(values -> !values.isEmpty()).count());
        assertEquals(63, index.memberships().size());
        var counts = new TreeMap<ObjectMembershipKind, Integer>();
        index.memberships().forEach(value -> counts.merge(value.kind(), 1, Integer::sum));
        assertEquals(7, counts.get(ObjectMembershipKind.HAS_ATTACK_SECTION));
        assertEquals(7, counts.get(ObjectMembershipKind.HAS_HIT_SECTION));
        assertEquals(7, counts.get(ObjectMembershipKind.HAS_AUTHORED_ATTACK_TYPE));
        assertEquals(7, counts.get(ObjectMembershipKind.SECTION_CONTAINS_MODIFIER_TAG));
        assertEquals(7, counts.get(ObjectMembershipKind.SECTION_CONTAINS_FORMULA_TAG));
        assertEquals(14, counts.get(ObjectMembershipKind.SECTION_CONTAINS_ENTRY));
        assertEquals(14, counts.get(ObjectMembershipKind.ENTRY_CONTAINS_SYNTAX));
        assertTrue(index.findByObjectId("object:HBTD:automatic-ballista").isEmpty());
        assertTrue(index.findByObjectId("object:EFA:eldritch-cannon").isEmpty());
    }

    @Test
    void reconcilesEveryGoldLabelWithExplicitControlAndAbstentionDenominators() throws Exception {
        JsonNode gold = mapper.readTree(Files.readString(Path.of(
                "analysis/mechanics/batch-10-object-relationships/gold-set.json")));
        var eligibleKinds = EnumSet.of(ObjectMembershipKind.HAS_ATTACK_SECTION,
                ObjectMembershipKind.HAS_HIT_SECTION, ObjectMembershipKind.HAS_AUTHORED_ATTACK_TYPE,
                ObjectMembershipKind.SECTION_CONTAINS_FORMULA_TAG,
                ObjectMembershipKind.SECTION_CONTAINS_MODIFIER_TAG);
        Set<ObjectEvidenceMembership> matched = new HashSet<>();
        var reconciliation = mapper.createArrayNode();
        int truePositives = 0;
        int trueNegatives = 0;
        int abstentions = 0;
        int excludedControls = 0;
        for (JsonNode label : gold.path("labels")) {
            List<ObjectEvidenceMembership> matches = memberships.index().memberships().stream()
                    .filter(value -> matchesLabel(value, label)).toList();
            boolean control = label.path("container").asText().equals("CONTROL");
            boolean positive = label.path("decision").asText().equals("ACCEPT");
            boolean eligible = positive && !control;
            assertEquals(eligible ? 1 : 0, matches.size(), label.path("id").asText());
            if (eligible) {
                truePositives++;
                matched.addAll(matches);
            } else if (label.path("decision").asText().equals("REJECT")) {
                trueNegatives++;
            } else if (label.path("abstain").asBoolean()) {
                abstentions++;
            } else {
                excludedControls++;
            }
            var row = reconciliation.addObject();
            row.put("id", label.path("id").asText());
            row.put("eligiblePositive", eligible);
            row.put("emitted", !matches.isEmpty());
            row.put("goldDecision", label.path("decision").asText());
        }
        var eligibleOutput = memberships.index().memberships().stream()
                .filter(value -> eligibleKinds.contains(value.kind())).toList();
        assertEquals(new HashSet<>(eligibleOutput), matched, "No unmatched eligible output edges");
        assertEquals(35, truePositives);
        assertEquals(9, trueNegatives);
        assertEquals(33, abstentions);
        assertEquals(3, excludedControls);
        var report = mapper.createObjectNode();
        report.put("labels", reconciliation.size());
        report.put("eligibleTruePositives", truePositives);
        report.put("eligibleFalsePositives", 0);
        report.put("eligibleFalseNegatives", 0);
        report.put("structuralTrueNegatives", trueNegatives);
        report.put("requiredAbstentions", abstentions);
        report.put("excludedPositiveControls", excludedControls);
        report.put("boundaryViolations", 0);
        report.put("eligiblePrecision", 1.0);
        report.put("eligibleRecall", 1.0);
        report.put("additionalStructuralEdges", 28);
        report.set("reconciliation", reconciliation);
        Files.createDirectories(REPORT.getParent());
        Files.writeString(REPORT, mapper.writerWithDefaultPrettyPrinter().writeValueAsString(report) + "\n");
    }

    @Test
    void everyEndpointHasExactOriginalOwnerHierarchyAndSpan() throws Exception {
        byte[] source;
        try (var input = new ClassPathResource("data/objects.json").getInputStream()) {
            source = input.readAllBytes();
        }
        JsonNode document = mapper.readTree(source);
        String fingerprint = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(source));
        for (var membership : memberships.index().memberships()) {
            assertNotNull(objects.findById(membership.objectId()));
            for (var anchor : List.of(membership.parent(), membership.child())) {
                var location = anchor.location();
                assertEquals("data/objects.json", location.owner().resourcePath());
                assertEquals(fingerprint, location.owner().resourceSha256());
                assertEquals(SyntaxOrigin.AUTHORED, location.origin());
                assertFalse(document.at(location.jsonPointer()).isMissingNode());
                var owner = document.at(location.owner().jsonPointer());
                assertEquals(objects.findById(membership.objectId()).name(), owner.path("name").asText());
                assertEquals(objects.findById(membership.objectId()).source().identity().code(),
                        owner.path("source").asText());
                assertFalse(location.hierarchy().isEmpty());
                for (var context : location.hierarchy()) {
                    JsonNode container = document.at(context.jsonPointer());
                    assertFalse(container.isMissingNode());
                    assertEquals(container.path("type").isTextual() ? container.path("type").textValue() : null,
                            context.authoredType());
                    assertTrue(location.jsonPointer().startsWith(context.jsonPointer() + "/"));
                }
                if (anchor.span() != null) {
                    assertEquals(membership.observation().tag().originalText(),
                            anchor.span().extract(document.at(location.jsonPointer()).textValue()));
                }
            }
            assertNull(membership.parent().span());
        }
    }

    @Test
    void preservesExactMetadataWithoutInterpretingCodes() {
        var values = memberships.index().memberships().stream()
                .filter(value -> value.kind() == ObjectMembershipKind.HAS_AUTHORED_ATTACK_TYPE).toList();
        assertEquals(List.of("MW", "RW", "RW", "RW", "RW", "MW", "RW"),
                values.stream().map(ObjectEvidenceMembership::authoredValue).toList());
        assertTrue(values.stream().allMatch(value -> value.observation() == null && value.child().span() == null));
    }

    @Test
    void maintainsPerOwnerSequenceAndDeterministicSourceOrder() {
        var repeated = new ObjectEvidenceMembershipAdapter(mapper).adapt(objects.definitions());
        assertEquals(memberships.index().memberships(), repeated.memberships());
        for (var values : repeated.byObjectId().values()) {
            for (int index = 0; index < values.size(); index++) {
                assertEquals(index, values.get(index).occurrenceIndex());
            }
        }
    }

    @Test
    void resultsAndEvidenceHierarchiesAreImmutable() {
        var index = memberships.index();
        assertThrows(UnsupportedOperationException.class, () -> index.memberships().clear());
        assertThrows(UnsupportedOperationException.class, () -> index.byObjectId().clear());
        var values = index.findByObjectId("object:DMG:ballista");
        assertThrows(UnsupportedOperationException.class, () -> values.clear());
        assertThrows(UnsupportedOperationException.class, () -> values.getFirst().child().location().hierarchy().clear());
        var supplied = new ArrayList<>(index.memberships());
        var copy = new ObjectEvidenceMembershipIndex(new ArrayList<>(index.byObjectId().keySet()), supplied);
        supplied.clear();
        assertEquals(63, copy.memberships().size());
    }

    @Test
    void syntheticMultipleFormulasAndMembersDoNotCollapseLocations() throws Exception {
        var index = synthetic(List.of("{@hit +0}"), List.of("{@damage 1d6} plus {@damage 2d6}", "{@damage 1d6}"));
        var formulas = index.memberships().stream()
                .filter(value -> value.kind() == ObjectMembershipKind.SECTION_CONTAINS_FORMULA_TAG).toList();
        assertEquals(3, formulas.size());
        assertNotEquals(formulas.get(0).child(), formulas.get(2).child());
        assertEquals(formulas.get(0).child().location(), formulas.get(1).child().location());
        assertNotEquals(formulas.get(0).child().span(), formulas.get(1).child().span());
        assertTrue(formulas.get(2).child().location().jsonPointer().endsWith("/hitEntries/1"));
    }

    @Test
    void nestedTagsUnknownSyntaxAndUtf16SpansRemainEvidenceOnly() throws Exception {
        var index = synthetic(List.of(), List.of("😀 {@note {@damage 1d6}} {@future x}"));
        var tags = index.memberships().stream()
                .filter(value -> value.kind() == ObjectMembershipKind.ENTRY_CONTAINS_SYNTAX).toList();
        assertEquals(List.of("note", "damage", "future"),
                tags.stream().map(value -> value.observation().tag().name()).toList());
        assertEquals(3, tags.getFirst().child().span().startInclusive());
        assertEquals(10, tags.get(1).child().span().startInclusive());
        assertEquals(tags.getFirst().child().location(), tags.get(1).child().location());
        assertEquals(1, tags.getFirst().observation().tag().children().size());
    }

    @Test
    void emptySectionsAndRepeatedCorpusFormulasRemainSeparate() throws Exception {
        assertEquals(3, synthetic(List.of(), List.of()).memberships().size());
        var formulas = memberships.index().memberships().stream()
                .filter(value -> value.kind() == ObjectMembershipKind.SECTION_CONTAINS_FORMULA_TAG)
                .filter(value -> value.observation().tag().originalText().equals("{@damage 3d10}")).toList();
        assertEquals(2, formulas.size());
        assertNotEquals(formulas.getFirst().objectId(), formulas.getLast().objectId());
        assertNotEquals(formulas.getFirst().child().location(), formulas.getLast().child().location());
    }

    @Test
    void malformedSyntaxAndUnsupportedEntryShapesFailWithoutPartialOutput() {
        assertThrows(RuntimeException.class, () -> synthetic(List.of(), List.of("{@damage 1d6")));
        assertThrows(IllegalStateException.class, () -> adaptInvalidSource("""
                {"object":[{"name":"Test","source":"DMG","actionEntries":[{"type":"actions",
                "name":"Test","entries":[{"type":"attack","attackType":"RW","attackEntries":[],
                "hitEntries":[{"type":"entries","entries":["{@damage 1d6}"]}]}]}]}]}
                """));
    }

    @Test
    void mismatchedCatalogAndUnknownSourceFieldsAreRejected() throws Exception {
        assertThrows(IllegalStateException.class,
                () -> new ObjectEvidenceMembershipAdapter(mapper).adapt(List.of()));
        assertThrows(IllegalStateException.class,
                () -> adaptInvalidSource("{\"object\":[{\"name\":\"Test\",\"source\":\"DMG\",\"unexpected\":true}]}"));
    }

    @Test
    void rejectsCrossSectionAndCrossOwnerMembershipConstruction() {
        var all = memberships.index().memberships();
        var attackSection = all.stream()
                .filter(value -> value.kind() == ObjectMembershipKind.HAS_ATTACK_SECTION).findFirst().orElseThrow();
        var formula = all.stream()
                .filter(value -> value.kind() == ObjectMembershipKind.SECTION_CONTAINS_FORMULA_TAG)
                .findFirst().orElseThrow();
        assertThrows(IllegalArgumentException.class, () -> new ObjectEvidenceMembership(
                formula.objectId(), 0, formula.kind(), attackSection.child(), formula.child(),
                null, formula.observation()));
        var other = all.stream().filter(value -> !value.objectId().equals(formula.objectId())).findFirst().orElseThrow();
        assertThrows(IllegalArgumentException.class, () -> new ObjectEvidenceMembership(
                formula.objectId(), 0, formula.kind(), other.parent(), formula.child(),
                null, formula.observation()));
    }

    @Test
    void rejectsUnknownOwnersAndIncorrectSequenceInImmutableIndex() {
        var first = memberships.index().memberships().getFirst();
        assertThrows(IllegalArgumentException.class,
                () -> new ObjectEvidenceMembershipIndex(List.of(), List.of(first)));
        assertThrows(IllegalArgumentException.class,
                () -> new ObjectEvidenceMembershipIndex(List.of(first.objectId(), first.objectId()), List.of()));
        var second = memberships.index().memberships().get(1);
        assertThrows(IllegalArgumentException.class,
                () -> new ObjectEvidenceMembershipIndex(List.of(second.objectId()), List.of(second)));
    }

    @Test
    void publicProjectionAndExistingDefinitionsRemainIndependent() throws Exception {
        var before = mapper.writeValueAsString(objects.definitions());
        new ObjectEvidenceMembershipAdapter(mapper).adapt(objects.definitions());
        assertEquals(before, mapper.writeValueAsString(objects.definitions()));
        for (var definition : objects.definitions()) {
            JsonNode json = mapper.valueToTree(ObjectDetailDto.from(definition));
            assertFalse(json.has("memberships"));
            assertFalse(json.has("actions"));
            assertFalse(json.toString().contains("{@"));
        }
        assertEquals(37, objects.size());
    }

    private boolean matchesLabel(ObjectEvidenceMembership value, JsonNode label) {
        return value.kind().name().equals(label.path("relationship").asText())
                && matchesAnchor(value.parent(), label.path("subject"))
                && matchesAnchor(value.child(), label.path("object"));
    }

    private boolean matchesAnchor(ObjectEvidenceAnchor anchor, JsonNode expected) {
        if (!anchor.location().owner().resourcePath().equals(expected.path("resource").asText())
                || !anchor.location().jsonPointer().equals(expected.path("pointer").asText())) {
            return false;
        }
        if (!expected.has("span")) {
            return anchor.span() == null;
        }
        return anchor.span() != null
                && anchor.span().startInclusive() == expected.path("span").get(0).asInt()
                && anchor.span().endExclusive() == expected.path("span").get(1).asInt();
    }

    private ObjectEvidenceMembershipIndex synthetic(List<String> attackEntries, List<String> hitEntries)
            throws Exception {
        var document = mapper.createObjectNode();
        var owner = document.putArray("object").addObject();
        owner.put("name", "Test");
        owner.put("source", "DMG");
        var action = owner.putArray("actionEntries").addObject();
        action.put("type", "actions");
        action.put("name", "Test");
        var attack = action.putArray("entries").addObject();
        attack.put("type", "attack");
        attack.put("attackType", "RW");
        attack.set("attackEntries", mapper.valueToTree(attackEntries));
        attack.set("hitEntries", mapper.valueToTree(hitEntries));
        return syntheticSource(mapper.writeValueAsString(document));
    }

    private ObjectEvidenceMembershipIndex syntheticSource(String json) throws Exception {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        var typed = mapper.readValue(bytes, ObjectFileDte.class);
        var definitions = typed.object().stream().map(new ObjectDefinitionMapper()::map).toList();
        return new ObjectEvidenceMembershipAdapter(mapper, new ByteArrayResource(bytes)).adapt(definitions);
    }

    private ObjectEvidenceMembershipIndex adaptInvalidSource(String json) {
        return new ObjectEvidenceMembershipAdapter(mapper,
                new ByteArrayResource(json.getBytes(StandardCharsets.UTF_8))).adapt(List.of());
    }
}
