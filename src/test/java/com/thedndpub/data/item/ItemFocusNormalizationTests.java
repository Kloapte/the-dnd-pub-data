package com.thedndpub.data.item;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.thedndpub.data.controllers.ItemsController;
import com.thedndpub.data.dto.item.ItemBooleanFocus;
import com.thedndpub.data.dto.item.ItemClassFocus;
import com.thedndpub.data.dto.item.ItemCopyResolver;
import com.thedndpub.data.dto.item.ItemDefinition;
import com.thedndpub.data.dto.item.ItemDefinitionKind;
import com.thedndpub.data.dto.item.ItemDto;
import com.thedndpub.data.dto.item.ItemMapper;
import com.thedndpub.data.dto.item.ResolvedItemDte;
import com.thedndpub.data.dto.magicvariant.MagicVariantMatcher;
import com.thedndpub.data.dte.item.BaseItemsFileDte;
import com.thedndpub.data.dte.item.ItemFileDte;
import com.thedndpub.data.dte.item.ItemSourceDte;
import com.thedndpub.data.dte.item.ItemStringListDte;
import com.thedndpub.data.references.CrossCatalogResolutionCoordinator;
import com.thedndpub.data.references.ResolutionOutcome;
import com.thedndpub.data.services.ItemCatalog;
import com.thedndpub.data.services.ItemsService;
import com.thedndpub.data.services.MagicVariantCatalog;
import com.thedndpub.data.services.SourceRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ItemFocusNormalizationTests {
    @TempDir
    private Path temporaryDirectory;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ItemCatalog catalog;

    @Autowired
    private ItemsService service;

    @Autowired
    private MagicVariantCatalog variants;

    @Autowired
    private CrossCatalogResolutionCoordinator references;

    @Autowired
    private ItemMapper mapper;

    @Autowired
    private ItemCopyResolver copyResolver;

    @Autowired
    private SourceRegistry sourceRegistry;

    @Test
    void identitiesExistingNormalizedFieldsPublicJsonMagicVariantsAndReferencesRemainUnchanged() throws Exception {
        List<String> identities = catalog.summaries().stream().map(summary -> summary.id()).sorted().toList();
        assertEquals(2_742, identities.size());
        List<ItemDefinition> definitions = identities.stream().map(catalog::findById).toList();
        ArrayNode existingNormalized = objectMapper.valueToTree(definitions);
        existingNormalized.forEach(value -> {
            ((ObjectNode) value).remove("focus");
            ((ObjectNode) value).remove("signedBonuses");
        });
        Map<String, List<String>> eligibleItems = new TreeMap<>();
        for (var variant : variants.definitions()) {
            var source = variants.findImportProvenance(variant.id()).resolvedRecord().source();
            eligibleItems.put(variant.id(), catalog.variantMatchData().stream()
                    .filter(item -> MagicVariantMatcher.matchesAnyRequirement(source.requires(), item))
                    .filter(item -> !MagicVariantMatcher.isExcluded(source.excludes(), item))
                    .map(item -> item.itemId()).toList());
        }
        // Captured before production changes; only the new internal focus component is excluded.
        assertFingerprint("36ede70df35319da3fb1613c7c59a61594972320518ca2afc832f3c3ba5f4978", identities);
        assertFingerprint("2342fb27f7ceb0c1510b1c3e1c9ea41185eaad796ec9116e89dad12868bf8be5", existingNormalized);
        assertFingerprint("5ef6a9e613eddba8a0ced7604832f41b04fc358dfc873e8a065c045a8153dd2d",
                identities.stream().map(service::byId).toList());
        assertFingerprint("8b34e73c664796ead49814114e9c51d8eb638700af86dfbe4df1061f25772e4b", catalog.summaries());
        assertFingerprint("a5a268b63559f284b332a872be9ecc4abc5af68883e8afc939cfd23b189ec27d", catalog.variantMatchData());
        assertFingerprint("e422dd207791bbf3e7562a90207c3c48428912142b91e50b713f294d4c72f198", eligibleItems);
        assertFingerprint("4ac15cd5555e45c131804cb3dfa559a41506194025e803bd4023add5fac0e258", variants.definitions());
        assertFingerprint("ac512b43187d5160dc4b5fde29723b036bc93a5a79674ce30e20e1f376eae45b", variants.statistics());
        assertFingerprint("bdd0c7e036b87b211456b43a463a6781a0a7754aab638b0fbc6f5050a1f48036",
                references.snapshot().results());
        assertEquals(6_246, references.snapshot().results().size());
        assertEquals(Map.of(ResolutionOutcome.RESOLVED, 2_517L, ResolutionOutcome.AMBIGUOUS, 2_054L,
                ResolutionOutcome.UNRESOLVED, 181L, ResolutionOutcome.DEFERRED, 1_494L),
                references.snapshot().countsByOutcome());
    }

    @Test
    void allAuthoredArraysAndBothBooleansPreserveTheirExactSourceVariants() throws Exception {
        ItemFileDte items = resource("data/items.json", ItemFileDte.class);
        int arrays = 0;
        int booleans = 0;
        Set<List<String>> variants = new HashSet<>();
        for (ItemSourceDte source : items.item()) {
            if (source.focus() == null) {
                continue;
            }
            ItemDefinition definition = definition(source);
            assertFocus(source.focus(), definition);
            if (source.focus().enabled() == null) {
                arrays++;
                variants.add(source.focus().values());
                assertFalse(source.focus().values().isEmpty());
            } else {
                booleans++;
                assertEquals("Ruby of the War Mage", source.name());
                assertTrue(Set.of("XGE", "XDMG").contains(source.source()));
                assertEquals(Boolean.TRUE, definition.focusEnabled());
            }
        }
        assertEquals(52, arrays);
        assertEquals(2, booleans);
        assertEquals(Set.of(List.of("Artificer"), List.of("Wizard"), List.of("Sorcerer"),
                List.of("Druid", "Ranger"), List.of("Druid", "Warlock"),
                List.of("Sorcerer", "Warlock", "Wizard"), List.of("Warlock"),
                List.of("Artificer", "Bard", "Cleric", "Druid", "Paladin", "Ranger", "Sorcerer", "Warlock", "Wizard")),
                variants);

        // Group selectors remain source-only auxiliary records, not additional Item definitions.
        assertEquals(7, items.itemGroup().stream().filter(group -> group.focus() != null).count());
        assertEquals(230, resource("data/items-base.json", BaseItemsFileDte.class).baseitem().stream()
                .filter(baseItem -> baseItem.focus() == null).count());
    }

    @Test
    void everyEffectiveDefinitionAndThreeInheritedFocusChainsAreAccountedFor() throws Exception {
        ItemFileDte items = resource("data/items.json", ItemFileDte.class);
        List<ResolvedItemDte> resolved = new ArrayList<>(copyResolver.resolve(items.item()));
        for (ItemSourceDte baseItem : resource("data/items-base.json", BaseItemsFileDte.class).baseitem()) {
            resolved.add(new ResolvedItemDte(baseItem, null, false, List.of(), List.of()));
        }
        int arrays = 0;
        int booleans = 0;
        int absent = 0;
        Map<String, Integer> inheritedChains = new TreeMap<>();
        for (ResolvedItemDte item : resolved) {
            ItemDefinition definition = definition(item.source());
            ResolvedItemDte origin = item;
            int copyDepth = 0;
            while (origin.source().focus() == null && origin.copiedFrom() != null) {
                origin = origin.copiedFrom();
                copyDepth++;
            }
            ItemStringListDte expected = origin.source().focus();
            assertFocus(expected, definition);
            if (expected == null) {
                absent++;
            } else if (expected.enabled() != null) {
                booleans++;
            } else {
                arrays++;
            }
            if (expected != null && copyDepth > 0) {
                inheritedChains.put(item.source().name(), copyDepth);
                assertEquals("Slumbering Dragon-Touched Focus", origin.source().name());
                assertEquals("FTD", origin.source().source());
            }
        }
        assertEquals(2_742, resolved.size());
        assertEquals(2_742, catalog.size());
        assertEquals(67, catalog.expandedCopyCount());
        assertEquals(55, arrays);
        assertEquals(2, booleans);
        assertEquals(2_685, absent);
        assertEquals(Map.of("Stirring Dragon-Touched Focus", 1, "Wakened Dragon-Touched Focus", 2,
                "Ascendant Dragon-Touched Focus", 3), inheritedChains);
    }

    @Test
    void supportedFalseEmptyAbsentAndNullInputsKeepBooleanCompatibility() throws Exception {
        ItemDefinition enabled = synthetic("Enabled", "true");
        ItemDefinition disabled = synthetic("Disabled", "false");
        ItemDefinition empty = synthetic("Empty", "[]");
        ItemDefinition absent = synthetic("Absent", null);
        ItemDefinition explicitNull = synthetic("Null", "null");
        assertEquals(new ItemBooleanFocus(true), enabled.focus());
        assertEquals(new ItemBooleanFocus(false), disabled.focus());
        assertEquals(Boolean.TRUE, enabled.focusEnabled());
        assertEquals(Boolean.FALSE, disabled.focusEnabled());
        assertEquals(new ItemClassFocus(List.of()), empty.focus());
        assertNull(empty.focusEnabled());
        assertNull(absent.focus());
        assertNull(absent.focusEnabled());
        assertNull(explicitNull.focus());
        assertNull(explicitNull.focusEnabled());

        JsonNode disabledJson = objectMapper.valueToTree(ItemDto.from(disabled));
        assertTrue(disabledJson.has("focusEnabled"));
        assertFalse(disabledJson.get("focusEnabled").booleanValue());
        for (ItemDefinition definition : List.of(enabled, disabled, empty, absent, explicitNull)) {
            assertFalse(objectMapper.valueToTree(ItemDto.from(definition)).has("focus"));
        }
    }

    @Test
    void copyOverridesPreserveFalseEmptyAndOrderedLabelsWithoutInferringEligibility() throws Exception {
        List<ItemSourceDte> sources = new ArrayList<>();
        sources.add(source("""
                {"name":"Parent","source":"PHB","focus":["Wizard","Sorcerer","Wizard"," WIZARD "]}
                """));
        sources.add(source("""
                {"name":"Inherited","source":"PHB","_copy":{"name":"Parent","source":"PHB"}}
                """));
        sources.add(source("""
                {"name":"Null Child","source":"PHB","focus":null,"_copy":{"name":"Parent","source":"PHB"}}
                """));
        sources.add(source("""
                {"name":"False Child","source":"PHB","focus":false,"_copy":{"name":"Parent","source":"PHB"}}
                """));
        sources.add(source("""
                {"name":"False Grandchild","source":"PHB","_copy":{"name":"False Child","source":"PHB"}}
                """));
        sources.add(source("""
                {"name":"Empty Child","source":"PHB","focus":[],"_copy":{"name":"Parent","source":"PHB"}}
                """));
        sources.add(source("""
                {"name":"Label Override","source":"PHB","focus":["Sorcerer","Wizard"],
                 "_copy":{"name":"Parent","source":"PHB"}}
                """));
        Map<String, ItemDefinition> definitions = new TreeMap<>();
        for (ResolvedItemDte resolved : copyResolver.resolve(sources)) {
            definitions.put(resolved.source().name(), mapper.map(resolved, ItemDefinitionKind.ITEM, Map.of()));
        }
        ItemClassFocus parent = new ItemClassFocus(List.of("Wizard", "Sorcerer", "Wizard", " WIZARD "));
        for (String name : List.of("Parent", "Inherited", "Null Child")) {
            assertEquals(parent, definitions.get(name).focus());
            assertNull(definitions.get(name).focusEnabled());
            assertEquals(parent.classLabels(), definitions.get(name).classifications());
        }
        for (String name : List.of("False Child", "False Grandchild")) {
            assertEquals(new ItemBooleanFocus(false), definitions.get(name).focus());
            assertEquals(Boolean.FALSE, definitions.get(name).focusEnabled());
        }
        assertEquals(new ItemClassFocus(List.of()), definitions.get("Empty Child").focus());
        assertNull(definitions.get("Empty Child").focusEnabled());
        assertTrue(definitions.get("Empty Child").classifications().isEmpty());
        assertEquals(new ItemClassFocus(List.of("Sorcerer", "Wizard")), definitions.get("Label Override").focus());
    }

    @Test
    void normalizedClassLabelsAreDefensivelyCopiedAndImmutable() {
        List<String> labels = new ArrayList<>(List.of("Wizard", "Sorcerer", "Wizard", " WIZARD "));
        ItemClassFocus focus = new ItemClassFocus(labels);
        labels.clear();
        assertEquals(List.of("Wizard", "Sorcerer", "Wizard", " WIZARD "), focus.classLabels());
        assertThrows(UnsupportedOperationException.class, () -> focus.classLabels().clear());
        assertThrows(UnsupportedOperationException.class,
                () -> new ItemClassFocus(List.of()).classLabels().add("Wizard"));
        assertThrows(NullPointerException.class, () -> new ItemClassFocus(null));
    }

    @Test
    void strictFocusDeserializationRejectsUnsupportedShapesAndNonStringElements() {
        for (String invalid : List.of("\"Wizard\"", "0", "{}", "{\"enabled\":true}", "[1]", "[1.5]",
                "[true]", "[false]", "[null]", "[[\"Wizard\"]]", "[{}]", "[\"Wizard\",1]")) {
            assertThrows(JsonMappingException.class,
                    () -> source("{\"name\":\"Invalid\",\"source\":\"PHB\",\"focus\":" + invalid + "}"), invalid);
        }
        assertThrows(JsonMappingException.class,
                () -> source("{\"name\":\"Unknown\",\"source\":\"PHB\",\"focus\":true,\"unknownFocus\":true}"));
        assertThrows(JsonMappingException.class,
                () -> source("{\"name\":\"Version\",\"source\":\"PHB\",\"_versions\":[]}"));
    }

    @Test
    void existingItemRoutesKeepTheirPublicFocusFlagAndPublishNoNewFocusField() throws Exception {
        var mvc = MockMvcBuilders.standaloneSetup(new ItemsController(service)).build();
        for (String id : List.of("item:tce:1-all-purpose-tool", "item:xge:ruby-of-the-war-mage",
                "item:ftd:ascendant-dragon-touched-focus", "item:phb:longsword")) {
            JsonNode response = objectMapper.readTree(mvc.perform(get("/api/items/" + id))
                    .andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
            assertEquals(objectMapper.valueToTree(service.byId(id)), response);
            assertFalse(response.has("focus"));
            if (id.equals("item:xge:ruby-of-the-war-mage")) {
                assertTrue(response.get("focusEnabled").booleanValue());
            } else {
                assertFalse(response.has("focusEnabled"));
            }
        }
        JsonNode byName = objectMapper.readTree(mvc.perform(get("/api/items/by-name/+1 All-Purpose Tool")
                .param("source", "TCE")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
        assertEquals(1, byName.size());
        assertFalse(byName.get(0).has("focus"));
        assertEquals(objectMapper.valueToTree(service.find(null, null, null, null)),
                objectMapper.readTree(mvc.perform(get("/api/items")).andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString()));
    }

    @Test
    void jarClasspathResourcesNormalizeEveryFocusWithoutFilesystemResourceAssumptions() throws Exception {
        Path jarPath = temporaryDirectory.resolve("item-focus-resources.jar");
        try (JarOutputStream jar = new JarOutputStream(Files.newOutputStream(jarPath))) {
            for (String resourcePath : List.of("data/items.json", "data/items-base.json")) {
                jar.putNextEntry(new JarEntry(resourcePath));
                try (InputStream input = new ClassPathResource(resourcePath).getInputStream()) {
                    input.transferTo(jar);
                }
                jar.closeEntry();
            }
        }
        try (URLClassLoader loader = new URLClassLoader(new URL[]{jarPath.toUri().toURL()}, null)) {
            ItemFileDte items;
            BaseItemsFileDte baseItems;
            try (InputStream input = new ClassPathResource("data/items.json", loader).getInputStream()) {
                items = objectMapper.readerFor(ItemFileDte.class)
                        .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readValue(input);
            }
            try (InputStream input = new ClassPathResource("data/items-base.json", loader).getInputStream()) {
                baseItems = objectMapper.readerFor(BaseItemsFileDte.class)
                        .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readValue(input);
            }
            ItemCatalog packaged = new ItemCatalog(items, baseItems, mapper, copyResolver, sourceRegistry);
            assertEquals(2_742, packaged.size());
            for (var summary : catalog.summaries()) {
                assertEquals(catalog.findById(summary.id()), packaged.findById(summary.id()));
            }
        }
    }

    private ItemDefinition definition(ItemSourceDte source) {
        return catalog.findByName(source.name()).stream()
                .filter(item -> item.source().identity().code().equals(source.source()))
                .findFirst().orElseThrow();
    }

    private static void assertFocus(ItemStringListDte source, ItemDefinition definition) {
        if (source == null) {
            assertNull(definition.focus(), definition.id());
            assertNull(definition.focusEnabled(), definition.id());
        } else if (source.enabled() != null) {
            ItemBooleanFocus focus = assertInstanceOf(ItemBooleanFocus.class, definition.focus(), definition.id());
            assertEquals(source.enabled(), focus.enabled(), definition.id());
            assertEquals(source.enabled(), definition.focusEnabled(), definition.id());
        } else {
            ItemClassFocus focus = assertInstanceOf(ItemClassFocus.class, definition.focus(), definition.id());
            assertEquals(source.values(), focus.classLabels(), definition.id());
            assertNull(definition.focusEnabled(), definition.id());
        }
    }

    private ItemDefinition synthetic(String name, String focusJson) throws Exception {
        String focusField = focusJson == null ? "" : ",\"focus\":" + focusJson;
        ItemSourceDte source = source("{\"name\":\"" + name + "\",\"source\":\"PHB\"" + focusField + "}");
        return mapper.map(new ResolvedItemDte(source, null, false, List.of(), List.of()),
                ItemDefinitionKind.ITEM, Map.of());
    }

    private ItemSourceDte source(String json) throws Exception {
        return objectMapper.readerFor(ItemSourceDte.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readValue(json);
    }

    private <T> T resource(String path, Class<T> type) throws Exception {
        try (InputStream input = new ClassPathResource(path).getInputStream()) {
            return objectMapper.readerFor(type).with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .readValue(input);
        }
    }

    private void assertFingerprint(String expected, Object value) throws Exception {
        assertEquals(expected, fingerprint(objectMapper.valueToTree(value)));
    }

    private String fingerprint(JsonNode value) throws Exception {
        byte[] bytes = objectMapper.writeValueAsBytes(canonical(value));
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
    }

    private JsonNode canonical(JsonNode value) {
        if (value.isObject()) {
            ObjectNode result = objectMapper.createObjectNode();
            TreeSet<String> names = new TreeSet<>();
            value.fieldNames().forEachRemaining(names::add);
            for (String name : names) {
                result.set(name, canonical(value.get(name)));
            }
            return result;
        }
        if (value.isArray()) {
            ArrayNode result = objectMapper.createArrayNode();
            value.forEach(child -> result.add(canonical(child)));
            return result;
        }
        return value;
    }
}
