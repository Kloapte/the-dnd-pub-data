package com.thedndpub.data.spell;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.thedndpub.data.controllers.SpellsController;
import com.thedndpub.data.dto.spell.SpellDefinition;
import com.thedndpub.data.dto.spell.SpellFormulaAnnotationIndex;
import com.thedndpub.data.dto.spell.SpellId;
import com.thedndpub.data.dto.spell.api.SpellDto;
import com.thedndpub.data.dto.spell.api.SpellFormulaProjectionStatus;
import com.thedndpub.data.dto.spell.api.SpellPublicMapper;
import com.thedndpub.data.services.SpellCatalog;
import com.thedndpub.data.services.SpellFormulaAnnotationCatalog;
import com.thedndpub.data.services.SpellFormulaProjectionCatalog;
import com.thedndpub.data.services.SpellsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class SpellFormulaProjectionStatusTests {
    private static final Path ASSESSMENT = Path.of("analysis/mechanics/public-v1/spells");

    @Autowired
    private SpellCatalog spells;

    @Autowired
    private SpellFormulaAnnotationCatalog annotations;

    @Autowired
    private SpellFormulaProjectionCatalog projection;

    @Autowired
    private SpellsService service;

    @Autowired
    private SpellPublicMapper mapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void reconcilesEveryOwnerAndEmitsOnlyTheApprovedCorpusStatuses() throws Exception {
        var index = annotations.index();
        assertEquals(969, spells.size());
        assertEquals(969, index.spellRootCount());
        assertEquals(930, index.annotations().size());
        assertEquals(422, index.formulaOwnerCount());
        assertEquals(new HashSet<>(spells.findAll().stream().map(SpellDefinition::id).toList()),
                index.bySpellId().keySet());

        Map<SpellFormulaProjectionStatus, Integer> counts = new EnumMap<>(SpellFormulaProjectionStatus.class);
        for (var spell : spells.findAll()) {
            var expected = index.bySpellId().get(spell.id()).isEmpty()
                    ? SpellFormulaProjectionStatus.NONE_OBSERVED : SpellFormulaProjectionStatus.WITHHELD;
            assertEquals(expected, projection.statusFor(spell.id()));
            assertEquals(expected, service.findById(spell.id().value()).formulaProjectionStatus());
            counts.merge(expected, 1, Integer::sum);
        }
        assertEquals(Map.of(SpellFormulaProjectionStatus.NONE_OBSERVED, 547,
                SpellFormulaProjectionStatus.WITHHELD, 422), counts);

        for (JsonNode record : assessment("runtime-evidence.json").path("records")) {
            String id = record.path("id").asText();
            assertNotNull(spells.findById(id));
            assertEquals(record.path("formulaCount").asInt(),
                    index.bySpellId().get(new SpellId(id)).size(), id);
        }
    }

    @Test
    void serializesAllFourEnumValuesWithoutPublishingAnyPayload() throws Exception {
        assertEquals(List.of("NONE_OBSERVED", "WITHHELD", "PARTIAL", "COMPLETE"),
                List.of(SpellFormulaProjectionStatus.values()).stream().map(Enum::name).toList());
        for (var status : SpellFormulaProjectionStatus.values()) {
            SpellDto dto = new SpellDto("spell:test:example", "Example", null, List.of(), 0, null,
                    List.of(), false, null, null, List.of(), null, List.of(), null, status);
            JsonNode json = objectMapper.readTree(objectMapper.writeValueAsBytes(dto));
            assertEquals(status.name(), json.path("formulaProjectionStatus").asText());
            assertEquals(0, json.path("level").asInt());
            assertFalse(json.path("ritual").asBoolean());
            assertFalse(json.has("formulaObservations"));
            assertEquals(status, objectMapper.treeToValue(
                    json.path("formulaProjectionStatus"), SpellFormulaProjectionStatus.class));
        }
    }

    @Test
    void fireballIsWithheldAndShieldAbsenceDoesNotClaimAbsenceOfMechanics() throws Exception {
        for (String source : List.of("phb", "xphb")) {
            assertEquals(SpellFormulaProjectionStatus.WITHHELD,
                    service.findById("spell:" + source + ":fireball").formulaProjectionStatus());
            SpellDto shield = service.findById("spell:" + source + ":shield");
            assertEquals(SpellFormulaProjectionStatus.NONE_OBSERVED, shield.formulaProjectionStatus());
            // Its existing structured metadata and internal entries still exist, despite no indexed formula.
            assertNotNull(shield.range());
            assertFalse(shield.durations().isEmpty());
            assertFalse(spells.findById(shield.id()).entries().isEmpty());
        }
        for (JsonNode label : assessment("gold-set.json").path("labels")) {
            String id = label.path("spellId").asText();
            var expected = annotations.index().bySpellId().get(new SpellId(id)).isEmpty()
                    ? SpellFormulaProjectionStatus.NONE_OBSERVED : SpellFormulaProjectionStatus.WITHHELD;
            assertEquals(expected, service.findById(id).formulaProjectionStatus(), label.path("id").asText());
        }
    }

    @Test
    void detailAndByNameAgreeAndSummaryRoutesRemainUnchanged() throws Exception {
        var mvc = MockMvcBuilders.standaloneSetup(new SpellsController(service)).build();
        mvc.perform(get("/api/spells/spell:phb:fireball"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.formulaProjectionStatus").value("WITHHELD"));
        mvc.perform(get("/api/spells/by-name/Fireball").param("source", "PHB"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].formulaProjectionStatus").value("WITHHELD"));
        mvc.perform(get("/api/spells/by-name/Shield").param("source", "PHB"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].formulaProjectionStatus").value("NONE_OBSERVED"));
        mvc.perform(get("/api/spells").param("name", "Fireball").param("source", "PHB"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].formulaProjectionStatus").doesNotExist());

        var legacyMapper = new SpellPublicMapper();
        for (var spell : spells.findAll()) {
            assertEquals(service.findById(spell.id().value()),
                    service.findByName(spell.identity().name(), spell.source().identity().code()).stream()
                            .filter(dto -> dto.id().equals(spell.id().value())).findFirst().orElseThrow());
            assertEquals(objectMapper.writeValueAsString(legacyMapper.summary(spell)),
                    objectMapper.writeValueAsString(mapper.summary(spell)));
        }
    }

    @Test
    void everyExistingDetailFieldAndItsSerializationOrderMatchesTheApprovedAssessment() throws Exception {
        var records = assessment("runtime-evidence.json").path("records");
        assertEquals(969, records.size());
        for (JsonNode record : records) {
            String id = record.path("id").asText();
            ObjectNode json = objectMapper.valueToTree(service.findById(id));
            List<String> names = new ArrayList<>();
            json.fieldNames().forEachRemaining(names::add);
            assertEquals("formulaProjectionStatus", names.getLast(), id);
            assertNotNull(json.remove("formulaProjectionStatus"));
            String actual = HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256")
                    .digest(objectMapper.writeValueAsBytes(json)));
            assertEquals(record.path("publicSnapshotSha256").asText(), actual, id);
        }
    }

    @Test
    void legacyConstructorAndStandaloneMapperLeaveStatusNullAndOmitted() throws Exception {
        SpellDto current = service.findById("spell:phb:fireball");
        SpellDto legacy = new SpellDto(current.id(), current.name(), current.source(), current.aliases(),
                current.level(), current.school(), current.castingTimes(), current.ritual(), current.range(),
                current.components(), current.durations(), current.mechanics(), current.scaling(),
                current.sourceRelationships());
        assertNull(legacy.formulaProjectionStatus());
        ObjectNode currentJson = objectMapper.valueToTree(current);
        currentJson.remove("formulaProjectionStatus");
        assertEquals(currentJson, objectMapper.valueToTree(legacy));
        assertEquals(legacy, new SpellPublicMapper().detail(spells.findById(current.id())));
        assertFalse(objectMapper.readTree(objectMapper.writeValueAsBytes(legacy)).has("formulaProjectionStatus"));
    }

    @Test
    void projectionIsDeterministicAndDtoCollectionsRemainImmutable() throws Exception {
        var definitions = new ArrayList<>(spells.findAll());
        var snapshot = new SpellFormulaProjectionCatalog(definitions, annotations.index());
        definitions.clear();
        for (var spell : spells.findAll()) {
            assertEquals(projection.statusFor(spell.id()), snapshot.statusFor(spell.id()));
            assertEquals(objectMapper.writeValueAsString(mapper.detail(spell)),
                    objectMapper.writeValueAsString(mapper.detail(spell)));
        }
        var dto = service.findById("spell:phb:fireball");
        assertThrows(UnsupportedOperationException.class, () -> dto.aliases().add("Other"));
        assertThrows(UnsupportedOperationException.class, () -> dto.castingTimes().clear());
        assertThrows(UnsupportedOperationException.class, () -> dto.durations().clear());
        assertThrows(UnsupportedOperationException.class, () -> dto.scaling().clear());
    }

    @Test
    void startupReconciliationRejectsMissingExtraAndDuplicateOwners() {
        var first = spells.findAll().getFirst();
        var second = spells.findAll().get(1);
        var incomplete = emptyIndex(List.of(first));
        var missing = assertThrows(IllegalArgumentException.class,
                () -> new SpellFormulaProjectionCatalog(List.of(first, second), incomplete));
        assertTrue(missing.getMessage().contains(second.id().value()));
        assertThrows(IllegalArgumentException.class,
                () -> new SpellFormulaProjectionCatalog(List.of(first), emptyIndex(List.of(first, second))));
        assertThrows(IllegalArgumentException.class,
                () -> new SpellFormulaProjectionCatalog(List.of(first, first), incomplete));
        assertThrows(NullPointerException.class,
                () -> new SpellFormulaProjectionCatalog(List.of(first), null));
    }

    @Test
    void sameOwnerCountAndSameNameCannotHideAnEditionIdentityMismatch() {
        var phb = spells.findById("spell:phb:shield");
        var xphb = spells.findById("spell:xphb:shield");
        assertEquals(phb.identity().name(), xphb.identity().name());
        assertThrows(IllegalArgumentException.class,
                () -> new SpellFormulaProjectionCatalog(List.of(phb), emptyIndex(List.of(xphb))));
    }

    @Test
    void unknownOwnerLookupFailsInsteadOfClaimingNoObservedFormulas() {
        var unknown = new SpellId("spell:phb:not-an-indexed-owner");
        assertThrows(IllegalArgumentException.class, () -> projection.statusFor(unknown));
        assertThrows(NullPointerException.class, () -> projection.statusFor(null));
    }

    private SpellFormulaAnnotationIndex emptyIndex(List<SpellDefinition> definitions) {
        return new SpellFormulaAnnotationIndex(definitions, 0, List.of(), List.of());
    }

    private JsonNode assessment(String file) throws Exception {
        return objectMapper.readTree(ASSESSMENT.resolve(file).toFile());
    }
}
