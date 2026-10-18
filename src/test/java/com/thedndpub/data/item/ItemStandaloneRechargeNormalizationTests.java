package com.thedndpub.data.item;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.item.ItemCopyResolver;
import com.thedndpub.data.dto.item.ItemDefinition;
import com.thedndpub.data.dto.item.ItemDefinitionKind;
import com.thedndpub.data.dto.item.ItemDto;
import com.thedndpub.data.dto.item.ItemMapper;
import com.thedndpub.data.dto.item.ItemRechargeDeclaration;
import com.thedndpub.data.dto.source.SourceReference;
import com.thedndpub.data.dte.item.ItemFileDte;
import com.thedndpub.data.dte.item.ItemRechargeDte;
import com.thedndpub.data.services.ItemCatalog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ItemStandaloneRechargeNormalizationTests {
    private static final Set<String> EXPECTED_STANDALONE_IDS = Set.of(
            "item:dmg:bag-of-tricks-gray",
            "item:xdmg:bag-of-tricks-gray",
            "item:dmg:bag-of-tricks-rust",
            "item:xdmg:bag-of-tricks-rust",
            "item:dmg:bag-of-tricks-tan",
            "item:xdmg:bag-of-tricks-tan",
            "item:bmt:balance-card",
            "item:bmt:comet-card",
            "item:bmt:donjon-card",
            "item:bmt:euryale-card",
            "item:bmt:fates-card",
            "item:bmt:flames-card",
            "item:bmt:fool-card",
            "item:bmt:gem-card",
            "item:bmt:jester-card",
            "item:bmt:knight-card",
            "item:bmt:moon-card",
            "item:bmt:puzzle-card",
            "item:bmt:rogue-card",
            "item:bmt:ruin-card",
            "item:bmt:sage-card",
            "item:bmt:skull-card",
            "item:bmt:star-card",
            "item:bmt:sun-card",
            "item:bmt:talons-card",
            "item:bmt:throne-card",
            "item:bmt:void-card",
            "item:erlw:ventilating-lungs");

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ItemCatalog catalog;

    @Autowired
    private ItemMapper mapper;

    @Autowired
    private ItemCopyResolver copyResolver;

    @Test
    void preservesAllTwentyEightAuthoredStandaloneRechargeDeclarations() throws Exception {
        ItemFileDte items = resource("data/items.json", ItemFileDte.class);
        JsonNode sourceItems = sourceJson().path("item");
        List<com.thedndpub.data.dto.item.ResolvedItemDte> resolvedItems = copyResolver.resolve(items.item());
        Set<String> expectedIds = new HashSet<>();
        int rawOccurrences = 0;

        for (int index = 0; index < items.item().size(); index++) {
            var source = items.item().get(index);
            JsonNode raw = sourceItems.get(index);
            boolean standalone = source.recharge() != null
                    && source.recharge().present()
                    && source.charges() == null
                    && source.rechargeAmount() == null;
            if (!standalone) {
                continue;
            }

            assertTrue(raw.path("recharge").isTextual(), source.name());
            assertTrue(raw.has("recharge"), source.name());
            assertEquals("dawn", source.recharge().value(), source.name());

            var resolved = resolvedItems.get(index);
            ItemDefinition definition = mapper.map(resolved, ItemDefinitionKind.ITEM, Map.of());
            ItemRechargeDeclaration declaration = definition.standaloneRecharge();
            assertNotNull(declaration, definition.id());
            assertEquals("dawn", declaration.authoredValue(), definition.id());
            assertEquals(source.name(), declaration.authoredOwnerName(), definition.id());
            assertEquals(source.source(), declaration.authoredSource().identity().code(), definition.id());
            assertEquals(source.page(), declaration.authoredSource().page(), definition.id());
            assertNull(definition.charges(), definition.id());
            assertFalse(objectMapper.valueToTree(ItemDto.from(definition)).has("standaloneRecharge"));
            assertTrue(expectedIds.add(definition.id()), definition.id());
            rawOccurrences++;
        }

        assertEquals(28, rawOccurrences);
        assertEquals(EXPECTED_STANDALONE_IDS, expectedIds);
        assertEquals(2_742, catalog.size());
        assertEquals(28, catalog.summaries().stream()
                .map(summary -> catalog.findById(summary.id()))
                .filter(definition -> definition.standaloneRecharge() != null)
                .count());
    }

    @Test
    void retainsPresenceAndExactStringValuesWhileRejectingUnsupportedJsonShapes() throws Exception {
        ItemFileDte file = objectMapper.readValue(
                "{\"item\":["
                        + "{\"name\":\"Absent\",\"source\":\"PHB\"},"
                        + "{\"name\":\"Present\",\"source\":\"PHB\",\"recharge\":\"dawn\"},"
                        + "{\"name\":\"Empty\",\"source\":\"PHB\",\"recharge\":\"\"},"
                        + "{\"name\":\"Null\",\"source\":\"PHB\",\"recharge\":null}"
                        + "]}",
                ItemFileDte.class);

        assertEquals(new ItemRechargeDte(null, false), file.item().get(0).recharge());
        assertEquals(new ItemRechargeDte("dawn", true), file.item().get(1).recharge());
        assertEquals(new ItemRechargeDte("", true), file.item().get(2).recharge());
        assertEquals(new ItemRechargeDte(null, true), file.item().get(3).recharge());

        Map<String, ItemDefinition> mappedValues = copyResolver.resolve(file.item()).stream()
                .map(resolved -> mapper.map(resolved, ItemDefinitionKind.ITEM, Map.of()))
                .collect(java.util.stream.Collectors.toMap(ItemDefinition::name, value -> value));
        assertNull(mappedValues.get("Absent").standaloneRecharge());
        assertEquals(new ItemRechargeDeclaration("", "Empty",
                        new SourceReference("PHB", null)),
                mappedValues.get("Empty").standaloneRecharge());
        assertEquals(new ItemRechargeDeclaration(null, "Null",
                        new SourceReference("PHB", null)),
                mappedValues.get("Null").standaloneRecharge());

        for (String value : List.of("5", "true", "[]", "{}")) {
            String json = "{\"item\":[{\"name\":\"Invalid\",\"source\":\"PHB\",\"recharge\":"
                    + value + "}]}";
            assertThrows(JsonMappingException.class, () -> objectMapper.readValue(json, ItemFileDte.class), value);
        }
    }

    @Test
    void copyInheritanceAndLocalOverrideRetainTheAuthoredOwner() throws Exception {
        ItemFileDte items = objectMapper.readValue(
                "{\"item\":["
                        + "{\"name\":\"Recharge Parent\",\"source\":\"PHB\",\"recharge\":\"dawn\"},"
                        + "{\"name\":\"Inherited Child\",\"source\":\"XGE\",\"_copy\":{"
                        + "\"name\":\"Recharge Parent\",\"source\":\"PHB\"}},"
                        + "{\"name\":\"Override Child\",\"source\":\"DMG\",\"recharge\":\"dusk\","
                        + "\"_copy\":{\"name\":\"Recharge Parent\",\"source\":\"PHB\"}}"
                        + "]}",
                ItemFileDte.class);

        Map<String, ItemDefinition> mapped = copyResolver.resolve(items.item()).stream()
                .map(resolved -> mapper.map(resolved, ItemDefinitionKind.ITEM, Map.of()))
                .collect(java.util.stream.Collectors.toMap(ItemDefinition::name, value -> value));

        ItemRechargeDeclaration inherited = mapped.get("Inherited Child").standaloneRecharge();
        assertEquals("dawn", inherited.authoredValue());
        assertEquals("Recharge Parent", inherited.authoredOwnerName());
        assertEquals("PHB", inherited.authoredSource().identity().code());

        ItemRechargeDeclaration overridden = mapped.get("Override Child").standaloneRecharge();
        assertEquals("dusk", overridden.authoredValue());
        assertEquals("Override Child", overridden.authoredOwnerName());
        assertEquals("DMG", overridden.authoredSource().identity().code());
    }

    @Test
    void rechargeDeclarationsAreImmutableValuesAndDoNotChangeExistingChargeProjection() {
        ItemRechargeDeclaration first = catalog.findById("item:dmg:bag-of-tricks-gray").standaloneRecharge();
        ItemRechargeDeclaration second = catalog.findById("item:xdmg:bag-of-tricks-gray").standaloneRecharge();
        assertEquals(first, new ItemRechargeDeclaration("dawn", "Bag of Tricks, Gray", first.authoredSource()));
        assertEquals("DMG", first.authoredSource().identity().code());
        assertEquals("XDMG", second.authoredSource().identity().code());

        ItemDefinition wand = catalog.findById("item:dmg:wand-of-magic-missiles");
        assertNotNull(wand.charges());
        assertEquals("dawn", wand.charges().recharge());
        assertNull(wand.standaloneRecharge());
    }

    private <T> T resource(String path, Class<T> type) throws IOException {
        try (var input = new ClassPathResource(path).getInputStream()) {
            return objectMapper.readerFor(type).readValue(input);
        }
    }

    private JsonNode sourceJson() throws IOException {
        try (var input = new ClassPathResource("data/items.json").getInputStream()) {
            return objectMapper.readTree(input);
        }
    }
}
