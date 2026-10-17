package com.thedndpub.data.reward;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.controllers.RewardsController;
import com.thedndpub.data.controllers.RewardsApiExceptionHandler;
import com.thedndpub.data.dto.reward.RewardDefinition;
import com.thedndpub.data.dto.reward.RewardSpellGrantDto;
import com.thedndpub.data.dte.common.entry.NamedEntryDte;
import com.thedndpub.data.dte.common.entry.ListEntryDte;
import com.thedndpub.data.dte.common.entry.ListItemEntryDte;
import com.thedndpub.data.dte.common.entry.RewardInlineStatblockEntryDte;
import com.thedndpub.data.dte.common.entry.TextEntryDte;
import com.thedndpub.data.dte.reward.RewardReprintTokenDte;
import com.thedndpub.data.dte.reward.RewardReprintUidDte;
import com.thedndpub.data.dte.reward.RewardSpellAbilityChoiceDte;
import com.thedndpub.data.dte.reward.RewardsFileDte;
import com.thedndpub.data.references.CrossCatalogResolutionCoordinator;
import com.thedndpub.data.references.PublicReferenceProjection;
import com.thedndpub.data.services.RewardCatalog;
import com.thedndpub.data.services.RewardsService;
import com.thedndpub.data.services.SourceRegistry;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class RewardsCatalogTests {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RewardCatalog catalog;
    @Autowired
    private RewardsService service;
    @Autowired
    private SourceRegistry sourceRegistry;
    @Autowired
    private CrossCatalogResolutionCoordinator resolver;

    @Test
    void loadsCompleteCorpusWithRegisteredSourcesAndUniqueStableIds() throws Exception {
        RewardsFileDte source;
        try (InputStream input = new ClassPathResource("data/rewards.json").getInputStream()) {
            source = objectMapper.readerFor(RewardsFileDte.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readValue(input);
        }
        assertEquals(278, source.reward().size());
        assertEquals(278, catalog.size());
        assertEquals(275, catalog.definitions().stream().filter(value -> value.source().page() != null).count());
        assertEquals(89, catalog.definitions().stream().filter(value -> !value.additionalSpells().isEmpty()).count());
        assertEquals(29, catalog.definitions().stream().filter(value -> !value.seeAlsoFacility().isEmpty()).count());
        assertEquals(25, catalog.definitions().stream().filter(value -> !value.reprintedAs().isEmpty()).count());
        assertEquals(8, catalog.definitions().stream().filter(value -> value.rarity() != null).count());
        assertEquals(1, catalog.definitions().stream().filter(value -> !value.otherSources().isEmpty()).count());
        assertEquals(278, new HashSet<>(catalog.definitions().stream()
                .map(value -> value.id().value()).toList()).size());
        assertTrue(catalog.definitions().stream()
                .allMatch(value -> sourceRegistry.find(value.source().identity().code()) != null));
        assertEquals(Set.of("Blessing", "Boon", "Charm", "Curse", "Draconic Gift", "Fragment of Suffering",
                "Inhabitation", "Other", "Piety Trait"), catalog.definitions().stream()
                .map(RewardDefinition::type).collect(java.util.stream.Collectors.toSet()));
        assertEquals(26, catalog.definitions().stream()
                .map(value -> value.source().identity().code()).distinct().count());
        assertEquals(40, catalog.definitions().stream()
                .filter(value -> value.source().identity().code().equals("DMG")).count());
        assertEquals(60, catalog.definitions().stream()
                .filter(value -> value.source().identity().code().equals("MOT")).count());
        assertEquals(19, catalog.definitions().stream()
                .filter(value -> value.source().identity().code().equals("XDMG")).count());
        assertEquals(25, catalog.definitions().stream()
                .mapToInt(value -> value.reprintedAs().size()).sum());
        assertEquals(1, catalog.definitions().stream()
                .mapToInt(value -> value.otherSources().size()).sum());
        assertEquals(List.of("DMG", "XDMG"), catalog.findByName("Blessing of Health").stream()
                .map(value -> value.source().identity().code()).toList());
        assertEquals("reward:DMG:blessing-of-health", catalog.findByName("Blessing of Health").stream()
                .filter(value -> value.source().identity().code().equals("DMG"))
                .findFirst().orElseThrow().id().value());
    }

    @Test
    void strictlyRejectsUnknownRootNestedAndPolymorphicFields() {
        assertThrows(Exception.class, () -> objectMapper.readerFor(RewardsFileDte.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .readValue("{\"reward\":[{\"name\":\"x\",\"source\":\"DMG\",\"type\":\"Charm\","
                        + "\"entries\":[],\"unknown\":true}]}"));
        assertThrows(Exception.class, () -> objectMapper.readerFor(RewardsFileDte.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .readValue("{\"reward\":[{\"name\":\"x\",\"source\":\"DMG\",\"type\":\"Charm\","
                        + "\"entries\":[{\"type\":\"mystery\"}]}]}"));
        assertThrows(Exception.class, () -> objectMapper.readerFor(RewardsFileDte.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .readValue("{\"reward\":[{\"name\":\"x\",\"source\":\"DMG\",\"type\":\"Charm\","
                        + "\"additionalSpells\":[{\"innate\":{\"_\":{\"daily\":{},\"surprise\":{}}}}]}]}"));
        assertThrows(Exception.class, () -> objectMapper.readerFor(RewardsFileDte.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .readValue("{\"reward\":[{\"name\":\"x\",\"source\":\"DMG\",\"type\":\"Charm\","
                        + "\"entries\":[{\"type\":\"statblockInline\",\"statblockType\":\"object\","
                        + "\"statblockData\":{\"name\":\"x\",\"unmapped\":1}}]}]}"));
    }

    @Test
    void preservesObservedEntryVariantsAndStructuredSpellBuckets() {
        List<RewardDefinition> definitions = catalog.definitions();
        List<com.thedndpub.data.dte.common.entry.EntryDte> entries = definitions.stream()
                .flatMap(value -> flattenEntries(value.authoredEntries()).stream()).toList();
        assertTrue(entries.stream().anyMatch(NamedEntryDte.class::isInstance));
        assertTrue(entries.stream().anyMatch(ListEntryDte.class::isInstance));
        assertTrue(entries.stream().anyMatch(ListItemEntryDte.class::isInstance));
        assertTrue(entries.stream().anyMatch(TextEntryDte.class::isInstance));
        assertEquals(3, entries.stream().filter(RewardInlineStatblockEntryDte.class::isInstance).count());
        assertEquals(89, definitions.stream().mapToInt(value -> value.additionalSpells().size()).sum());
        List<RewardSpellGrantDto> grants = definitions.stream()
                .flatMap(value -> service.findById(value.id().value()).spellGrants().stream()).toList();
        assertTrue(grants.stream().anyMatch(value -> !value.knownSpellIds().isEmpty()));
        assertTrue(grants.stream().anyMatch(value -> !value.innateSpellIds().isEmpty()));
        assertTrue(grants.stream().anyMatch(value -> !value.daily().isEmpty()));
        assertTrue(grants.stream().anyMatch(value -> !value.limited().isEmpty()));
        assertTrue(grants.stream().anyMatch(value -> !value.ritualSpellIds().isEmpty()));
        assertTrue(grants.stream().anyMatch(value -> value.ability() != null));
        assertTrue(grants.stream().anyMatch(value -> !value.abilityChoices().isEmpty()));
        assertTrue(grants.stream().anyMatch(value -> value.resourceName() != null));
        assertTrue(definitions.stream().flatMap(value -> value.reprintedAs().stream())
                .anyMatch(RewardReprintTokenDte.class::isInstance));
        assertTrue(definitions.stream().flatMap(value -> value.reprintedAs().stream())
                .anyMatch(RewardReprintUidDte.class::isInstance));
        assertThrows(UnsupportedOperationException.class, () -> definitions.getFirst().authoredEntries().clear());
    }

    private static List<com.thedndpub.data.dte.common.entry.EntryDte> flattenEntries(
            List<com.thedndpub.data.dte.common.entry.EntryDte> entries) {
        java.util.ArrayList<com.thedndpub.data.dte.common.entry.EntryDte> flattened = new java.util.ArrayList<>();
        if (entries == null) {
            return List.of();
        }
        for (var entry : entries) {
            flattened.add(entry);
            if (entry instanceof NamedEntryDte named) {
                flattened.addAll(flattenEntries(named.entries()));
            } else if (entry instanceof ListEntryDte list) {
                flattened.addAll(flattenEntries(list.items()));
            } else if (entry instanceof ListItemEntryDte item) {
                flattened.addAll(flattenEntries(item.entries()));
            }
        }
        return List.copyOf(flattened);
    }

    @Test
    void exposesSafeMetadataAndOnlyExplicitResolverReferences() throws Exception {
        String json = objectMapper.writeValueAsString(service.findById("reward:DMG:blessing-of-health"));
        assertTrue(json.contains("Blessing of Health"));
        assertFalse(json.contains("entries"));
        assertFalse(json.contains("{@"));
        assertFalse(json.contains("You gain"));
        var rewardReferences = resolver.snapshot().results().stream()
                .filter(value -> value.reference().owner().catalog().equals("reward")).toList();
        assertEquals(291, rewardReferences.size());
        assertEquals(61, rewardReferences.stream().filter(value -> value.resolvedTargetId() != null).count());
        assertEquals(113, rewardReferences.stream()
                .filter(value -> value.reference().fieldLocator().startsWith("additionalSpells["))
                .count());
        assertTrue(resolver.snapshot().results().stream()
                .filter(value -> value.reference().owner().catalog().equals("reward"))
                .noneMatch(value -> value.reference().targetKind().name().equals("MONSTER")));
    }

    @Test
    void preservesFalseAndZeroInlineStatblockValuesAndSpellAbilityChoices() throws Exception {
        RewardsFileDte synthetic = objectMapper.readerFor(RewardsFileDte.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .readValue("{\"reward\":[{\"name\":\"Synthetic\",\"source\":\"DMG\","
                        + "\"type\":\"Charm\",\"entries\":[{\"type\":\"statblockInline\","
                        + "\"statblockType\":\"object\",\"statblockData\":{\"name\":\"x\","
                        + "\"isNpc\":false,\"hp\":0}}],\"additionalSpells\":[{"
                        + "\"ability\":{\"choose\":[\"int\",\"wis\"]}}]}]}");
        var inline = (RewardInlineStatblockEntryDte) synthetic.reward().getFirst().entries().getFirst();
        assertEquals(Boolean.FALSE, inline.statblockData().isNpc());
        assertEquals(0, inline.statblockData().hp());
        assertTrue(synthetic.reward().getFirst().additionalSpells().getFirst().ability()
                instanceof RewardSpellAbilityChoiceDte);
        assertNotNull(synthetic);
    }

    @Test
    void filtersAndLooksUpDefinitionsByNameAndSource() throws Exception {
        assertEquals(278, service.findAll(null, null).size());
        assertEquals(40, service.findAll("DMG", null).size());
        assertEquals(138, service.findAll(null, "Charm").size());
        assertTrue(service.findByName("Blessing of Health", "XDMG").size() == 1);
        assertEquals(2, service.findByName("Blessing of Health", null).size());
        assertEquals(catalog.summaries(), service.findAll(null, null));
        assertThrows(UnsupportedOperationException.class, () -> catalog.summaries().clear());
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new RewardsController(service))
                .setControllerAdvice(new RewardsApiExceptionHandler())
                .build();
        mvc.perform(get("/api/rewards").param("source", "DMG").param("type", "Blessing"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].source").value("DMG"));
        mvc.perform(get("/api/rewards/by-name/{name}", "Blessing of Health").param("source", "DMG"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id.value").value("reward:DMG:blessing-of-health"));
        mvc.perform(get("/api/rewards/reward:DMG:blessing-of-health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.type").value("Blessing"));
        mvc.perform(get("/api/rewards/reward:missing:missing"))
                .andExpect(status().isNotFound());
    }
}
