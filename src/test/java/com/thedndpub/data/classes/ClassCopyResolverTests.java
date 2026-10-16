package com.thedndpub.data.classes;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.thedndpub.data.dte.classes.SubclassDte;
import com.thedndpub.data.dte.classes.SubclassFeatureDte;
import com.thedndpub.data.importer.classes.ClassCopyResolver;
import com.thedndpub.data.importer.classes.ClassIdentities;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ClassCopyResolverTests {
    private final ClassCopyResolver resolver = new ClassCopyResolver();

    @Test
    void resolvesEveryAuthoredCopyWithoutAddingDefinitionsOrMutatingSources() throws Exception {
        var corpus = ClassTestSources.fullCorpus();
        var before = corpus.toString();
        var resolved = resolver.resolve(corpus);
        assertEquals(2536, resolved.size());
        assertEquals(124, resolved.stream().filter(record -> record.data() instanceof SubclassDte
                && !record.copyBases().isEmpty()).count());
        assertEquals(75, resolved.stream().filter(record -> record.data() instanceof SubclassFeatureDte
                && !record.copyBases().isEmpty()).count());
        assertTrue(resolved.stream().filter(record -> record.data() instanceof SubclassFeatureDte)
                .map(record -> (SubclassFeatureDte) record.data()).allMatch(feature -> feature.entries() != null));
        assertEquals(before, corpus.toString());
    }

    @Test
    void protectsUnlistedMetadataAndHonorsExplicitPreservation() throws Exception {
        var resolved = resolver.resolve(ClassTestSources.read("""
                {"subclass":[
                  {"name":"Base","shortName":"B","source":"PHB","className":"Wizard","classSource":"PHB",
                   "page":12,"edition":"classic","srd":true,"basicRules":true,"hasFluffImages":true,
                   "subclassFeatures":["F|Wizard||B||2"]},
                  {"name":"Copy","shortName":"B","source":"PHB","className":"Wizard","classSource":"XPHB",
                   "_copy":{"name":"Base","shortName":"B","source":"PHB","className":"Wizard",
                            "classSource":"PHB","_preserve":{"page":true,"srd":true}}}
                ]}
                """));
        var copy = resolved.stream().filter(record -> record.data().name().equals("Copy")).findFirst().orElseThrow();
        var data = (SubclassDte) copy.data();
        assertEquals(12, data.page());
        assertEquals(true, data.srd());
        assertNull(data.basicRules());
        assertNull(data.edition());
        assertNull(data.hasFluffImages());
        assertEquals(List.of("F|Wizard||B||2"), data.subclassFeatures());
        assertTrue(copy.inheritedFields().contains("page"));
        assertTrue(copy.suppressedMetadata().contains("edition"));
        assertEquals("XPHB", data.classSource());
    }

    @Test
    void explicitNullFalseAndEmptyListOverrideTheBaseAtomically() throws Exception {
        var resolved = resolver.resolve(ClassTestSources.read("""
                {"subclass":[
                  {"name":"Base","shortName":"B","source":"PHB","className":"Wizard","classSource":"PHB",
                   "page":12,"srd":true,"subclassFeatures":["F|Wizard||B||2"]},
                  {"name":"Copy","shortName":"B","source":"PHB","className":"Wizard","classSource":"XPHB",
                   "page":null,"srd":false,"subclassFeatures":[],
                   "_copy":{"name":"Base","shortName":"B","source":"PHB","className":"Wizard",
                            "classSource":"PHB","_preserve":{"page":true,"srd":true}}}
                ]}
                """));
        var data = (SubclassDte) resolved.stream().filter(record -> record.data().name().equals("Copy"))
                .findFirst().orElseThrow().data();
        assertNull(data.page());
        assertEquals(false, data.srd());
        assertEquals(List.of(), data.subclassFeatures());
    }

    @Test
    void preservesTheSixActualFeatureSrdRequestsAndThreeBasicRulesRequests() throws Exception {
        var resolved = resolver.resolve(ClassTestSources.fullCorpus());
        var copiedFeatures = resolved.stream().filter(record -> record.data() instanceof SubclassFeatureDte
                && !record.copyBases().isEmpty()).map(record -> (SubclassFeatureDte) record.data()).toList();
        assertEquals(6, copiedFeatures.stream().filter(feature -> Boolean.TRUE.equals(feature.srd())).count());
        assertEquals(3, copiedFeatures.stream().filter(feature -> Boolean.TRUE.equals(feature.basicRules())).count());
        assertTrue(copiedFeatures.stream().allMatch(feature -> feature.page() != null));
    }

    @Test
    void rejectsMissingCopyTargetsAndContradictoryShortNames() throws Exception {
        var missing = ClassTestSources.read("""
                {"subclass":[{"name":"X","shortName":"X","source":"PHB","className":"Wizard","classSource":"PHB",
                  "_copy":{"name":"Missing","shortName":"M","source":"PHB","className":"Wizard","classSource":"PHB"}}]}
                """);
        assertThrows(IllegalStateException.class, () -> resolver.resolve(missing));
        var contradictory = ClassTestSources.read("""
                {"subclass":[
                  {"name":"Base","shortName":"B","source":"PHB","className":"Wizard","classSource":"PHB"},
                  {"name":"Copy","shortName":"B","source":"PHB","className":"Wizard","classSource":"XPHB",
                   "_copy":{"name":"Base","shortName":"Wrong","source":"PHB","className":"Wizard","classSource":"PHB"}}
                ]}
                """);
        assertThrows(IllegalStateException.class, () -> resolver.resolve(contradictory));
    }

    @Test
    void rejectsCopyCyclesAndDuplicateIdentities() throws Exception {
        var cycle = ClassTestSources.read("""
                {"subclass":[
                  {"name":"A","shortName":"A","source":"PHB","className":"Wizard","classSource":"PHB",
                   "_copy":{"name":"B","shortName":"B","source":"PHB","className":"Wizard","classSource":"PHB"}},
                  {"name":"B","shortName":"B","source":"PHB","className":"Wizard","classSource":"PHB",
                   "_copy":{"name":"A","shortName":"A","source":"PHB","className":"Wizard","classSource":"PHB"}}
                ]}
                """);
        assertThrows(IllegalStateException.class, () -> resolver.resolve(cycle));
        var duplicates = ClassTestSources.read("""
                {"class":[{"name":"Wizard","source":"PHB"},{"name":"wizard","source":"phb"}]}
                """);
        assertThrows(IllegalStateException.class, () -> resolver.resolve(duplicates));
    }

    @Test
    void rejectsUnsupportedOperationsAndInvalidPreservationPaths() {
        assertThrows(JsonMappingException.class, () -> ClassTestSources.read("""
                {"subclass":[{"_copy":{"_mod":{"entries":{"mode":"replaceTxt"}}}}]}
                """));
        assertThrows(JsonMappingException.class, () -> ClassTestSources.read("""
                {"subclass":[{"_copy":{"_preserve":{"unknown.path":true}}}]}
                """));
    }

    @Test
    void outputAndLineageAreImmutableAndIndependentOfInputOrder() throws Exception {
        var corpus = ClassTestSources.fullCorpus();
        var resolved = resolver.resolve(corpus);
        var reversed = new java.util.ArrayList<>(corpus.documents());
        java.util.Collections.reverse(reversed);
        assertEquals(resolved, resolver.resolve(new com.thedndpub.data.importer.classes.ClassSourceCorpus(reversed)));
        assertThrows(UnsupportedOperationException.class, resolved::clear);
        var copy = resolved.stream().filter(record -> !record.copyBases().isEmpty()).findFirst().orElseThrow();
        assertThrows(UnsupportedOperationException.class, copy.copyBases()::clear);
        assertThrows(UnsupportedOperationException.class, copy.inheritedFields()::clear);
        assertEquals(ClassIdentities.identity(copy.authored().data()), ClassIdentities.identity(copy.data()));
    }
}
