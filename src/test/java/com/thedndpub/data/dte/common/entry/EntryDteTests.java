package com.thedndpub.data.dte.common.entry;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EntryDteTests {
    private final ObjectMapper mapper = new ObjectMapper().enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    @Test
    void deserializesMonsterEntryContentAcrossTheWholeCollection() throws Exception {
        Path directory = Path.of("src/main/resources/data/monsters");
        List<Path> files;
        try (var walk = Files.list(directory)) {
            files = walk.filter(p -> p.getFileName().toString().startsWith("bestiary-") && p.toString().endsWith(".json")).sorted().toList();
        }
        int entities = 0, entryNodes = 0, cells = 0, excludedSpellcasting = 0;
        List<String> failures = new ArrayList<>();
        for (Path file : files) {
            JsonNode root = mapper.readTree(file.toFile());
            JsonNode monsters = root.path("monster");
            int index = 0;
            for (JsonNode monster : monsters) {
                String name = monster.path("name").asText("<missing-name>");
                for (String section : List.of("trait", "action", "bonus", "reaction", "legendary", "mythic", "variant")) {
                    JsonNode list = monster.path(section);
                    for (int i = 0; i < list.size(); i++) {
                        JsonNode feature = list.get(i).path("entries");
                        int[] counts = {entryNodes, cells, excludedSpellcasting};
                        walk(feature, file.getFileName().toString(), name, index, "$." + section + "[" + i + "].entries", counts, failures);
                        entryNodes = counts[0];
                        cells = counts[1];
                        excludedSpellcasting = counts[2];
                    }
                }
                JsonNode spellcasting = monster.path("spellcasting");
                if (spellcasting.isArray()) {
                    excludedSpellcasting += spellcasting.size();
                }
                entities++;
                index++;
            }
        }
        System.out.printf("Monster entry validation: files=%d entities=%d entryNodes=%d tableCells=%d excludedSpellcasting=%d failures=%d%n", files.size(), entities, entryNodes, cells, excludedSpellcasting, failures.size());
        assertEquals(111, files.size());
        assertEquals(4560, entities);
        assertTrue(failures.isEmpty(), () -> "Monster entry validation failures:\n" + String.join("\n", failures));
    }

    @Test
    void parsesRecursiveAndSourceSpecificVariants() throws Exception {
        EntryDte result = mapper.readValue("""
                {"type":"list","style":"list-hang-notitle","items":[
                  {"type":"item","name":"A","entry":"{@damage 1d6}"},
                  {"type":"itemSub","name":"B","entries":[{"type":"variantInner","name":"C","entries":["text"]}]}]}
                """, EntryDte.class);
        ListEntryDte list = assertInstanceOf(ListEntryDte.class, result);
        assertInstanceOf(ListItemEntryDte.class, list.items().get(0));
        NamedEntryDte sub = assertInstanceOf(NamedEntryDte.class, list.items().get(1));
        assertEquals("itemSub", sub.type());
        assertEquals("variantInner", assertInstanceOf(NamedEntryDte.class, sub.entries().get(0)).type());
        assertEquals("{@damage 1d6}", assertInstanceOf(ListItemEntryDte.class, list.items().get(0)).entry());
    }

    @Test
    void parsesStructuredTableCellWithoutFlattening() throws Exception {
        TableEntryDte table = (TableEntryDte) mapper.readValue("""
                {"type":"table","rows":[[{"type":"cell","name":"Choice","entries":["{@spell fireball}"]},"plain"]]}
                """, EntryDte.class);
        StructuredTableCellDte cell = assertInstanceOf(StructuredTableCellDte.class, table.rows().get(0).get(0));
        assertEquals("cell", cell.type());
        assertEquals("Choice", cell.name());
        assertEquals("{@spell fireball}", assertInstanceOf(TextEntryDte.class, cell.entries().get(0)).text());
    }

    @Test
    void parsesEveryObservedEntryVariantAndPreservesMetadata() throws Exception {
        EntryDte inset = mapper.readValue("{\"type\":\"inset\",\"name\":\"Inset\",\"source\":\"TST\",\"page\":2,\"entries\":[\"text\"]}", EntryDte.class);
        assertEquals("TST", assertInstanceOf(InsetEntryDte.class, inset).source());
        QuoteEntryDte quote = assertInstanceOf(QuoteEntryDte.class, mapper.readValue("{\"type\":\"quote\",\"by\":\"A\",\"entries\":[\"quoted\"]}", EntryDte.class));
        assertEquals("A", quote.by());
        assertEquals("quoted", assertInstanceOf(TextEntryDte.class, quote.entries().get(0)).text());
        TableEntryDte table = assertInstanceOf(TableEntryDte.class, mapper.readValue("{\"type\":\"table\",\"rows\":[[{\"type\":\"cell\",\"roll\":{\"exact\":4,\"pad\":true}}]]}", EntryDte.class));
        RollTableCellDte roll = assertInstanceOf(RollTableCellDte.class, table.rows().get(0).get(0));
        assertEquals(4, roll.roll().exact());
        TableEntryDte numericTable = assertInstanceOf(TableEntryDte.class,
                mapper.readValue("{\"type\":\"table\",\"rows\":[[1,\"result\"]]}", EntryDte.class));
        assertEquals(1, assertInstanceOf(NumericTableCellDte.class,
                numericTable.rows().get(0).get(0)).value());
        assertTrue(roll.roll().pad());
    }

    @Test
    void rejectsUnknownTypesPropertiesAndMalformedCellsWithPaths() {
        assertBad("{\"type\":\"mystery\"}", "$entry");
        assertBad("{\"type\":\"entries\",\"entries\":[{\"type\":\"entries\",\"oops\":1}]}", "oops");
        assertBad("{\"type\":\"table\",\"rows\":[3]}", "rows[0]");
    }

    private void assertBad(String json, String path) {
        var error = assertThrows(Exception.class, () -> mapper.readValue(json, EntryDte.class));
        assertTrue(error.getMessage().contains(path), error::getMessage);
    }

    private void walk(JsonNode node, String file, String entity, int index, String path, int[] counts, List<String> failures) {
        if (node == null || node.isMissingNode() || node.isNull()) {
            return;
        }
        if (node.isArray()) {
            for (int i = 0; i < node.size(); i++) {
                walk(node.get(i), file, entity, index, path + "[" + i + "]", counts, failures);
            }
            return;
        }
        if (node.isTextual()) {
            counts[0]++;
            try {
                mapper.treeToValue(node, EntryDte.class);
            } catch (Exception e) {
                failures.add(file + " entity '" + entity + "' index " + index + " path " + path + ": " + e.getMessage());
            }
            return;
        }
        if (!node.isObject()) {
            return;
        }
        String type = node.path("type").asText("");
        if (type.equals("spellcasting")) {
            counts[2]++;
            return;
        }
        counts[0]++;
        try {
            mapper.treeToValue(node, EntryDte.class);
        } catch (Exception e) {
            failures.add(file + " entity '" + entity + "' index " + index + " path " + path + ": " + e.getMessage());
        }
        JsonNode rows = node.path("rows");
        if (rows.isArray()) {
            for (JsonNode row : rows) {
                if (row.isArray()) {
                    counts[1] += row.size();
                }
            }
        }
        for (String key : List.of("entries", "items")) {
            if (node.has(key)) {
                walk(node.get(key), file, entity, index, path + "." + key, counts, failures);
            }
        }
        if (rows.isArray()) for (int r = 0; r < rows.size(); r++)
            for (int c = 0; c < rows.get(r).size(); c++) {
                JsonNode cell = rows.get(r).get(c);
                if (cell.isObject() && cell.has("entries")) {
                    walk(cell.get("entries"), file, entity, index, path + ".rows[" + r + "][" + c + "].entries", counts, failures);
                }
            }
    }
}
