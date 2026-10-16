package com.thedndpub.data.item;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.controllers.ItemsController;
import com.thedndpub.data.controllers.MonsterApiExceptionHandler;
import com.thedndpub.data.dto.item.ItemDefinitionKind;
import com.thedndpub.data.dto.item.ItemDto;
import com.thedndpub.data.dto.item.ItemMapper;
import com.thedndpub.data.dto.item.ItemCopyResolver;
import com.thedndpub.data.dte.item.BaseItemsFileDte;
import com.thedndpub.data.dte.item.ItemFileDte;
import com.thedndpub.data.dte.item.ItemSourceDte;
import com.thedndpub.data.services.ItemCatalog;
import com.thedndpub.data.services.ItemsService;
import com.thedndpub.data.services.SourceRegistry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ItemPipelineTests {
    @TempDir
    private Path temporaryDirectory;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ItemCatalog catalog;

    @Autowired
    private ItemsService service;

    @Autowired
    private ItemMapper mapper;

    @Autowired
    private ItemCopyResolver copyResolver;

    @Autowired
    private SourceRegistry sourceRegistry;

    @Test
    void bothResourcesDeserializeStrictlyAndIncludeEveryRootCollection() throws Exception {
        ItemFileDte items;
        BaseItemsFileDte baseItems;
        try (InputStream input = new ClassPathResource("data/items.json").getInputStream()) {
            items = objectMapper.readerFor(ItemFileDte.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readValue(input);
        }
        try (InputStream input = new ClassPathResource("data/items-base.json").getInputStream()) {
            baseItems = objectMapper.readerFor(BaseItemsFileDte.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readValue(input);
        }

        assertEquals(2512, items.item().size());
        assertEquals(119, items.itemGroup().size());
        assertEquals(230, baseItems.baseitem().size());
        assertEquals(27, baseItems.itemProperty().size());
        assertEquals(67, baseItems.itemType().size());
        assertEquals(2, baseItems.itemTypeAdditionalEntries().size());
        assertEquals(13, baseItems.itemEntry().size());
        assertEquals(8, baseItems.itemMastery().size());
    }

    @Test
    void catalogAccountsForDefinitionsAndResolvesEveryCopyRoot() {
        assertEquals(2512, catalog.itemRootCount());
        assertEquals(230, catalog.baseItemRootCount());
        assertEquals(67, catalog.expandedCopyCount());
        assertEquals(2742, catalog.size());
        assertEquals(2742, catalog.summaries().size());
        assertEquals(catalog.size(), new HashSet<>(catalog.summaries().stream()
                .map(summary -> summary.id()).toList()).size());
    }

    @Test
    void baseItemsAndConcreteItemsShareCatalogButKeepKindsAndSources() {
        ItemDto phbLongsword = service.byId("item:phb:longsword");
        ItemDto xphbLongsword = service.byId("item:xphb:longsword");

        assertEquals(ItemDefinitionKind.BASE_ITEM, phbLongsword.definitionKind());
        assertEquals(ItemDefinitionKind.BASE_ITEM, xphbLongsword.definitionKind());
        assertEquals("PHB", phbLongsword.source().identity().code());
        assertEquals("XPHB", xphbLongsword.source().identity().code());
        assertEquals(2, service.byName("Longsword", null).size());
    }

    @Test
    void weaponArmorAndEquipmentMechanicsAreProjectedAsStructuredValues() {
        ItemDto longsword = service.byId("item:phb:longsword");
        ItemDto chainMail = service.byId("item:phb:chain-mail");
        ItemDto explorersPack = service.byId("item:phb:explorer-s-pack");

        assertEquals("1d8", longsword.damage().primary());
        assertEquals("1d10", longsword.damage().versatile());
        assertTrue(longsword.properties().stream().anyMatch(property -> property.uid().equals("V")));
        assertEquals(16, chainMail.armorClass());
        assertEquals("13", chainMail.strengthRequirement());
        assertEquals(Boolean.TRUE, chainMail.stealthDisadvantage());
        assertFalse(explorersPack.packContents().isEmpty());
    }

    @Test
    void magicItemChargesRechargeSpellGrantsAndSourceRelationsArePreserved() {
        ItemDto wand = service.byId("item:dmg:wand-of-magic-missiles");

        assertEquals(7, wand.charges().count());
        assertEquals("dawn", wand.charges().recharge());
        assertEquals("{@dice 1d6 + 1}", wand.charges().rechargeAmountText());
        assertEquals(List.of("magic missile"), wand.attachedSpells().charges().byUses().get("1"));
        assertEquals("XDMG", wand.reprintedAs().getFirst().uid().split("\\|")[1]);
        assertTrue(sourceRegistry.find(wand.source().identity().code()) != null);
    }

    @Test
    void attunementRestrictionsAbilitiesAndVehicleStatisticsRemainStructured() {
        ItemDto tool = service.byId("item:tce:1-all-purpose-tool");
        ItemDto amulet = service.byId("item:dmg:amulet-of-health");
        ItemDto airship = service.byId("item:dmg:airship");

        assertEquals("artificer|tce", tool.attunement().restrictions().getFirst().className());
        assertEquals(19, amulet.ability().staticScores().constitution());
        assertEquals(10, airship.vehicleStatistics().crew());
        assertEquals(13, airship.vehicleStatistics().armorClass());
        assertEquals(20, airship.vehicleStatistics().passengerCapacity());
    }

    @Test
    void copyResolutionKeepsIdentityAndMergesInheritedStructuredProperties() {
        ItemDto doom = service.byId("item:scc:alchemist-s-doom");
        ItemDto enchantedDeck = service.byId("item:llk:enchanted-three-dragon-ante-set");

        assertEquals("SCC", doom.source().identity().code());
        assertEquals("G", doom.typeCodes().getFirst());
        assertNotNull(doom.name());
        assertEquals("LLK", enchantedDeck.source().identity().code());
    }

    @Test
    void baseItemReferencesAndTypeCodesRemainSourceQualified() {
        ItemDto magicLongsword = catalog.summaries().stream()
                .map(summary -> service.byId(summary.id()))
                .filter(item -> item.baseItem() != null && item.baseItem().equalsIgnoreCase("longsword|phb"))
                .findFirst().orElseThrow();

        assertEquals("longsword|phb", magicLongsword.baseItem().toLowerCase());
        assertTrue(magicLongsword.typeCodes().size() >= 1);
    }

    @Test
    void filtersAreCaseInsensitiveAndDeterministicallySorted() {
        List<?> filtered = service.find("longsword", "phb", null, "NONE");
        assertFalse(filtered.isEmpty());
        assertTrue(service.find(null, null, "SCF", null).size() > 0);
        assertEquals(catalog.summaries(), service.find(null, null, null, null));
    }

    @Test
    void publicSerializationOmitsProseAndPreservesMeaningfulZeroAndFalse() throws Exception {
        JsonNode wand = objectMapper.valueToTree(service.byId("item:dmg:wand-of-magic-missiles"));
        JsonNode prostheticLimb = objectMapper.valueToTree(service.byId("item:frhof:prosthetic-limb"));

        assertFalse(wand.has("entries"));
        assertFalse(wand.has("copy"));
        assertEquals(0, prostheticLimb.get("valueCopper").asInt());
        JsonNode armor = objectMapper.valueToTree(service.byId("item:wdmm:vanrak-s-mithral-shirt"));
        assertEquals(false, armor.get("stealthDisadvantage").asBoolean());
    }

    @Test
    void rejectsUnknownTopLevelFieldsInStructuredSourceRecords() throws Exception {
        String malformed = "{\"name\":\"Example\",\"source\":\"PHB\",\"unknownMechanic\":true}";
        assertThrows(Exception.class, () -> objectMapper.readerFor(ItemSourceDte.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readValue(malformed));
    }

    @Test
    void rejectsUnknownAttachedSpellScheduleFields() throws Exception {
        String malformed = "{\"name\":\"Example\",\"source\":\"PHB\",\"attachedSpells\":{"
                + "\"mysterySchedule\":{\"1\":[\"magic missile\"]}}}";
        assertThrows(Exception.class, () -> objectMapper.readerFor(ItemSourceDte.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readValue(malformed));
    }

    @Test
    void packagedJarContainsBothItemResourcesAndTheyRemainReadable() throws Exception {
        Path jarPath = temporaryDirectory.resolve("items-resources.jar");
        try (JarOutputStream jar = new JarOutputStream(Files.newOutputStream(jarPath))) {
            for (String resourcePath : List.of("data/items.json", "data/items-base.json")) {
                jar.putNextEntry(new JarEntry(resourcePath));
                try (InputStream input = new ClassPathResource(resourcePath).getInputStream()) {
                    input.transferTo(jar);
                }
                jar.closeEntry();
            }
        }
        try (URLClassLoader classLoader = new URLClassLoader(new URL[]{jarPath.toUri().toURL()}, null)) {
            for (String resourcePath : List.of("data/items.json", "data/items-base.json")) {
                try (InputStream input = new ClassPathResource(resourcePath, classLoader).getInputStream()) {
                    JsonNode json = objectMapper.readTree(input);
                    assertTrue(json.size() > 1);
                }
            }
        }
    }

    @Test
    void endpointsReturnSummariesDetailsAndSourceSpecificNameMatches() throws Exception {
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new ItemsController(service))
                .setControllerAdvice(new MonsterApiExceptionHandler()).build();

        mvc.perform(get("/api/items").param("source", "xphb").param("type", "M"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].source.source").value("XPHB"));
        mvc.perform(get("/api/items/item:phb:longsword"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.definitionKind").value("BASE_ITEM"));
        mvc.perform(get("/api/items/by-name/Longsword").param("source", "XPHB"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].source.source").value("XPHB"));
        mvc.perform(get("/api/items/not-a-real-item"))
                .andExpect(status().isNotFound());
    }

    @Test
    void unsupportedCopyOperationsFailExplicitly() throws Exception {
        String parentJson = "{\"name\":\"Parent\",\"source\":\"PHB\",\"entries\":[\"one\"]}";
        String childJson = "{\"name\":\"Child\",\"source\":\"PHB\",\"_copy\":{"
                + "\"name\":\"Parent\",\"source\":\"PHB\",\"_mod\":{\"entries\":{"
                + "\"mode\":\"replaceText\",\"items\":\"unsupported\"}}}}";
        ItemSourceDte parent = objectMapper.readValue(parentJson, ItemSourceDte.class);
        ItemSourceDte child = objectMapper.readValue(childJson, ItemSourceDte.class);

        assertThrows(IllegalStateException.class, () -> copyResolver.resolve(List.of(parent, child)));
    }
}
