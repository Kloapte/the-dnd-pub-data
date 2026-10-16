package com.thedndpub.data.classes;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dte.classes.ClassDte;
import com.thedndpub.data.dte.classes.ClassEntryDte;
import com.thedndpub.data.dte.classes.ClassEquipmentEntryDte;
import com.thedndpub.data.dte.classes.ClassEquipmentGroupDte;
import com.thedndpub.data.dte.classes.ClassFeatureDte;
import com.thedndpub.data.dte.classes.ClassLevelProgressionDte;
import com.thedndpub.data.dte.classes.ClassOptionsEntryDte;
import com.thedndpub.data.dte.classes.ClassSpellSelectionDte;
import com.thedndpub.data.dte.classes.ClassTableCellDte;
import com.thedndpub.data.dte.classes.SubclassDte;
import com.thedndpub.data.dte.classes.SubclassFeatureDte;
import com.thedndpub.data.importer.classes.ClassSourceCorpus;
import com.thedndpub.data.importer.classes.ClassesSourceLoader;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ClassesDeserializationTests {
    private final ObjectMapper mapper = new ObjectMapper();
    private final ClassesSourceLoader loader = new ClassesSourceLoader(mapper);

    @Test
    void deserializesEveryRootAndAuxiliaryMetadataObject() throws Exception {
        ClassSourceCorpus corpus = loader.load();
        assertEquals(15, corpus.documents().size());
        assertEquals(2536, corpus.records().size());
        assertEquals(30, corpus.records().stream().filter(record -> record.data() instanceof ClassDte).count());
        assertEquals(330, corpus.records().stream().filter(record -> record.data() instanceof SubclassDte).count());
        assertEquals(677, corpus.records().stream().filter(record -> record.data() instanceof ClassFeatureDte).count());
        assertEquals(1499, corpus.records().stream().filter(record -> record.data() instanceof SubclassFeatureDte).count());
        assertEquals(13, corpus.documents().stream().filter(document -> document.data().metadata() != null).count());
    }

    @Test
    void reconstructsEverySourceValueAndAll729ObservedPathsFromTypedModels() throws Exception {
        var projection = new ClassSourceRoundTrip();
        Set<String> paths = new HashSet<>();
        for (var document : loader.load().documents()) {
            try (var input = new org.springframework.core.io.ClassPathResource(document.resource()).getInputStream()) {
                var original = mapper.readTree(input);
                assertEquals(original, projection.sourceTree(document.data()), document.resource());
                original.fields().forEachRemaining(field -> paths(field.getValue(), field.getKey(), paths));
            }
        }
        assertEquals(729, paths.size());
    }

    @Test
    void rejectsUnknownRootNestedAndPolymorphicFields() {
        for (String json : List.of(
                "{\"class\":[{\"name\":\"X\",\"unexpected\":true}]}",
                "{\"class\":[{\"hd\":{\"faces\":8,\"unexpected\":true}}]}",
                "{\"classFeature\":[{\"entries\":[{\"type\":\"options\",\"unknown\":1}]}]}",
                "{\"subclass\":[{\"_copy\":{\"_mod\":{\"entries\":[]}}}]}",
                "{\"class\":[{\"startingEquipment\":{\"defaultData\":[{\"Z\":[]}]}}]}")) {
            assertThrows(JsonMappingException.class, () -> read(json), json);
        }
    }

    @Test
    void rejectsScalarCoercionAndUnknownEntryDiscriminators() {
        assertThrows(JsonMappingException.class, () -> read("{\"class\":[{\"page\":\"1\"}]}"));
        assertThrows(JsonMappingException.class, () -> read("{\"class\":[{\"hd\":{\"faces\":1.5}}]}"));
        assertThrows(JsonMappingException.class, () -> mapper.readValue(
                "{\"type\":\"unknown\"}", ClassEntryDte.class));
    }

    @Test
    void rejectsDuplicatePropertiesTrailingDocumentsAndNonObjectRoots() {
        for (String json : List.of("[]", "null", "{} {}",
                "{\"class\":[{\"name\":\"X\",\"source\":\"PHB\",\"source\":\"XPHB\"}]}")) {
            assertThrows(java.io.IOException.class, () -> read(json));
        }
    }

    @Test
    void preservesAbsentNullAndEmptyCopyOverrides() throws Exception {
        var document = read("{\"subclass\":[{\"name\":\"X\",\"page\":null,\"subclassFeatures\":[]}]}" );
        var root = document.records().getFirst();
        assertTrue(root.fields().explicitNulls().contains("page"));
        assertFalse(root.fields().present().contains("edition"));
        assertEquals(List.of(), ((SubclassDte) root.data()).subclassFeatures());
    }

    @Test
    void preservesTypedTablesAndPresentationStrings() throws Exception {
        var number = mapper.readValue("0", ClassTableCellDte.class);
        var text = mapper.readValue("\"2\"", ClassTableCellDte.class);
        var dice = mapper.readValue(
                "{\"type\":\"dice\",\"toRoll\":[{\"number\":1,\"faces\":6}],\"rollable\":false}",
                ClassTableCellDte.class);
        assertEquals(0, number.number());
        assertEquals("2", text.text());
        assertNull(text.number());
        assertEquals(false, dice.rollable());
        assertEquals(6, dice.toRoll().getFirst().faces());
    }

    @Test
    void preservesArrayAndSparseProgressionsAndSpellChoices() throws Exception {
        var dense = mapper.readValue("[0,2,2]", ClassLevelProgressionDte.class);
        var sparse = mapper.readValue("{\"3\":2,\"7\":3}", ClassLevelProgressionDte.class);
        var choice = mapper.readValue(
                "{\"choose\":{\"from\":[\"minor illusion|xphb#c\"],\"count\":1}}",
                ClassSpellSelectionDte.class);
        assertEquals(List.of(0, 2, 2), dense.dense());
        assertEquals(3, sparse.milestones().get(7));
        assertEquals("minor illusion|xphb#c", choice.choose().from().getFirst());
    }

    @Test
    void leavesSharedEntryConsumersUnchangedWhileAddingClassSpecificForms() throws Exception {
        var shared = mapper.readValue("{\"type\":\"entries\",\"name\":\"Existing\",\"entries\":[\"text\"]}",
                com.thedndpub.data.dte.common.entry.EntryDte.class);
        assertInstanceOf(com.thedndpub.data.dte.common.entry.NamedEntryDte.class, shared);
        String options = "{\"type\":\"options\",\"count\":1,\"entries\":[\"choice\"]}";
        assertInstanceOf(ClassOptionsEntryDte.class, mapper.readValue(options, ClassEntryDte.class));
        assertThrows(JsonMappingException.class, () -> mapper.readValue(options,
                com.thedndpub.data.dte.common.entry.EntryDte.class));
    }

    @Test
    void rejectsMalformedReferencesAndNullOrIncompleteSpellSelections() {
        assertThrows(JsonMappingException.class, () -> mapper.readValue("{\"type\":\"refClassFeature\"}", ClassEntryDte.class));
        assertThrows(JsonMappingException.class, () -> mapper.readValue("{\"choose\":null}", ClassSpellSelectionDte.class));
        assertThrows(JsonMappingException.class, () -> mapper.readValue("{\"choose\":{\"count\":1}}", ClassSpellSelectionDte.class));
        assertThrows(JsonMappingException.class, () -> mapper.readValue(
                "{\"type\":\"statblock\",\"tag\":\"unknown\"}", ClassEntryDte.class));
    }

    @Test
    void defensivelyCopiesSourceBranchListsAndPreservesTheirSourceForms() throws Exception {
        var branch = new java.util.ArrayList<ClassEquipmentEntryDte>();
        var entry = mapper.readValue("\"dagger|phb\"", ClassEquipmentEntryDte.class);
        branch.add(entry);
        var source = new java.util.LinkedHashMap<String, List<ClassEquipmentEntryDte>>();
        source.put("_", branch);
        var group = new ClassEquipmentGroupDte(source);
        branch.clear();
        source.clear();
        assertEquals(1, group.branches().get("_").size());
        assertThrows(UnsupportedOperationException.class, group.branches().get("_")::clear);
        assertTrue(entry.stringForm());
        assertFalse(mapper.readValue("{\"item\":\"dagger|phb\"}", ClassEquipmentEntryDte.class).stringForm());
    }

    private com.thedndpub.data.importer.classes.ClassSourceDocument read(String json) throws Exception {
        return loader.read("fixture.json", new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)));
    }

    private static void paths(com.fasterxml.jackson.databind.JsonNode value, String path, Set<String> paths) {
        if (value.isArray()) {
            value.forEach(child -> paths(child, path + "[]", paths));
            if (!path.contains(".")) {
                return;
            }
        } else if (value.isObject()) {
            value.fields().forEachRemaining(field -> paths(field.getValue(), path + "." + field.getKey(), paths));
        }
        paths.add(path);
    }
}
