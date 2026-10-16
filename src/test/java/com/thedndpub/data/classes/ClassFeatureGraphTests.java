package com.thedndpub.data.classes;

import com.thedndpub.data.dto.classes.ClassDefinitionKind;
import com.thedndpub.data.importer.classes.ClassCopyResolver;
import com.thedndpub.data.importer.classes.ClassIdentities;
import com.thedndpub.data.importer.classes.ClassReferenceResolver;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClassFeatureGraphTests {
    @Test
    void parsesIndependentSubclassAndFeatureSourceDefaults() {
        var adapted = ClassIdentities.featureUid(ClassDefinitionKind.SUBCLASS_FEATURE,
                "Knowledge Domain|Cleric|XPHB|Knowledge||3");
        assertEquals("XPHB", adapted.classSource().code());
        assertEquals("PHB", adapted.subclassSource().code());
        assertEquals("PHB", adapted.source().code());
        var feature = ClassIdentities.featureUid(ClassDefinitionKind.CLASS_FEATURE,
                "Spellcasting|Artificer|TCE|1");
        assertEquals("TCE", feature.source().code());
    }

    @Test
    void repeatedNamesAtDifferentLevelsRemainDifferentDefinitions() {
        var first = ClassIdentities.featureUid(ClassDefinitionKind.CLASS_FEATURE, "Ability Score Improvement|Fighter||4");
        var second = ClassIdentities.featureUid(ClassDefinitionKind.CLASS_FEATURE, "Ability Score Improvement|Fighter||8");
        assertNotEquals(first.lookupKey(), second.lookupKey());
        assertNotEquals(ClassIdentities.id(first), ClassIdentities.id(second));
        assertEquals("class-feature:phb:phb:fighter:4:ability-score-improvement", ClassIdentities.id(first));
    }

    @Test
    void rejectsMissingReferencesInsteadOfUsingNameOrLevelSimilarity() throws Exception {
        var corpus = ClassTestSources.read("""
                {"class":[{"name":"Wizard","source":"PHB","classFeatures":["Missing|Wizard||1"]}],
                 "classFeature":[{"name":"Almost Missing","source":"PHB","className":"Wizard","classSource":"PHB","level":1}]}
                """);
        var resolved = new ClassCopyResolver().resolve(corpus);
        assertThrows(IllegalStateException.class, () -> new ClassReferenceResolver(resolved));
    }

    @Test
    void rejectsCyclicNestedFeatureDependencies() throws Exception {
        var corpus = ClassTestSources.read("""
                {"class":[{"name":"Wizard","source":"PHB","classFeatures":["A|Wizard||1"]}],
                 "classFeature":[
                   {"name":"A","source":"PHB","className":"Wizard","classSource":"PHB","level":1,
                    "entries":[{"type":"refClassFeature","classFeature":"B|Wizard||1"}]},
                   {"name":"B","source":"PHB","className":"Wizard","classSource":"PHB","level":1,
                    "entries":[{"type":"options","count":1,"entries":[{"type":"refClassFeature","classFeature":"A|Wizard||1"}]}]}
                 ]}
                """);
        var resolved = new ClassCopyResolver().resolve(corpus);
        assertThrows(IllegalStateException.class, () -> new ClassReferenceResolver(resolved));
    }

    @Test
    void rejectsReadableIdCollisionsEvenWithDistinctSourceNames() throws Exception {
        var corpus = ClassTestSources.read("""
                {"class":[{"name":"Wizard","source":"PHB"}],
                 "classFeature":[
                   {"name":"A-B","source":"PHB","className":"Wizard","classSource":"PHB","level":1},
                   {"name":"A B","source":"PHB","className":"Wizard","classSource":"PHB","level":1}
                 ]}
                """);
        var resolved = new ClassCopyResolver().resolve(corpus);
        assertThrows(IllegalStateException.class, () -> new ClassReferenceResolver(resolved));
    }

    @Test
    void rejectsAmbiguousSubclassShortNames() throws Exception {
        var corpus = ClassTestSources.read("""
                {"class":[{"name":"Wizard","source":"PHB"}],
                 "subclass":[
                   {"name":"A","shortName":"Same","source":"PHB","className":"Wizard","classSource":"PHB"},
                   {"name":"B","shortName":"Same","source":"PHB","className":"Wizard","classSource":"PHB"}
                 ]}
                """);
        var resolved = new ClassCopyResolver().resolve(corpus);
        assertThrows(IllegalStateException.class, () -> new ClassReferenceResolver(resolved));
    }

    @Test
    void doesNotResolveParentNamesThroughSlugEquivalence() throws Exception {
        var corpus = ClassTestSources.read("""
                {"class":[{"name":"Wizard","source":"PHB"}],
                 "subclass":[{"name":"A","shortName":"A","source":"PHB","className":"Wizard!","classSource":"PHB"}]}
                """);
        var resolved = new ClassCopyResolver().resolve(corpus);
        assertThrows(IllegalStateException.class, () -> new ClassReferenceResolver(resolved));
    }

    @Test
    void accountsForOrphansWithoutDroppingDefinitions() throws Exception {
        var corpus = ClassTestSources.read("""
                {"class":[{"name":"Wizard","source":"PHB"}],
                 "classFeature":[{"name":"Orphan","source":"PHB","className":"Wizard","classSource":"PHB","level":1}]}
                """);
        var resolved = new ClassCopyResolver().resolve(corpus);
        var graph = new ClassReferenceResolver(resolved);
        assertEquals(2, resolved.size());
        assertEquals(1, graph.orphanCount());
    }

    @Test
    void rejectsMalformedUidShapesAndInvalidLevels() {
        assertThrows(IllegalArgumentException.class, () -> ClassIdentities.featureUid(
                ClassDefinitionKind.CLASS_FEATURE, "A|Wizard||not-a-level"));
        assertThrows(IllegalArgumentException.class, () -> ClassIdentities.featureUid(
                ClassDefinitionKind.SUBCLASS_FEATURE, "A|Wizard||School||0"));
        assertThrows(IllegalArgumentException.class, () -> ClassIdentities.featureUid(
                ClassDefinitionKind.CLASS_FEATURE, "A|Wizard"));
    }
}
