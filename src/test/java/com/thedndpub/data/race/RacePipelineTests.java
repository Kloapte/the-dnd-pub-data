package com.thedndpub.data.race;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.controllers.MonsterApiExceptionHandler;
import com.thedndpub.data.controllers.RacesController;
import com.thedndpub.data.dto.race.RaceDto;
import com.thedndpub.data.dto.race.RaceLineageDto;
import com.thedndpub.data.dto.race.RaceMovementDto;
import com.thedndpub.data.dto.race.RaceMovementValueDto;
import com.thedndpub.data.services.RaceNotFoundException;
import com.thedndpub.data.services.RaceCatalog;
import com.thedndpub.data.services.RacesService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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
class RacePipelineTests {
    @Autowired
    private RaceCatalog catalog;

    @Autowired
    private RacesService service;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUpMockMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(new RacesController(service))
                .setControllerAdvice(new MonsterApiExceptionHandler())
                .build();
    }

    @Test
    void catalogLoadsEveryRootAndExpandedDefinitionFromClasspath() {
        assertEquals(356, catalog.size());
        assertEquals(258, catalog.rootCount());
        assertEquals(103, catalog.expandedCount());
        assertEquals(222, catalog.findByKind(com.thedndpub.data.dto.race.RaceDefinitionKind.RACE).size());
        assertEquals(134, catalog.findByKind(com.thedndpub.data.dto.race.RaceDefinitionKind.SUBRACE).size());
        assertEquals(356, catalog.definitions().size());
        assertEquals(356, catalog.summaries().size());
        assertThrows(UnsupportedOperationException.class, () -> catalog.definitions().clear());
        assertThrows(UnsupportedOperationException.class, () -> catalog.summaries().clear());

        catalog.definitions().stream()
                .filter(definition -> definition.provenance().expandedVersion())
                .forEach(definition -> assertNotNull(service.byId(definition.id().value())));
    }

    @Test
    void preservesParentSpecificAndEditionSpecificIdentities() {
        List<RaceDto> ixalan = service.byName("Ixalan", "PSX");
        assertEquals(2, ixalan.size());
        assertTrue(ixalan.get(0).id().compareTo(ixalan.get(1).id()) != 0);
        assertTrue(ixalan.stream().allMatch(value -> value.parent() != null));
        assertTrue(ixalan.get(0).parent().id().compareTo(ixalan.get(1).parent().id()) != 0);

        List<RaceDto> dwarves = service.byName("dWaRf", null);
        assertTrue(dwarves.stream().anyMatch(value -> value.source().identity().code().equals("PHB")));
        assertTrue(dwarves.stream().anyMatch(value -> value.source().identity().code().equals("XPHB")));
        assertTrue(dwarves.stream().allMatch(value -> value.definitionKind().name().equals("RACE")));
    }

    @Test
    void mapsCorpusChoicesSpellSchedulesAndSourceRelationships() throws Exception {
        List<RaceDto> details = catalog.definitions().stream()
                .map(definition -> service.byId(definition.id().value()))
                .toList();
        assertTrue(details.stream().flatMap(value -> value.abilities().stream())
                .anyMatch(value -> value.choice() != null && !value.choice().from().isEmpty()));
        assertTrue(details.stream().flatMap(value -> value.skillProficiencies().stream())
                .flatMap(value -> value.grants().stream())
                .anyMatch(value -> value.choice() != null && value.choice().count() != null));
        assertTrue(details.stream().anyMatch(value -> !value.skillToolLanguageProficiencies().isEmpty()));
        assertTrue(details.stream().flatMap(value -> value.additionalSpells().stream())
                .anyMatch(value -> value.innate() != null || value.known() != null
                        || value.prepared() != null || value.expanded() != null));
        assertTrue(details.stream().anyMatch(value -> !value.otherSources().isEmpty()));
        assertTrue(details.stream().anyMatch(value -> !value.reprintedAs().isEmpty()));
        assertTrue(details.stream().anyMatch(value -> value.lineage() != null));
        assertTrue(details.stream().flatMap(value -> value.damageResistances().stream())
                .anyMatch(value -> value.choose() != null));

        String json = objectMapper.writeValueAsString(details);
        assertFalse(json.contains("{@"));
        assertFalse(json.contains(":{}"));
        assertFalse(json.contains("\"entries\""));
        assertFalse(json.contains("\"versions\""));
        assertFalse(json.contains("\"copy\""));
        assertFalse(json.contains("\"soundClip\""));
    }

    @Test
    void mapsStructuredMechanicsWithoutExposingEntriesOrImportInstructions() throws Exception {
        RaceDto aarakocra = service.byName("Aarakocra", "MPMM").getFirst();
        assertEquals(Integer.valueOf(30), aarakocra.movement().walk().feet());
        assertEquals(Boolean.TRUE, aarakocra.movement().fly().sourceFlag());
        assertEquals(Integer.valueOf(30), catalog.summaries().stream()
                .filter(summary -> summary.id().equals(aarakocra.id()))
                .findFirst()
                .orElseThrow()
                .walkingSpeed());

        RaceDto subrace = service.byName("Dragonborn (Black)", "PHB").getFirst();
        assertEquals("race:phb:dragonborn", subrace.parent().id());
        assertFalse(subrace.damageResistances().isEmpty());

        String json = objectMapper.writeValueAsString(subrace);
        assertFalse(json.contains("entries"));
        assertFalse(json.contains("{@"));
        assertFalse(json.contains("provenance"));
        assertFalse(json.contains("implementation"));
        assertTrue(json.contains("parent"));
        assertTrue(json.contains("damageResistances"));
    }

    @Test
    void keepsMeaningfulZeroAndFalseInPublicJson() throws Exception {
        RaceDto sample = new RaceDto(
                "race:test:sample",
                "Sample",
                new com.thedndpub.data.dto.source.SourceReference("PHB", null),
                com.thedndpub.data.dto.race.RaceDefinitionKind.RACE,
                null,
                null,
                List.of(),
                List.of(),
                List.of(),
                new RaceMovementDto(null, new RaceMovementValueDto(0, false), null, null, null),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                null,
                null,
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                new RaceLineageDto(false, null),
                null,
                null,
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of());

        JsonNode json = objectMapper.readTree(objectMapper.writeValueAsBytes(sample));
        assertEquals(0, json.path("movement").path("fly").path("feet").intValue());
        assertFalse(json.path("movement").path("fly").path("sourceFlag").booleanValue());
        assertFalse(json.path("lineage").path("flag").booleanValue());
        assertFalse(json.has("aliases"));
    }

    @Test
    void supportsDeterministicCaseInsensitiveCombinedFiltersAndNameAmbiguity() {
        var filtered = service.find("dWaRf", "xphb", "race");
        assertEquals(1, filtered.size());
        assertEquals("Dwarf", filtered.getFirst().name());
        assertEquals("XPHB", filtered.getFirst().source().identity().code());

        var all = service.find(null, null, null);
        assertEquals(catalog.summaries(), all);
        assertThrows(RaceNotFoundException.class, () -> service.byId("race:unknown:missing"));
        assertThrows(IllegalArgumentException.class, () -> service.find(null, null, "species"));
    }

    @Test
    void exposesRaceRoutesAndConsistentErrors() throws Exception {
        mockMvc.perform(get("/api/races").param("name", "dwarf").param("source", "PHB")
                        .param("definitionKind", "race"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("race:phb:dwarf"));

        mockMvc.perform(get("/api/races/race:phb:dwarf"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.definitionKind").value("RACE"));

        mockMvc.perform(get("/api/races/by-name/Ixalan").param("source", "PSX"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        mockMvc.perform(get("/api/races/race:unknown:missing"))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/races/by-name/NoSuchRace"))
                .andExpect(status().isNotFound());

        mockMvc.perform(get("/api/races").param("definitionKind", "species"))
                .andExpect(status().isBadRequest());
    }
}
