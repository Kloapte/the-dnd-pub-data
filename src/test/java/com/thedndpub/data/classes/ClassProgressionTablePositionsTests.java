package com.thedndpub.data.classes;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.thedndpub.data.dto.classes.ClassNormalizationResult;
import com.thedndpub.data.dto.classes.ClassPublicMapper;
import com.thedndpub.data.dto.classes.ClassProgressionTable;
import com.thedndpub.data.dto.classes.ClassTableCellKind;
import com.thedndpub.data.dto.classes.ClassDefinition;
import com.thedndpub.data.dto.classes.ClassHitDice;
import com.thedndpub.data.dto.classes.ClassLevelCount;
import com.thedndpub.data.dto.classes.ClassPositionedTableCellDto;
import com.thedndpub.data.dto.classes.ClassProgressionTableDto;
import com.thedndpub.data.dto.classes.ClassProgressionTableRowDto;
import com.thedndpub.data.dto.classes.ClassSpellcasting;
import com.thedndpub.data.dto.classes.ClassSpellcastingDto;
import com.thedndpub.data.dto.classes.ClassTableCell;
import com.thedndpub.data.dto.classes.ClassTableKind;
import com.thedndpub.data.dto.classes.ClassTableRow;
import com.thedndpub.data.dto.classes.SubclassDefinition;
import com.thedndpub.data.controllers.ClassesController;
import com.thedndpub.data.controllers.SubclassesController;
import com.thedndpub.data.dte.classes.ClassDte;
import com.thedndpub.data.dte.classes.ClassProgressionTableDte;
import com.thedndpub.data.dte.classes.ClassTableCellDte;
import com.thedndpub.data.dte.classes.SubclassDte;
import com.thedndpub.data.references.CrossCatalogResolutionCoordinator;
import com.thedndpub.data.references.ResolutionOutcome;
import com.thedndpub.data.services.ClassDomainSnapshot;
import com.thedndpub.data.services.ClassesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ClassProgressionTablePositionsTests {
    @Autowired
    private ClassDomainSnapshot snapshot;

    @Autowired
    private ClassPublicMapper mapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CrossCatalogResolutionCoordinator references;

    @Autowired
    private ClassesService service;

    @Test
    void identitiesNormalizationGraphReferencesAndLegacyJsonRemainUnchanged() throws Exception {
        ClassNormalizationResult domain = snapshot.result();
        List<String> identities = new ArrayList<>();
        domain.classes().forEach(value -> identities.add(value.id().value()));
        domain.subclasses().forEach(value -> identities.add(value.id().value()));
        domain.classFeatures().forEach(value -> identities.add(value.id().value()));
        domain.subclassFeatures().forEach(value -> identities.add(value.id().value()));
        identities.sort(String::compareTo);
        assertEquals(2_536, identities.size());

        List<Object> publicDefinitions = new ArrayList<>();
        domain.classes().forEach(value -> publicDefinitions.add(mapper.detail(value)));
        domain.subclasses().forEach(value -> publicDefinitions.add(mapper.detail(value)));
        // Captured before changing the mapper; only unordered source-presence sets are sorted.
        assertEquals("8c4924d8280df9e8eec0f35fa9d8d6d4f58f6a59d97862ba3cf70b97e99509ee",
                fingerprint(objectMapper.valueToTree(identities)));
        assertEquals("2282af49184ca0c128e00a5fdd2bddb9fce1c13676e8e2fc942da3d2e202e92e",
                fingerprint(objectMapper.valueToTree(domain)));
        assertEquals("41595bcf2ece3f6c3a8ad7b30af127005e81e9877ced39c7e73f0bde4a8b5814",
                fingerprint(objectMapper.valueToTree(domain.featureGraph())));
        assertEquals("bdd0c7e036b87b211456b43a463a6781a0a7754aab638b0fbc6f5050a1f48036",
                fingerprint(objectMapper.valueToTree(references.snapshot().results())));
        assertEquals("6df24c929eb5b92fa9aa6ff56e41732bcbfe263264b246f048a452333f927166",
                fingerprint(objectMapper.valueToTree(publicDefinitions)));
        assertEquals(2_725, domain.featureGraph().size());
        assertEquals(6_246, references.snapshot().results().size());
        assertEquals(Map.of(ResolutionOutcome.RESOLVED, 2_517L, ResolutionOutcome.AMBIGUOUS, 2_054L,
                ResolutionOutcome.UNRESOLVED, 181L, ResolutionOutcome.DEFERRED, 1_494L),
                references.snapshot().countsByOutcome());
    }

    @Test
    void barbarianRageDamageKeepsColumnOneWhileCompactCellsRemainUnchanged() throws Exception {
        var barbarian = service.classById("class:phb:barbarian");
        var table = barbarian.tables().getFirst();
        var row = table.rows().getFirst();
        assertEquals(List.of("Rages", "Rage Damage"), table.columns());
        assertEquals(1, row.level());
        assertEquals(1, row.cells().size());
        assertEquals(ClassTableCellKind.BONUS, row.cells().getFirst().kind());
        assertEquals(2, row.cells().getFirst().number());
        assertEquals(1, row.positionedCells().getFirst().columnIndex());
        assertEquals(2, row.positionedCells().getFirst().number());

        JsonNode json = objectMapper.readTree(objectMapper.writeValueAsBytes(row));
        assertEquals(objectMapper.readTree("""
                {"level":1,"cells":[{"kind":"BONUS","number":2}],
                "positionedCells":[{"columnIndex":1,"kind":"BONUS","number":2}]}
                """), json);
    }

    @Test
    void allNinetySevenAuthoredRowsAndSixRootsMatchOriginalDteColumnIndexes() throws Exception {
        Map<String, Integer> affected = new TreeMap<>();
        for (var record : ClassTestSources.fullCorpus().records()) {
            List<ClassProgressionTableDte> sourceTables;
            List<ClassProgressionTableDto> publicTables;
            String owner;
            if (record.data() instanceof ClassDte source) {
                ClassDefinition definition = snapshot.result().classes().stream()
                        .filter(value -> value.provenance().authored().location().equals(record.location()))
                        .findFirst().orElseThrow();
                sourceTables = source.classTableGroups();
                publicTables = mapper.detail(definition).tables();
                owner = "class|" + source.name() + "|" + source.source();
            } else if (record.data() instanceof SubclassDte source) {
                SubclassDefinition definition = snapshot.result().subclasses().stream()
                        .filter(value -> value.provenance().authored().location().equals(record.location()))
                        .findFirst().orElseThrow();
                sourceTables = source.subclassTableGroups();
                publicTables = mapper.detail(definition).tables();
                owner = "subclass|" + source.name() + "|" + source.source();
            } else {
                continue;
            }
            if (sourceTables == null) {
                continue;
            }
            for (int tableIndex = 0; tableIndex < sourceTables.size(); tableIndex++) {
                var table = sourceTables.get(tableIndex);
                if (table.rows() == null) {
                    continue;
                }
                for (int rowIndex = 0; rowIndex < table.rows().size(); rowIndex++) {
                    var sourceRow = table.rows().get(rowIndex);
                    boolean withheld = false;
                    boolean shifted = false;
                    for (var cell : sourceRow) {
                        if (cell.text() != null) {
                            withheld = true;
                        } else if (withheld) {
                            shifted = true;
                        }
                    }
                    if (shifted) {
                        affected.merge(owner, 1, Integer::sum);
                        assertAuthoredRow(sourceRow, publicTables.get(tableIndex).rows().get(rowIndex));
                    }
                }
            }
        }
        assertEquals(Map.of("class|Artificer|EFA", 1, "class|Barbarian|PHB", 20,
                "class|Barbarian|XPHB", 20, "class|Warlock|PHB", 20,
                "subclass|Psi Warrior|XPHB", 18, "subclass|Soulknife|XPHB", 18), affected);
        assertEquals(97, affected.values().stream().mapToInt(Integer::intValue).sum());
    }

    @Test
    void everyEffectiveTablePathPreservesSourceValuesPositionsAndOrder() throws Exception {
        ClassNormalizationResult domain = snapshot.result();
        Map<String, Integer> affected = new TreeMap<>();
        int nestedAffectedRows = 0;
        domain.classes().forEach(value -> {
            var detail = mapper.detail(value);
            int count = assertTables(value.tables(), detail.tables());
            if (count > 0) {
                affected.put(value.id().value(), count);
            }
        });
        domain.subclasses().forEach(value -> {
            var detail = mapper.detail(value);
            int count = assertTables(value.tables(), detail.tables());
            if (count > 0) {
                affected.put(value.id().value(), count);
            }
        });
        for (var value : domain.classes()) {
            nestedAffectedRows += assertSpellcasting(value.spellcasting(), mapper.detail(value).spellcasting());
        }
        for (var value : domain.subclasses()) {
            nestedAffectedRows += assertSpellcasting(value.spellcasting(), mapper.detail(value).spellcasting());
        }
        assertEquals(Map.of("class:efa:artificer", 1, "class:phb:barbarian", 20,
                "class:xphb:barbarian", 20, "class:phb:warlock", 20,
                "subclass:xphb:xphb:fighter:psi-warrior", 18,
                "subclass:xphb:xphb:rogue:soulknife", 18), affected);
        assertEquals(97, affected.values().stream().mapToInt(Integer::intValue).sum());
        assertEquals(20, nestedAffectedRows);

        String json = objectMapper.writeValueAsString(domain.classes().stream().map(mapper::detail).toList());
        assertFalse(json.contains("{@"));
        assertFalse(json.contains("PRESENTATION_TEXT"));
    }

    @Test
    void copiedSubclassTablesKeepInheritedSourcePositions() {
        for (String id : List.of("subclass:phb:xphb:fighter:eldritch-knight",
                "subclass:phb:xphb:rogue:arcane-trickster")) {
            var owner = snapshot.result().subclasses().stream()
                    .filter(value -> value.id().value().equals(id)).findFirst().orElseThrow();
            assertFalse(owner.provenance().copyBases().isEmpty());
            assertFalse(owner.tables().isEmpty());
            assertTables(owner.tables(), mapper.detail(owner).tables());
            assertSpellcasting(owner.spellcasting(), mapper.detail(owner).spellcasting());
        }
    }

    @Test
    void leadingInterveningTrailingAndMultipleWithheldCellsNeverRenumberValues() throws Exception {
        var row = new ClassTableRow(1, List.of(text(), text(), number(0), text(),
                new ClassTableCell(ClassTableCellKind.BONUS, 2, null, List.of(), null), text(),
                new ClassTableCell(ClassTableCellKind.DICE, null, null, List.of(new ClassHitDice(1, 6)), false),
                new ClassTableCell(ClassTableCellKind.SPEED_BONUS, 0, null, List.of(), null), text()));
        var table = new ClassProgressionTable(7, ClassTableKind.LEVEL_PRESENTATION, "Safe Title",
                List.of("A", "{@dice 1d6}", "<hidden>", "D", "E", "F", "G", "H", "I"),
                List.of(row), List.of());
        var projected = mapper.detail(withTables(List.of(table))).tables().getFirst();
        assertEquals(List.of(2, 4, 6, 7), projected.rows().getFirst().positionedCells().stream()
                .map(ClassPositionedTableCellDto::columnIndex).toList());
        assertEquals(List.of("A", "Column 2", "Column 3", "D", "E", "F", "G", "H", "I"),
                projected.columns());
        assertTables(List.of(table), List.of(projected));
        String json = objectMapper.writeValueAsString(projected);
        assertFalse(json.contains("WITHHELD_SOURCE"));
        assertFalse(json.contains("{@"));
        assertFalse(json.contains("<hidden>"));
        assertFalse(json.contains("\"text\""));
    }

    @Test
    void unfilteredFullyWithheldAndEmptyRowsKeepOrderAndOmitEmptyCollections() throws Exception {
        var complete = new ClassProgressionTable(4, ClassTableKind.LEVEL_PRESENTATION, null,
                List.of("A", "B"), List.of(new ClassTableRow(1, List.of(number(0), number(1))),
                new ClassTableRow(2, List.of(text(), text()))), List.of());
        var empty = new ClassProgressionTable(2, ClassTableKind.SPELL_SLOTS, null,
                List.of(), List.of(new ClassTableRow(0, List.of())), List.of());
        var projected = mapper.detail(withTables(List.of(complete, empty))).tables();
        assertEquals(List.of(4, 2), projected.stream().map(ClassProgressionTableDto::order).toList());
        assertEquals(List.of(0, 1), projected.getFirst().rows().getFirst().positionedCells().stream()
                .map(ClassPositionedTableCellDto::columnIndex).toList());
        assertTables(List.of(complete, empty), projected);
        assertEquals(objectMapper.readTree("{\"level\":2}"),
                objectMapper.valueToTree(projected.getFirst().rows().get(1)));
        assertEquals(objectMapper.readTree("{\"level\":0}"),
                objectMapper.valueToTree(projected.get(1).rows().getFirst()));
        assertFalse(objectMapper.valueToTree(projected.get(1)).has("columns"));
        assertEquals(objectMapper.readTree("{}"),
                objectMapper.valueToTree(new ClassProgressionTableRowDto(null, null, null)));
    }

    @Test
    void positionedValuesPreserveZeroFalseAndEverySafeCellKind() throws Exception {
        var cells = List.of(number(0), new ClassTableCell(ClassTableCellKind.BONUS, -2, null, List.of(), null),
                new ClassTableCell(ClassTableCellKind.DICE, null, null, List.of(new ClassHitDice(1, 8)), false),
                new ClassTableCell(ClassTableCellKind.SPEED_BONUS, 0, null, List.of(), null));
        var table = new ClassProgressionTable(0, ClassTableKind.LEVEL_PRESENTATION, null,
                List.of("A", "B", "C", "D"), List.of(new ClassTableRow(1, cells)), List.of());
        var result = mapper.detail(withTables(List.of(table))).tables().getFirst().rows().getFirst();
        assertTables(List.of(table), mapper.detail(withTables(List.of(table))).tables());
        JsonNode json = objectMapper.readTree(objectMapper.writeValueAsBytes(result.positionedCells()));
        assertTrue(json.get(0).has("columnIndex"));
        assertTrue(json.get(0).has("number"));
        assertEquals(0, json.get(0).path("columnIndex").intValue());
        assertEquals(0, json.get(0).path("number").intValue());
        assertFalse(json.get(0).has("dice"));
        assertFalse(json.get(0).has("rollable"));
        assertEquals(-2, json.get(1).path("number").intValue());
        assertTrue(json.get(2).has("rollable"));
        assertFalse(json.get(2).path("rollable").booleanValue());
        assertEquals(0, json.get(3).path("number").intValue());
    }

    @Test
    void newDtoCollectionsAreDefensivelyCopiedAndImmutable() {
        List<ClassHitDice> dice = new ArrayList<>(List.of(new ClassHitDice(1, 6)));
        var positioned = new ClassPositionedTableCellDto(0, ClassTableCellKind.DICE, null, dice, false);
        dice.clear();
        assertEquals(1, positioned.dice().size());
        assertThrows(UnsupportedOperationException.class, () -> positioned.dice().clear());

        List<ClassTableCell> cells = new ArrayList<>(List.of(number(0)));
        List<ClassPositionedTableCellDto> positions = new ArrayList<>(List.of(positioned));
        var row = new ClassProgressionTableRowDto(1, cells, positions);
        cells.clear();
        positions.clear();
        assertEquals(1, row.cells().size());
        assertEquals(1, row.positionedCells().size());
        assertThrows(UnsupportedOperationException.class, () -> row.cells().clear());
        assertThrows(UnsupportedOperationException.class, () -> row.positionedCells().clear());

        List<String> columns = new ArrayList<>(List.of("Value"));
        List<ClassProgressionTableRowDto> rows = new ArrayList<>(List.of(row));
        var table = new ClassProgressionTableDto(0, ClassTableKind.SPELL_SLOTS, null, columns, rows, null);
        columns.clear();
        rows.clear();
        assertEquals(1, table.columns().size());
        assertEquals(1, table.rows().size());
        assertThrows(UnsupportedOperationException.class, () -> table.columns().clear());
        assertThrows(UnsupportedOperationException.class, () -> table.rows().clear());
        assertThrows(UnsupportedOperationException.class, () -> table.subclasses().clear());

        List<ClassLevelCount> counts = new ArrayList<>(List.of(new ClassLevelCount(1, 0)));
        List<ClassProgressionTableDto> tables = new ArrayList<>(List.of(table));
        var spellcasting = new ClassSpellcastingDto(null, null, counts, counts, counts, null, null,
                counts, false, null, tables, tables, null);
        counts.clear();
        tables.clear();
        assertEquals(1, spellcasting.cantrips().size());
        assertEquals(1, spellcasting.slotTables().size());
        for (List<?> collection : List.of(spellcasting.cantrips(), spellcasting.known(), spellcasting.prepared(),
                spellcasting.fixedKnown(), spellcasting.fixedByLevel(), spellcasting.slotTables(),
                spellcasting.pactTables(), spellcasting.additionalSpells())) {
            assertThrows(UnsupportedOperationException.class, collection::clear);
        }
        assertThrows(IllegalArgumentException.class,
                () -> new ClassPositionedTableCellDto(-1, ClassTableCellKind.NUMBER, 0, null, null));
        assertThrows(IllegalArgumentException.class,
                () -> new ClassPositionedTableCellDto(0, ClassTableCellKind.PRESENTATION_TEXT, null, null, null));
    }

    @Test
    void existingClassAndSubclassRoutesExposePositionsForAllSixAffectedOwners() throws Exception {
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new ClassesController(service),
                new SubclassesController(service)).build();
        for (var owner : snapshot.result().classes()) {
            if (affectedRows(owner.tables()) > 0) {
                assertRouteRows(mvc, "/api/classes/" + owner.id().value(), mapper.detail(owner).tables());
            }
        }
        for (var owner : snapshot.result().subclasses()) {
            if (affectedRows(owner.tables()) > 0) {
                assertRouteRows(mvc, "/api/subclasses/" + owner.id().value(), mapper.detail(owner).tables());
            }
        }
        JsonNode matches = objectMapper.readTree(mvc.perform(get("/api/classes/by-name/Barbarian")
                .param("source", "PHB")).andExpect(status().isOk()).andReturn().getResponse().getContentAsString());
        assertEquals(1, matches.size());
        assertEquals(1, matches.get(0).path("tables").get(0).path("rows").get(0)
                .path("positionedCells").get(0).path("columnIndex").intValue());
    }

    private void assertRouteRows(MockMvc mvc, String route, List<ClassProgressionTableDto> expected) throws Exception {
        JsonNode response = objectMapper.readTree(mvc.perform(get(route)).andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString());
        assertEquals(objectMapper.valueToTree(expected), response.path("tables"));
    }

    private ClassDefinition withTables(List<ClassProgressionTable> tables) {
        ClassDefinition source = snapshot.result().classes().getFirst();
        return new ClassDefinition(source.id(), source.identity(), source.name(), source.source(),
                source.declaredEdition(), source.sidekick(), source.hitDice(), source.savingThrows(),
                source.primaryAbilities(), source.startingOptions(), source.multiclassing(), source.features(),
                source.subclasses(), tables, source.choices(), null, source.sources(), source.provenance());
    }

    private static ClassTableCell text() {
        return new ClassTableCell(ClassTableCellKind.PRESENTATION_TEXT, null,
                "WITHHELD_SOURCE {@damage 99d99}", List.of(), null);
    }

    private static ClassTableCell number(int value) {
        return new ClassTableCell(ClassTableCellKind.NUMBER, value, null, List.of(), null);
    }

    private static int assertSpellcasting(ClassSpellcasting source, ClassSpellcastingDto projected) {
        if (source == null) {
            assertEquals(null, projected);
            return 0;
        }
        return assertTables(source.slotTables(), projected.slotTables())
                + assertTables(source.pactTables(), projected.pactTables());
    }

    private static int assertTables(List<ClassProgressionTable> source, List<ClassProgressionTableDto> projected) {
        assertEquals(source.size(), projected.size());
        for (int tableIndex = 0; tableIndex < source.size(); tableIndex++) {
            var table = source.get(tableIndex);
            var publicTable = projected.get(tableIndex);
            assertEquals(table.order(), publicTable.order());
            assertEquals(table.kind(), publicTable.kind());
            assertEquals(table.columns().size(), publicTable.columns().size());
            assertEquals(table.subclasses(), publicTable.subclasses());
            assertEquals(table.rows().size(), publicTable.rows().size());
            for (int rowIndex = 0; rowIndex < table.rows().size(); rowIndex++) {
                var row = table.rows().get(rowIndex);
                var publicRow = publicTable.rows().get(rowIndex);
                assertEquals(row.level(), publicRow.level());
                List<ClassTableCell> safeCells = row.cells().stream()
                        .filter(cell -> cell.kind() != ClassTableCellKind.PRESENTATION_TEXT).toList();
                assertEquals(safeCells, publicRow.cells());
                assertEquals(safeCells.size(), publicRow.positionedCells().size());
                int retainedIndex = 0;
                for (int columnIndex = 0; columnIndex < row.cells().size(); columnIndex++) {
                    var cell = row.cells().get(columnIndex);
                    if (cell.kind() == ClassTableCellKind.PRESENTATION_TEXT) {
                        continue;
                    }
                    var positioned = publicRow.positionedCells().get(retainedIndex++);
                    assertEquals(columnIndex, positioned.columnIndex());
                    assertEquals(cell.kind(), positioned.kind());
                    assertEquals(cell.number(), positioned.number());
                    assertEquals(cell.dice(), positioned.dice());
                    assertEquals(cell.rollable(), positioned.rollable());
                }
            }
        }
        return affectedRows(source);
    }

    private static void assertAuthoredRow(List<ClassTableCellDte> source, ClassProgressionTableRowDto projected) {
        int retainedIndex = 0;
        for (int columnIndex = 0; columnIndex < source.size(); columnIndex++) {
            var cell = source.get(columnIndex);
            if (cell.text() != null) {
                continue;
            }
            var positioned = projected.positionedCells().get(retainedIndex++);
            assertEquals(columnIndex, positioned.columnIndex());
            if (cell.number() != null) {
                assertEquals(ClassTableCellKind.NUMBER, positioned.kind());
                assertEquals(cell.number(), positioned.number());
            } else {
                ClassTableCellKind kind = switch (cell.type()) {
                    case "bonus" -> ClassTableCellKind.BONUS;
                    case "bonusSpeed" -> ClassTableCellKind.SPEED_BONUS;
                    case "dice" -> ClassTableCellKind.DICE;
                    default -> throw new AssertionError("Unexpected authored progression cell " + cell.type());
                };
                assertEquals(kind, positioned.kind());
                assertEquals(cell.value(), positioned.number());
                List<ClassHitDice> expectedDice = cell.toRoll() == null ? List.of() : cell.toRoll().stream()
                        .map(die -> new ClassHitDice(die.number(), die.faces()))
                        .toList();
                assertEquals(expectedDice, positioned.dice());
                assertEquals(cell.rollable(), positioned.rollable());
            }
        }
        assertEquals(retainedIndex, projected.positionedCells().size());
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
                if (!name.equals("positionedCells")) {
                    JsonNode child = canonical(value.get(name));
                    if (child.isArray() && Set.of("present", "explicitNulls", "inheritedFields", "suppressedMetadata")
                            .contains(name)) {
                        List<JsonNode> sorted = new ArrayList<>();
                        child.forEach(sorted::add);
                        sorted.sort(java.util.Comparator.comparing(JsonNode::toString));
                        ArrayNode array = objectMapper.createArrayNode();
                        sorted.forEach(array::add);
                        child = array;
                    }
                    result.set(name, child);
                }
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

    private static int affectedRows(List<ClassProgressionTable> tables) {
        int count = 0;
        for (var table : tables) {
            for (var row : table.rows()) {
                boolean withheld = false;
                for (var cell : row.cells()) {
                    if (cell.kind() == ClassTableCellKind.PRESENTATION_TEXT) {
                        withheld = true;
                    } else if (withheld) {
                        count++;
                        break;
                    }
                }
            }
        }
        return count;
    }
}
