package com.thedndpub.data.item;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.item.ItemDefinition;
import com.thedndpub.data.dto.item.ItemDefinitionKind;
import com.thedndpub.data.dto.item.ItemDto;
import com.thedndpub.data.dto.item.ItemMapper;
import com.thedndpub.data.dto.item.ItemSignedBonus;
import com.thedndpub.data.dto.item.ItemSignedBonusParser;
import com.thedndpub.data.dto.item.ItemSignedBonuses;
import com.thedndpub.data.dto.item.ResolvedItemDte;
import com.thedndpub.data.dte.item.BaseItemsFileDte;
import com.thedndpub.data.dte.item.ItemFileDte;
import com.thedndpub.data.dte.item.ItemSourceDte;
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
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ItemSignedBonusNormalizationTests {
    private static final List<String> BONUS_FIELDS = List.of(
            "bonusAc",
            "bonusWeapon",
            "bonusWeaponAttack",
            "bonusWeaponDamage",
            "bonusSpellAttack",
            "bonusSpellSaveDc",
            "bonusSavingThrow",
            "bonusSavingThrowConcentration",
            "bonusAbilityCheck",
            "bonusProficiencyBonus");

    private static final Map<String, Integer> AUTHORED_FIELD_COUNTS = Map.ofEntries(
            Map.entry("bonusAc", 85),
            Map.entry("bonusWeapon", 200),
            Map.entry("bonusWeaponAttack", 3),
            Map.entry("bonusWeaponDamage", 2),
            Map.entry("bonusSpellAttack", 53),
            Map.entry("bonusSpellSaveDc", 32),
            Map.entry("bonusSavingThrow", 29),
            Map.entry("bonusSavingThrowConcentration", 1),
            Map.entry("bonusAbilityCheck", 3),
            Map.entry("bonusProficiencyBonus", 2));

    private static final Map<String, Integer> EFFECTIVE_FIELD_COUNTS = Map.ofEntries(
            Map.entry("bonusAc", 88),
            Map.entry("bonusWeapon", 201),
            Map.entry("bonusWeaponAttack", 3),
            Map.entry("bonusWeaponDamage", 2),
            Map.entry("bonusSpellAttack", 59),
            Map.entry("bonusSpellSaveDc", 35),
            Map.entry("bonusSavingThrow", 29),
            Map.entry("bonusSavingThrowConcentration", 1),
            Map.entry("bonusAbilityCheck", 3),
            Map.entry("bonusProficiencyBonus", 2));

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ItemCatalog catalog;

    @Autowired
    private ItemMapper mapper;

    @Autowired
    private com.thedndpub.data.dto.item.ItemCopyResolver copyResolver;

    @Test
    void everyAuthoredBonusIsASignedIntegerStringWithExactFieldCounts() throws Exception {
        ItemFileDte items = resource("data/items.json", ItemFileDte.class);
        BaseItemsFileDte baseItems = resource("data/items-base.json", BaseItemsFileDte.class);
        Map<String, Integer> counts = new TreeMap<>();
        int affectedRoots = 0;
        int occurrences = 0;
        int positive = 0;
        int negative = 0;
        int zero = 0;
        int unsigned = 0;
        int explicitNulls = 0;

        JsonNode itemSource = sourceJson("data/items.json").path("item");
        for (int index = 0; index < items.item().size(); index++) {
            ItemSourceDte item = items.item().get(index);
            JsonNode raw = itemSource.get(index);
            int itemOccurrences = 0;
            for (String field : BONUS_FIELDS) {
                String token = sourceBonus(item, field);
                if (token == null) {
                    if (raw.has(field) && raw.get(field).isNull()) {
                        explicitNulls++;
                    }
                    assertFalse(raw.has(field) && !raw.get(field).isNull(), item.name() + ":" + field);
                    continue;
                }

                assertTrue(raw.get(field).isTextual(), item.name() + ":" + field);
                assertTrue(token.matches("[+-][0-9]+"), item.name() + ":" + field + "=" + token);
                int numericValue = Integer.parseInt(token);
                assertEquals(token, raw.get(field).textValue());
                counts.merge(field, 1, Integer::sum);
                itemOccurrences++;
                occurrences++;
                if (numericValue > 0) {
                    positive++;
                } else if (numericValue < 0) {
                    negative++;
                } else {
                    zero++;
                }
                if (!token.startsWith("+") && !token.startsWith("-")) {
                    unsigned++;
                }
            }
            if (itemOccurrences > 0) {
                affectedRoots++;
            }
        }

        for (ItemSourceDte baseItem : baseItems.baseitem()) {
            for (String field : BONUS_FIELDS) {
                assertNull(sourceBonus(baseItem, field), baseItem.name() + ":" + field);
            }
        }

        assertEquals(2_512, items.item().size());
        assertEquals(230, baseItems.baseitem().size());
        assertEquals(339, affectedRoots);
        assertEquals(410, occurrences);
        assertEquals(AUTHORED_FIELD_COUNTS, counts);
        assertEquals(408, positive);
        assertEquals(2, negative);
        assertEquals(0, zero);
        assertEquals(0, unsigned);
        assertEquals(0, explicitNulls);
    }

    @Test
    void allEffectiveItemAndBaseItemDefinitionsRetainAuthoredTokensAndNumericValues() throws Exception {
        ItemFileDte items = resource("data/items.json", ItemFileDte.class);
        BaseItemsFileDte baseItems = resource("data/items-base.json", BaseItemsFileDte.class);
        List<ResolvedItemDte> resolvedItems = copyResolver.resolve(items.item());
        Map<String, Integer> effectiveCounts = new TreeMap<>();
        Map<String, Integer> inheritedCounts = new TreeMap<>();
        int affectedDefinitions = 0;
        int effectiveOccurrences = 0;
        int inheritedDefinitions = 0;
        int inheritedOccurrences = 0;
        Set<String> mappedIds = new HashSet<>();

        for (ResolvedItemDte resolved : resolvedItems) {
            ItemDefinition mapped = mapper.map(resolved, ItemDefinitionKind.ITEM, Map.of());
            ItemDefinition catalogDefinition = catalog.findById(mapped.id());
            assertEquals(mapped.id(), catalogDefinition.id());
            assertEquals(mapped.signedBonuses(), catalogDefinition.signedBonuses());
            assertTrue(mappedIds.add(mapped.id()));

            int itemOccurrences = 0;
            int authoredOccurrences = 0;
            for (String field : BONUS_FIELDS) {
                String effectiveToken = effectiveBonus(resolved, field);
                String authoredToken = sourceBonus(resolved.source(), field);
                assertEquals(effectiveToken, bonusToken(mapped.signedBonuses(), field),
                        mapped.name() + ":" + field);
                assertEquals(effectiveToken, legacyBonus(mapped.bonuses(), field),
                        mapped.name() + " legacy " + field);
                if (effectiveToken != null) {
                    ItemSignedBonus signedBonus = bonus(mapped.signedBonuses(), field);
                    assertEquals(effectiveToken, signedBonus.authoredToken());
                    assertEquals(Integer.parseInt(effectiveToken), signedBonus.numericValue());
                    effectiveCounts.merge(field, 1, Integer::sum);
                    itemOccurrences++;
                    effectiveOccurrences++;
                }
                if (authoredToken != null) {
                    authoredOccurrences++;
                } else if (effectiveToken != null) {
                    inheritedCounts.merge(field, 1, Integer::sum);
                    inheritedOccurrences++;
                }
            }
            if (itemOccurrences > 0) {
                affectedDefinitions++;
            }
            if (itemOccurrences > authoredOccurrences) {
                inheritedDefinitions++;
            }
        }

        for (ItemSourceDte baseItem : baseItems.baseitem()) {
            ItemDefinition mapped = mapper.map(new ResolvedItemDte(baseItem, null, false,
                    baseItem.property() == null ? List.of() : baseItem.property(),
                    baseItem.entries() == null ? List.of() : baseItem.entries()),
                    ItemDefinitionKind.BASE_ITEM, Map.of());
            assertNull(mapped.signedBonuses(), mapped.name());
            assertEquals(mapped.id(), catalog.findById(mapped.id()).id());
            assertTrue(mappedIds.add(mapped.id()));
        }

        assertEquals(2_742, catalog.size());
        assertEquals(2_742, mappedIds.size());
        assertEquals(349, affectedDefinitions);
        assertEquals(423, effectiveOccurrences);
        assertEquals(EFFECTIVE_FIELD_COUNTS, effectiveCounts);
        assertEquals(10, inheritedDefinitions);
        assertEquals(13, inheritedOccurrences);
        assertEquals(Map.of("bonusAc", 3, "bonusWeapon", 1, "bonusSpellAttack", 6,
                "bonusSpellSaveDc", 3), inheritedCounts);
    }

    @Test
    void signedBonusParserPreservesPositiveNegativeAndZeroTokens() {
        assertEquals(new ItemSignedBonus("+3", 3), ItemSignedBonusParser.parse("+3", "bonusWeapon"));
        assertEquals(new ItemSignedBonus("-2", -2), ItemSignedBonusParser.parse("-2", "bonusSavingThrow"));
        assertEquals(new ItemSignedBonus("+0", 0), ItemSignedBonusParser.parse("+0", "bonusAc"));
        assertEquals(new ItemSignedBonus("-0", 0), ItemSignedBonusParser.parse("-0", "bonusAc"));
        assertNull(ItemSignedBonusParser.parse(null, "bonusAc"));
    }

    @Test
    void parserRejectsUnsignedExpressionsMarkupWhitespaceAndOutOfRangeValues() {
        assertThrows(IllegalArgumentException.class, () -> ItemSignedBonusParser.parse("2", "bonusAc"));
        assertThrows(IllegalArgumentException.class, () -> ItemSignedBonusParser.parse("  +2", "bonusAc"));
        assertThrows(IllegalArgumentException.class, () -> ItemSignedBonusParser.parse("1d4", "bonusAc"));
        assertThrows(IllegalArgumentException.class,
                () -> ItemSignedBonusParser.parse("{@damage +1}", "bonusAc"));
        assertThrows(IllegalArgumentException.class,
                () -> ItemSignedBonusParser.parse("+2147483648", "bonusAc"));
    }

    @Test
    void sourceBonusFieldsRejectJsonNumbersInsteadOfCoercingThemIntoStrings() {
        String source = "{\"item\":[{\"name\":\"Synthetic Item\",\"source\":\"PHB\",\"bonusAc\":-2}]}";

        assertThrows(JsonMappingException.class, () -> objectMapper.readValue(source, ItemFileDte.class));
    }

    @Test
    void signedBonusValuesAreInternalAndItemFocusAndPublicProjectionRemainUnchanged() throws Exception {
        ItemDefinition definition = catalog.findById("item:tftyp:stone-of-ill-luck");
        assertEquals(new ItemSignedBonus("-2", -2), definition.signedBonuses().bonusSavingThrow());
        assertEquals(new ItemSignedBonus("-2", -2), definition.signedBonuses().bonusAbilityCheck());
        assertFalse(objectMapper.valueToTree(ItemDto.from(definition)).has("signedBonuses"));

        ItemDefinition focusItem = catalog.findById("item:ftd:slumbering-dragon-touched-focus");
        assertEquals(List.of("Artificer", "Bard", "Cleric", "Druid", "Paladin", "Ranger", "Sorcerer",
                "Warlock", "Wizard"), ((com.thedndpub.data.dto.item.ItemClassFocus)
                focusItem.focus()).classLabels());
    }

    @Test
    void signedBonusRecordsAreImmutableValueCarriers() {
        ItemSignedBonus signedBonus = new ItemSignedBonus("+2", 2);
        ItemSignedBonuses signedBonuses = new ItemSignedBonuses(signedBonus, null, null, null,
                null, null, null, null, null, null);

        assertEquals("+2", signedBonuses.bonusAc().authoredToken());
        assertEquals(2, signedBonuses.bonusAc().numericValue());
        assertFalse(signedBonuses.isEmpty());
        assertTrue(new ItemSignedBonuses(null, null, null, null, null, null, null, null, null, null).isEmpty());
    }

    private <T> T resource(String path, Class<T> type) throws IOException {
        try (var input = new ClassPathResource(path).getInputStream()) {
            return objectMapper.readerFor(type).readValue(input);
        }
    }

    private JsonNode sourceJson(String path) throws IOException {
        try (var input = new ClassPathResource(path).getInputStream()) {
            return objectMapper.readTree(input);
        }
    }

    private static String sourceBonus(ItemSourceDte source, String field) {
        return switch (field) {
            case "bonusAc" -> source.bonusAc();
            case "bonusWeapon" -> source.bonusWeapon();
            case "bonusWeaponAttack" -> source.bonusWeaponAttack();
            case "bonusWeaponDamage" -> source.bonusWeaponDamage();
            case "bonusSpellAttack" -> source.bonusSpellAttack();
            case "bonusSpellSaveDc" -> source.bonusSpellSaveDc();
            case "bonusSavingThrow" -> source.bonusSavingThrow();
            case "bonusSavingThrowConcentration" -> source.bonusSavingThrowConcentration();
            case "bonusAbilityCheck" -> source.bonusAbilityCheck();
            case "bonusProficiencyBonus" -> source.bonusProficiencyBonus();
            default -> throw new IllegalArgumentException("Unsupported Item bonus field: " + field);
        };
    }

    private static String effectiveBonus(ResolvedItemDte resolved, String field) {
        for (ResolvedItemDte current = resolved; current != null; current = current.copiedFrom()) {
            String value = sourceBonus(current.source(), field);
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    private static ItemSignedBonus bonus(ItemSignedBonuses values, String field) {
        if (values == null) {
            return null;
        }
        return switch (field) {
            case "bonusAc" -> values.bonusAc();
            case "bonusWeapon" -> values.bonusWeapon();
            case "bonusWeaponAttack" -> values.bonusWeaponAttack();
            case "bonusWeaponDamage" -> values.bonusWeaponDamage();
            case "bonusSpellAttack" -> values.bonusSpellAttack();
            case "bonusSpellSaveDc" -> values.bonusSpellSaveDc();
            case "bonusSavingThrow" -> values.bonusSavingThrow();
            case "bonusSavingThrowConcentration" -> values.bonusSavingThrowConcentration();
            case "bonusAbilityCheck" -> values.bonusAbilityCheck();
            case "bonusProficiencyBonus" -> values.bonusProficiencyBonus();
            default -> throw new IllegalArgumentException("Unsupported Item bonus field: " + field);
        };
    }

    private static String bonusToken(ItemSignedBonuses values, String field) {
        ItemSignedBonus value = bonus(values, field);
        return value == null ? null : value.authoredToken();
    }

    private static String legacyBonus(com.thedndpub.data.dto.item.ItemBonusesDto values, String field) {
        if (values == null) {
            return null;
        }
        return switch (field) {
            case "bonusAc" -> values.armorClass();
            case "bonusWeapon" -> values.weapon();
            case "bonusWeaponAttack" -> values.weaponAttack();
            case "bonusWeaponDamage" -> values.weaponDamage();
            case "bonusSpellAttack" -> values.spellAttack();
            case "bonusSpellSaveDc" -> values.spellSaveDc();
            case "bonusSavingThrow" -> values.savingThrow();
            case "bonusSavingThrowConcentration" -> values.savingThrowConcentration();
            case "bonusAbilityCheck" -> values.abilityCheck();
            case "bonusProficiencyBonus" -> values.proficiencyBonus();
            default -> throw new IllegalArgumentException("Unsupported Item bonus field: " + field);
        };
    }
}
