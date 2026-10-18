package com.thedndpub.data.item;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.item.syntax.ItemChargeSyntaxCategory;
import com.thedndpub.data.dto.item.syntax.ItemChargeSyntaxObservation;
import com.thedndpub.data.dto.item.ItemDto;
import com.thedndpub.data.mechanics.syntax.BalancedMarkupParser;
import com.thedndpub.data.mechanics.syntax.FormulaSyntaxObservation;
import com.thedndpub.data.mechanics.syntax.MarkupSyntaxException;
import com.thedndpub.data.mechanics.syntax.OpaqueSyntaxObservation;
import com.thedndpub.data.mechanics.syntax.OpaqueTagCategory;
import com.thedndpub.data.mechanics.syntax.SyntaxObservationClassifier;
import com.thedndpub.data.mechanics.syntax.SyntaxExpressionKind;
import com.thedndpub.data.mechanics.syntax.SyntaxSourceOwner;
import com.thedndpub.data.mechanics.syntax.SyntaxText;
import com.thedndpub.data.mechanics.syntax.SyntaxTextLocation;
import com.thedndpub.data.mechanics.syntax.SyntaxOrigin;
import com.thedndpub.data.services.ItemCatalog;
import com.thedndpub.data.services.ItemChargeSyntaxCatalog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ItemChargeSyntaxCatalogTests {
    @Autowired
    private ItemChargeSyntaxCatalog syntaxCatalog;

    @Autowired
    private ItemCatalog itemCatalog;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void indexesAuthoredChargeFieldAndEntrySyntaxWithoutMaterializingEffectiveCopies() {
        var index = syntaxCatalog.index();

        assertEquals(1, index.sourceFileCount());
        assertEquals(2_512, index.authoredItemCount());
        assertEquals(2_742, index.effectiveDefinitionCount());
        assertEquals(2_742, itemCatalog.size());
        assertEquals(1, index.observations().stream()
                .filter(value -> value.category() == ItemChargeSyntaxCategory.COUNT_FIELD_SYNTAX)
                .count());
        assertEquals(219, index.observations().stream()
                .filter(value -> value.category() == ItemChargeSyntaxCategory.RECOVERY_FIELD_SYNTAX)
                .count());
        assertEquals(469, index.observations().stream()
                .filter(value -> value.category() == ItemChargeSyntaxCategory.CHARGE_OWNER_ENTRY_FORMULA)
                .count());
        assertEquals(702, index.observations().stream()
                .filter(value -> value.category() == ItemChargeSyntaxCategory.CHARGE_MARKED_ENTRY_OTHER_SYNTAX)
                .count());
        assertEquals(295, index.authoredItemsWithObservations());
        assertFalse(index.effectiveObservationsMaterialized());
    }

    @Test
    void preservesSourcePointerHierarchyArgumentsUtf16SpansAndStableOrdering() throws Exception {
        JsonNode document = readItems();
        Map<String, Integer> nextOccurrence = new HashMap<>();
        Map<ItemChargeSyntaxCategory, Long> categories = new EnumMap<>(ItemChargeSyntaxCategory.class);

        for (ItemChargeSyntaxObservation value : syntaxCatalog.index().observations()) {
            var tag = value.observation().tag();
            var evidence = tag.evidence();
            String pointer = evidence.location().jsonPointer();
            JsonNode authoredTextNode = document.at(pointer);

            assertTrue(authoredTextNode.isTextual(), pointer);
            assertEquals(tag.originalText(), evidence.span().extract(authoredTextNode.textValue()));
            assertEquals(value.authoredItemId(), itemIdForPointer(pointer));
            assertEquals(nextOccurrence.getOrDefault(value.authoredItemId(), 0), value.occurrenceIndex());
            nextOccurrence.put(value.authoredItemId(), value.occurrenceIndex() + 1);
            categories.merge(value.category(), 1L, Long::sum);

            assertEquals("data/items.json", evidence.location().owner().resourcePath());
            assertEquals("item", evidence.location().owner().collection());
            SyntaxOrigin expectedOrigin = pointer.contains("/_copy/") || pointer.contains("/_mod/")
                    || pointer.contains("/_versions/")
                    ? SyntaxOrigin.AUTHORED_TRANSFORM_INSTRUCTION
                    : SyntaxOrigin.AUTHORED;
            assertEquals(expectedOrigin, evidence.location().origin());
            assertTrue(pointer.startsWith(evidence.location().owner().jsonPointer()));
        }

        assertEquals(220, categories.get(ItemChargeSyntaxCategory.COUNT_FIELD_SYNTAX)
                + categories.get(ItemChargeSyntaxCategory.RECOVERY_FIELD_SYNTAX));
        assertThrows(UnsupportedOperationException.class,
                () -> syntaxCatalog.index().observations().clear());
        assertThrows(UnsupportedOperationException.class,
                () -> syntaxCatalog.index().byAuthoredItemId().clear());
        assertThrows(UnsupportedOperationException.class,
                () -> syntaxCatalog.index().findByAuthoredItemId(
                        syntaxCatalog.index().observations().getFirst().authoredItemId()).clear());
    }

    @Test
    void distinguishesCountRecoveryAndChargeMarkedEntryFormulas() {
        var index = syntaxCatalog.index();
        var all = index.observations();

        var count = all.stream()
                .filter(value -> value.category() == ItemChargeSyntaxCategory.COUNT_FIELD_SYNTAX)
                .filter(value -> value.observation().tag().evidence().location().jsonPointer()
                        .equals("/item/2224/charges"))
                .findFirst()
                .orElseThrow();
        assertFormula(count, "{@dice 1d6 + 1}", "1d6 + 1", SyntaxExpressionKind.DICE_NOTATION);

        var recovery = all.stream()
                .filter(value -> value.category() == ItemChargeSyntaxCategory.RECOVERY_FIELD_SYNTAX)
                .filter(value -> value.observation().tag().evidence().location().jsonPointer()
                        .equals("/item/2408/rechargeAmount"))
                .findFirst()
                .orElseThrow();
        assertFormula(recovery, "{@dice 1d6 + 1}", "1d6 + 1", SyntaxExpressionKind.DICE_NOTATION);

        List<ItemChargeSyntaxObservation> entryFormulas = all.stream()
                .filter(value -> value.category() == ItemChargeSyntaxCategory.CHARGE_OWNER_ENTRY_FORMULA)
                .filter(value -> value.observation().tag().evidence().location().jsonPointer()
                        .equals("/item/2168/entries/1/entries/1"))
                .toList();
        assertEquals(2, entryFormulas.size());
        assertTrue(entryFormulas.stream().allMatch(value -> value.observation() instanceof FormulaSyntaxObservation));
        assertTrue(entryFormulas.stream().anyMatch(value -> value.observation().tag().originalText()
                .equals("{@dice 1d6}")));
    }

    @Test
    void preservesUnsupportedTagsAndRejectsMalformedSelectedSyntax() {
        var owner = new SyntaxSourceOwner("data/items.json", null, "item", "/item/0");
        var location = new SyntaxTextLocation(owner, "/item/0/entries/0", List.of(), SyntaxOrigin.AUTHORED);
        var parser = new BalancedMarkupParser();
        var classifier = new SyntaxObservationClassifier();

        var parsed = parser.parse(new SyntaxText(location, "Charge-like text {@futurecharge|opaque}"));
        var opaque = classifier.classify(parsed.tags().getFirst());
        assertTrue(opaque instanceof OpaqueSyntaxObservation);
        assertEquals(OpaqueTagCategory.UNSUPPORTED, ((OpaqueSyntaxObservation) opaque).category());
        assertEquals("{@futurecharge|opaque}", opaque.tag().originalText());
        assertEquals(List.of("", "opaque"), opaque.tag().arguments());

        assertThrows(MarkupSyntaxException.class,
                () -> parser.parse(new SyntaxText(location, "Charge-like text {@dice 1d6")));
    }

    @Test
    void keepsAdjacentSyntaxSeparateFromChargeSemanticsAndDoesNotAddBaseItemObservations() {
        var index = syntaxCatalog.index();
        List<ItemChargeSyntaxObservation> anotherItemsFormula = index.observations().stream()
                .filter(value -> value.observation().tag().evidence().location().jsonPointer()
                        .equals("/item/102/entries/0"))
                .toList();

        assertFalse(anotherItemsFormula.isEmpty());
        assertTrue(anotherItemsFormula.stream().allMatch(value -> value.category()
                == ItemChargeSyntaxCategory.CHARGE_OWNER_ENTRY_FORMULA
                || value.category() == ItemChargeSyntaxCategory.CHARGE_MARKED_ENTRY_OTHER_SYNTAX));
        assertTrue(index.authoredOwners().stream().noneMatch(owner -> owner.evidenceOwner().jsonPointer()
                .startsWith("/baseitem/")));
        assertTrue(index.observations().stream()
                .filter(value -> value.observation().tag().name().equalsIgnoreCase("recharge"))
                .allMatch(value -> value.category() == ItemChargeSyntaxCategory.CHARGE_MARKED_ENTRY_OTHER_SYNTAX));
    }

    @Test
    void reconcilesEligibleSyntaxGoldLabelsAndAbstainsFromChargeMechanics() throws Exception {
        JsonNode gold;
        try (InputStream input = java.nio.file.Files.newInputStream(Path.of(
                "analysis/mechanics/batch-11-item-charges/review-cases.json"))) {
            gold = objectMapper.readTree(input);
        }

        int acceptedEligible = 0;
        int abstainedSemanticClaims = 0;
        int rejectedOriginClaims = 0;
        for (JsonNode label : gold.path("labels")) {
            String scope = label.path("scope").asText();
            String decision = label.path("decision").asText();
            if (scope.equals("AUTHORED_FIELD_SYNTAX_POSITIVE")
                    || scope.equals("ENTRY_SYNTAX_CONTROL_OUTSIDE_INITIAL_SCOPE")) {
                String pointer = label.path("object").path("pointer").asText();
                JsonNode spanNode = label.path("object").path("span");
                int start = spanNode.get(0).asInt();
                int end = spanNode.get(1).asInt();
                String token = label.path("object").path("token").asText();
                assertEquals("ACCEPT", decision);
                assertTrue(syntaxCatalog.index().observations().stream().anyMatch(value -> {
                    var tag = value.observation().tag();
                    return tag.evidence().location().jsonPointer().equals(pointer)
                            && tag.evidence().span().startInclusive() == start
                            && tag.evidence().span().endExclusive() == end
                            && tag.originalText().equals(token);
                }), label.path("id").asText());
                acceptedEligible++;
            } else if (scope.equals("SEMANTIC_ABSTENTION")) {
                assertEquals("ABSTAIN", decision);
                abstainedSemanticClaims++;
            } else if (scope.equals("ORIGIN_NEGATIVE")) {
                assertEquals("REJECT", decision);
                String childPointer = label.path("subject").path("pointer").asText();
                int childIndex = Integer.parseInt(childPointer.split("/")[2]);
                String childItemId = syntaxCatalog.index().authoredOwners().get(childIndex).itemId();
                String parentTokenPointer = label.path("object").path("pointer").asText();
                assertTrue(syntaxCatalog.index().findByAuthoredItemId(childItemId).stream()
                        .noneMatch(value -> value.observation().tag().evidence().location().jsonPointer()
                                .equals(parentTokenPointer)));
                rejectedOriginClaims++;
            }
        }

        assertEquals(25, acceptedEligible);
        assertEquals(6, abstainedSemanticClaims);
        assertEquals(1, rejectedOriginClaims);
    }

    @Test
    void retainsTheExistingRechargeDeclarationAndPublicItemProjection() {
        var definition = itemCatalog.findByName("Ventilating Lungs").stream()
                .filter(value -> value.source().identity().code().equals("ERLW"))
                .findFirst()
                .orElseThrow();

        assertEquals("item:erlw:ventilating-lungs", definition.id());
        assertNotNull(definition.standaloneRecharge());
        assertEquals("dawn", definition.standaloneRecharge().authoredValue());

        JsonNode publicJson = objectMapper.valueToTree(ItemDto.from(definition));
        assertFalse(publicJson.has("standaloneRecharge"));
        assertFalse(publicJson.has("chargeSyntax"));
    }

    private JsonNode readItems() throws Exception {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("data/items.json")) {
            assertNotNull(input);
            return objectMapper.readTree(input);
        }
    }

    private String itemIdForPointer(String pointer) {
        String[] segments = pointer.split("/");
        int itemIndex = Integer.parseInt(segments[2]);
        var owner = syntaxCatalog.index().authoredOwners().get(itemIndex);
        return owner.itemId();
    }

    private static void assertFormula(
            ItemChargeSyntaxObservation observation,
            String originalText,
            String authoredToken,
            SyntaxExpressionKind expectedKind) {
        assertTrue(observation.observation() instanceof FormulaSyntaxObservation);
        var formula = (FormulaSyntaxObservation) observation.observation();
        assertEquals(originalText, formula.tag().originalText());
        assertEquals(List.of(authoredToken), formula.tag().arguments());
        assertEquals(authoredToken, formula.expression().authoredToken());
        assertEquals(expectedKind, formula.expression().kind());
    }
}
