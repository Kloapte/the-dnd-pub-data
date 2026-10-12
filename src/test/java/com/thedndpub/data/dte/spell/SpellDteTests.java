package com.thedndpub.data.dte.spell;

import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import com.thedndpub.data.dte.common.entry.RollTableCellDte;
import com.thedndpub.data.dte.common.entry.NamedEntryDte;
import com.thedndpub.data.dte.common.entry.TableEntryDte;
import com.thedndpub.data.dte.common.entry.TextEntryDte;
import com.thedndpub.data.dte.common.entry.TextTableCellDte;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SpellDteTests {
    private final ObjectMapper mapper = new ObjectMapper()
            .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    @Test
    void deserializesAllSpellsFromAllSourceFiles() throws Exception {
        Path source = Path.of("src/main/resources/data/spells");
        List<Path> files;
        try (var walk = Files.list(source)) {
            files = walk.filter(Files::isRegularFile).filter(p -> p.toString().endsWith(".json")).sorted().toList();
        }
        assertEquals(18, files.size(), "spell source file count changed; review collection discovery");
        int entityCount = 0;
        List<String> failures = new ArrayList<>();
        for (Path file : files) {
            try (var parser = mapper.createParser(file.toFile())) {
                assertEquals(JsonToken.START_OBJECT, parser.nextToken());
                while (parser.nextToken() != JsonToken.END_OBJECT) {
                    assertEquals(JsonToken.FIELD_NAME, parser.currentToken(), "Unexpected root structure in " + file);
                    String key = parser.currentName();
                    JsonToken valueToken = parser.nextToken();
                    if (!key.equals("spell")) {
                        parser.skipChildren();
                        continue;
                    }
                    assertEquals(JsonToken.START_ARRAY, valueToken, "spell must remain an array in " + file);
                    int index = 0;
                    while (parser.nextToken() != JsonToken.END_ARRAY) {
                        TokenBuffer entityBuffer = new TokenBuffer(parser);
                        entityBuffer.copyCurrentStructure(parser);
                        String entityName = entityName(entityBuffer);
                        try {
                            SpellDte spell;
                            try (var entityParser = entityBuffer.asParser(mapper)) {
                                entityParser.nextToken();
                                spell = mapper.readValue(entityParser, SpellDte.class);
                            }
                            assertNotNull(spell.name(), file + " spell index " + index + " has no name");
                            entityCount++;
                        } catch (Exception error) {
                            failures.add(file.getFileName() + " entity '" + entityName + "' index " + index + " path " + jsonPath(error)
                                    + ": " + error.getMessage());
                        }
                        entityBuffer.close();
                        index++;
                    }
                }
            } catch (IOException error) {
                failures.add(file.getFileName() + " root path $: " + error.getMessage());
            }
        }
        assertTrue(failures.isEmpty(), () -> "Spell DTE validation failures:\n" + String.join("\n", failures));
        assertEquals(969, entityCount, "Every discovered spell must deserialize");
    }

    @Test
    void supportsMaterialComponentAndConsumeVariants() throws Exception {
        SpellDte spell = mapper.readValue("""
                {"name":"Variants","level":1,"source":"TST","school":"A","page":1,
                 "time":[],"range":{"type":"point"},"duration":[],"components":{"m":"a pinch of sand"},
                 "srd":true,"srd52":"Alternate Name","entries":[]}
                """, SpellDte.class);
        assertInstanceOf(MaterialTextDte.class, spell.components().m());
        assertEquals(Boolean.TRUE, spell.srd().listed());
        assertEquals("Alternate Name", spell.srd52().alternateName());
        SpellDte objectMaterial = mapper.readValue("""
                {"name":"Object","level":1,"source":"TST","school":"A","page":1,
                 "time":[],"range":{"type":"point"},"duration":[],
                 "components":{"m":{"text":"a gem","cost":5000,"consume":true}},"entries":[]}
                """, SpellDte.class);
        MaterialDetailsDte details = assertInstanceOf(MaterialDetailsDte.class, objectMaterial.components().m());
        assertEquals(5000, details.cost());
        assertEquals(new ConsumeFlagDte(true), details.consume());
        SpellDte stringConsume = mapper.readValue("""
                {"name":"String Consume","level":1,"source":"TST","school":"A","page":1,
                 "time":[],"range":{"type":"point"},"duration":[],
                 "components":{"m":{"text":"incense","consume":"optional"}},"entries":[]}
                """, SpellDte.class);
        assertEquals(new ConsumeNoteDte("optional"),
                assertInstanceOf(MaterialDetailsDte.class, stringConsume.components().m()).consume());
    }

    @Test
    void preservesRecursiveEntriesInlineMarkupAndTableRollCells() throws Exception {
        SpellDte spell = mapper.readValue("""
                {"name":"Entries","level":1,"source":"TST","school":"A","page":1,
                 "time":[],"range":{"type":"point"},"duration":[],"components":{},
                 "entries":["Deal {@damage 2d6} fire damage.",
                   {"type":"entries","name":"Details","entries":["{@condition prone}"]},
                   {"type":"table","colLabels":["Roll","Effect"],"rows":[["1-2",{"type":"cell","roll":{"min":1,"max":2}}]]}]}
                """, SpellDte.class);
        assertEquals("Deal {@damage 2d6} fire damage.", assertInstanceOf(TextEntryDte.class, spell.entries().get(0)).text());
        NamedEntryDte section = assertInstanceOf(NamedEntryDte.class, spell.entries().get(1));
        assertEquals("Details", section.name());
        assertEquals("{@condition prone}", assertInstanceOf(TextEntryDte.class, section.entries().get(0)).text());
        TableEntryDte table = assertInstanceOf(TableEntryDte.class, spell.entries().get(2));
        assertEquals("1-2", ((TextTableCellDte) table.rows().get(0).get(0)).text());
        RollTableCellDte cell = assertInstanceOf(RollTableCellDte.class, table.rows().get(0).get(1));
        assertEquals(1, cell.roll().min());
    }

    @Test
    void rejectsUnexpectedSpellAndEntryProperties() {
        String spellWithUnknown = """
                {"name":"Unknown","level":1,"source":"TST","school":"A","page":1,
                 "time":[],"range":{"type":"point"},"duration":[],"components":{},"entries":[],"newGameplayField":true}
                """;
        assertThrows(UnrecognizedPropertyException.class, () -> mapper.readValue(spellWithUnknown, SpellDte.class));
        String entryWithUnknown = """
                {"name":"Unknown","level":1,"source":"TST","school":"A","page":1,
                 "time":[],"range":{"type":"point"},"duration":[],"components":{},
                 "entries":[{"type":"entries","newRuleField":"preserve me"}]}
                """;
        com.fasterxml.jackson.databind.JsonMappingException error = assertThrows(
                com.fasterxml.jackson.databind.JsonMappingException.class,
                () -> mapper.readValue(entryWithUnknown, SpellDte.class));
        assertTrue(error.getMessage().contains("newRuleField"));
        String unknownEntryType = """
                {"name":"Unknown Type","level":1,"source":"TST","school":"A","page":1,
                 "time":[],"range":{"type":"point"},"duration":[],"components":{},
                 "entries":[{"type":"future-entry","entries":["rule"]}]}
                """;
        com.fasterxml.jackson.databind.JsonMappingException typeError = assertThrows(
                com.fasterxml.jackson.databind.JsonMappingException.class,
                () -> mapper.readValue(unknownEntryType, SpellDte.class));
        assertTrue(typeError.getMessage().contains("future-entry"));
    }

    private static String jsonPath(Exception error) {
        if (error instanceof com.fasterxml.jackson.databind.JsonMappingException mapping) {
            return mapping.getPath().stream().map(ref -> ref.getFieldName() != null ? "." + ref.getFieldName()
                    : "[" + ref.getIndex() + "]").reduce("$", String::concat);
        }
        return "$";
    }

    private String entityName(TokenBuffer entityBuffer) throws IOException {
        try (var parser = entityBuffer.asParser(mapper)) {
            if (parser.nextToken() != JsonToken.START_OBJECT) {
                return "<not-an-object>";
            }
            while (parser.nextToken() != JsonToken.END_OBJECT) {
                if (parser.currentToken() != JsonToken.FIELD_NAME) {
                    break;
                }
                String field = parser.currentName();
                JsonToken value = parser.nextToken();
                if (field.equals("name") && value == JsonToken.VALUE_STRING) {
                    return parser.getText();
                }
                parser.skipChildren();
            }
        }
        return "<missing-name>";
    }
}
