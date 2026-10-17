package com.thedndpub.data.encounter;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.controllers.EncountersController;
import com.thedndpub.data.dto.encounter.EncounterDefinition;
import com.thedndpub.data.dto.encounter.EncounterMapper;
import com.thedndpub.data.dto.encounter.EncounterTableSummaryDto;
import com.thedndpub.data.dto.references.PublicCrossCatalogReferenceDto;
import com.thedndpub.data.dte.encounter.EncounterDte;
import com.thedndpub.data.dte.encounter.EncounterFileDte;
import com.thedndpub.data.dte.encounter.EncounterRowDte;
import com.thedndpub.data.dte.encounter.EncounterTableDte;
import com.thedndpub.data.services.EncounterCatalog;
import com.thedndpub.data.services.EncountersService;
import com.thedndpub.data.services.SourceRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class EncounterCatalogTests {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private EncounterCatalog catalog;
    @Autowired
    private EncounterMapper mapper;
    @Autowired
    private SourceRegistry sourceRegistry;
    @Autowired
    private EncountersService service;

    @Test
    void loadsCompleteTypedCorpusAndValidatesSourcesAndStableIds() throws Exception {
        EncounterFileDte source;
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("data/encounters.json")) {
            assertNotNull(input);
            source = objectMapper.readerFor(EncounterFileDte.class)
                    .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).readValue(input);
        }
        assertEquals(42, source.encounter().size());
        assertEquals(42, catalog.size());
        assertEquals(93, catalog.definitions().stream().mapToInt(value -> value.tables().size()).sum());
        assertEquals(2299, catalog.definitions().stream().mapToInt(EncounterDefinition::rowCount).sum());
        assertEquals(42, new HashSet<>(catalog.definitions().stream()
                .map(value -> value.id().value()).toList()).size());
        assertTrue(catalog.definitions().stream().allMatch(value ->
                sourceRegistry.find(value.source().identity().code()) != null));
        assertEquals("encounter:BGG:cloud-giant", catalog.findByName("Cloud Giant").getFirst().id().value());
    }

    @Test
    void rejectsUnknownFieldsAndRetainsAuthoredRowsAndMechanicalMetadata() throws Exception {
        assertThrows(Exception.class, () -> objectMapper.readerFor(EncounterFileDte.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .readValue("{\"encounter\":[{\"name\":\"x\",\"source\":\"XGE\",\"tables\":[],\"unknown\":1}]}"));
        assertThrows(Exception.class, () -> objectMapper.readerFor(EncounterFileDte.class)
                .with(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .readValue("{\"encounter\":[{\"name\":\"x\",\"source\":\"XGE\","
                        + "\"tables\":[{\"diceExpression\":\"d6\",\"table\":[],\"unknown\":1}]}]}"));
        EncounterDefinition arctic = catalog.findByName("Arctic").getFirst();
        assertEquals("d100", arctic.tables().getFirst().diceExpression());
        assertTrue(arctic.tables().getFirst().rows().get(1).result().contains("{@creature"));
        assertEquals(1, arctic.tables().getFirst().minLevel());
        assertEquals(4, arctic.tables().getFirst().maxLevel());
        assertThrows(UnsupportedOperationException.class, () -> arctic.tables().clear());
        assertThrows(UnsupportedOperationException.class, () -> arctic.tables().getFirst().rows().clear());
    }

    @Test
    void sourceAwareNameLookupSeparatesSameNameRecordsAndOrderingIsStable() {
        List<EncounterDefinition> urban = catalog.findByName(" Urban ");
        assertEquals(2, urban.size());
        assertEquals(List.of("DMG", "XGE"), urban.stream()
                .map(value -> value.source().identity().code()).toList());
        assertEquals(urban.stream().map(value -> value.id().value()).sorted().toList(),
                catalog.definitions().stream().filter(value -> value.name().equals("Urban"))
                        .map(value -> value.id().value()).sorted().toList());
        assertEquals(1, service.findByName("Urban", "DMG").size());
    }

    @Test
    void preservesAuthoredZeroBoundsAndFalseFlags() {
        EncounterDefinition definition = mapper.map(new EncounterDte(
                "Synthetic zero outcome",
                "XGE",
                null,
                List.of(new EncounterTableDte(
                        null,
                        null,
                        "d100",
                        List.of(new EncounterRowDte(0, 0, "retained source result", null)),
                        null,
                        null,
                        null,
                        List.of(),
                        null,
                        false))));

        assertEquals(0, definition.tables().getFirst().rows().getFirst().min());
        assertEquals(0, definition.tables().getFirst().rows().getFirst().max());
        assertEquals(Boolean.FALSE, definition.tables().getFirst().rollAttitude());
        EncounterTableSummaryDto tableSummary = new EncounterTableSummaryDto(
                null, null, "d100", 1, List.of(), null, false);
        assertTrue(objectMapper.valueToTree(tableSummary).toString().contains("\"rollAttitude\":false"));
    }

    @Test
    void publicProjectionWithholdsRowsMarkupAndDescriptiveText() throws Exception {
        String json = objectMapper.writeValueAsString(service.findById("encounter:XGE:arctic"));
        assertFalse(json.contains("result"));
        assertFalse(json.contains("creature"));
        assertFalse(json.contains("crossCatalogReferences"));
        assertFalse(json.contains("kobold"));
        assertTrue(json.contains("diceExpression"));
        assertTrue(json.contains("rowCount"));
    }

    @Test
    void exposesOnlyExplicitSupportedMarkupReferencesWithoutOutcomeText() throws Exception {
        String json = objectMapper.writeValueAsString(
                service.findById("encounter:FRAiF:forgotten-realms-encounters"));

        assertTrue(json.contains("crossCatalogReferences"));
        assertTrue(json.contains("tables[0].table[15].result.markup[1]"));
        assertTrue(json.contains("Mechanical Wonder|FRAiF|Mechanical Wonders"));
        assertFalse(json.contains("specializing in"));
        assertFalse(json.contains("creature"));
    }

    @Test
    void preservesOnlyExplicitSupportedResolverOccurrences() {
        List<PublicCrossCatalogReferenceDto> references = catalog.definitions()
                .stream()
                .flatMap(definition -> service.findById(definition.id().value())
                        .crossCatalogReferences().stream())
                .toList();

        assertEquals(15, references.size());
        assertEquals(3, references.stream().filter(reference -> reference.targetId() != null).count());
        assertTrue(references.stream().allMatch(reference ->
                reference.fieldLocator().contains(".result.markup[")));
        assertTrue(references.stream().noneMatch(reference ->
                reference.targetKind().name().equals("MONSTER")));
    }

    @Test
    void exposesReadOnlyEndpointsAndSourceFilter() throws Exception {
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new EncountersController(service)).build();
        mvc.perform(get("/api/encounters").param("source", "DMG"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].source").value("DMG"));
        mvc.perform(get("/api/encounters/by-name/Urban").param("source", "XGE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id.value").value("encounter:XGE:urban"));
        mvc.perform(get("/api/encounters/encounter:XGE:arctic"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tables[0].diceExpression").value("d100"))
                .andExpect(jsonPath("$.tables[0].rows[0]").doesNotExist());
        mvc.perform(get("/api/encounters/missing"))
                .andExpect(status().isNotFound());
    }
}

