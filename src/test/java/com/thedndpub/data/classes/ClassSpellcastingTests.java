package com.thedndpub.data.classes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.classes.ClassLevelCount;
import com.thedndpub.data.dto.classes.ClassSpellGate;
import com.thedndpub.data.dto.classes.ClassSpellGateKind;
import com.thedndpub.data.dto.classes.ClassSpellGrant;
import com.thedndpub.data.dto.classes.ClassSpellGrantCategory;
import com.thedndpub.data.dto.classes.ClassSpellLevelCount;
import com.thedndpub.data.dto.classes.ClassSpellReference;
import com.thedndpub.data.dto.classes.ClassSpellSelection;
import com.thedndpub.data.dto.classes.ClassSpellSelectionKind;
import com.thedndpub.data.dto.classes.ClassSpellUsageKind;
import com.thedndpub.data.dto.classes.ClassSpellcasting;
import com.thedndpub.data.dte.classes.ClassAdditionalSpellsDte;
import com.thedndpub.data.dte.classes.ClassDte;
import com.thedndpub.data.dte.classes.ClassSpellcastingSourceDte;
import com.thedndpub.data.dte.classes.SubclassDte;
import com.thedndpub.data.importer.classes.ClassSourceCorpus;
import com.thedndpub.data.importer.classes.ClassSpellcastingNormalizer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ClassSpellcastingTests {
    private static ClassSourceCorpus corpus;
    private final ClassSpellcastingNormalizer normalizer = new ClassSpellcastingNormalizer();

    @BeforeAll
    static void load() throws Exception {
        corpus = ClassTestSources.fullCorpus();
    }

    @Test
    void preservesAuthoredCasterCodesWithoutInferringThemFromNames() {
        assertEquals("full", spells("Wizard", "PHB").casterProgression());
        assertEquals("1/2", spells("Paladin", "PHB").casterProgression());
        assertEquals("artificer", spells("Paladin", "XPHB").casterProgression());
        assertEquals("pact", spells("Warlock", "PHB").casterProgression());
        assertNull(spells("Spellcaster Sidekick", "TCE").ability());
        var fighter = classSource("Fighter", "PHB");
        assertNull(normalizer.normalize(fighter, List.of()));
    }

    @Test
    void preservesKnownPreparedAndCantripCountsWithExplicitLevelAssociations() {
        var classic = spells("Bard", "PHB");
        var revised = spells("Bard", "XPHB");
        assertEquals(new ClassLevelCount(1, 4), classic.known().getFirst());
        assertEquals(new ClassLevelCount(20, 22), classic.known().getLast());
        assertTrue(classic.prepared().isEmpty());
        assertTrue(revised.known().isEmpty());
        assertEquals(new ClassLevelCount(1, 4), revised.prepared().getFirst());
        assertEquals("level", revised.preparedChange());
        assertEquals(20, revised.cantrips().size());
    }

    @Test
    void retainsPreparationFormulasWithoutChoosingRoundingOrEvaluatingThem() {
        var artificer = spells("Artificer", "TCE");
        assertEquals("<$level$> / 2 + <$int_mod$>", artificer.preparedExpression());
        assertTrue(artificer.prepared().isEmpty());
        assertEquals("restLong", artificer.preparedChange());
        assertNull(spells("Wizard", "PHB").preparedChange());
    }

    @Test
    void preservesFixedSpellProgressionsSeparatelyFromKnownCountsAndSlots() {
        var warlock = spells("Warlock", "PHB");
        assertEquals(List.of(new ClassSpellLevelCount(11, 6, 1), new ClassSpellLevelCount(13, 7, 1),
                new ClassSpellLevelCount(15, 8, 1), new ClassSpellLevelCount(17, 9, 1)), warlock.fixedByLevel());
        var wizard = spells("Wizard", "PHB");
        assertEquals(20, wizard.fixedKnown().size());
        assertNotNull(wizard.fixedAllowLowerLevel());
    }

    @Test
    void retainsUnqualifiedSpellNamesAndExplicitSourcesAndConfigurationSuffixes() {
        var unqualified = ClassSpellcastingNormalizer.reference("healing word");
        assertNull(unqualified.source());
        var qualified = ClassSpellcastingNormalizer.reference("mending|xphb#c");
        assertEquals("xphb", qualified.source().code());
        assertEquals("mending", qualified.name());
        assertEquals("c", qualified.configuration());
    }

    @Test
    void preservesAllFourGrantCategoriesFromRealSources() {
        Set<ClassSpellGrantCategory> observed = corpus.records().stream()
                .map(record -> record.data()).filter(ClassSpellcastingSourceDte.class::isInstance)
                .map(ClassSpellcastingSourceDte.class::cast)
                .flatMap(source -> normalizer.groups(source.additionalSpells()).stream())
                .flatMap(group -> group.grants().stream()).map(ClassSpellGrant::category).collect(Collectors.toSet());
        assertEquals(Set.of(ClassSpellGrantCategory.values()), observed);
    }

    @Test
    void preservesTheNestedScionOfTheThreeChoiceAndAllThreeCandidates() {
        var scion = corpus.records().stream().map(record -> record.data())
                .filter(SubclassDte.class::isInstance).map(SubclassDte.class::cast)
                .filter(source -> source.name().equals("Scion of the Three") && source.source().equals("FRHoF"))
                .findFirst().orElseThrow();
        var choice = normalizer.groups(scion.additionalSpells()).stream().flatMap(group -> group.grants().stream())
                .flatMap(grant -> grant.selections().stream()).filter(selection -> selection.from().size() == 3)
                .findFirst().orElseThrow();
        assertEquals(1, choice.count());
        assertEquals(List.of("minor illusion", "blade ward", "chill touch"), choice.from().stream()
                .map(ClassSpellReference::name).toList());
        assertTrue(choice.from().stream().allMatch(reference -> reference.configuration().equals("c")));
    }

    @Test
    void distinguishesClassLevelAndSpellLevelGatesAndExpandedSelectors() {
        var grants = spells("Bard", "XPHB").additionalSpells().stream().flatMap(group -> group.grants().stream()).toList();
        assertTrue(grants.stream().anyMatch(grant -> grant.gate().equals(new ClassSpellGate(ClassSpellGateKind.CLASS_LEVEL, 10))));
        assertTrue(grants.stream().anyMatch(grant -> grant.gate().equals(new ClassSpellGate(ClassSpellGateKind.SPELL_LEVEL, 6))));
        assertTrue(grants.stream().filter(grant -> grant.category() == ClassSpellGrantCategory.EXPANDED)
                .flatMap(grant -> grant.selections().stream()).anyMatch(selection -> selection.kind() == ClassSpellSelectionKind.ALL));
    }

    @Test
    void preservesUnrestrictedChoicesWithoutInventingDefaultCounts() {
        var choice = spells("Bard", "PHB").additionalSpells().stream().flatMap(group -> group.grants().stream())
                .flatMap(grant -> grant.selections().stream()).filter(ClassSpellSelection::unrestricted)
                .findFirst().orElseThrow();
        assertEquals(ClassSpellSelectionKind.CHOICE, choice.kind());
        assertNull(choice.count());
        assertTrue(choice.filter().isEmpty());
    }

    @Test
    void preservesRitualResourceRestDailyAbilityAndPerSpellUsageTokens() {
        var usages = corpus.records().stream().map(record -> record.data())
                .filter(ClassSpellcastingSourceDte.class::isInstance).map(ClassSpellcastingSourceDte.class::cast)
                .flatMap(source -> normalizer.groups(source.additionalSpells()).stream())
                .flatMap(group -> group.grants().stream()).map(ClassSpellGrant::usage).toList();
        assertTrue(usages.stream().anyMatch(usage -> usage.kind() == ClassSpellUsageKind.RITUAL));
        assertTrue(usages.stream().anyMatch(usage -> usage.kind() == ClassSpellUsageKind.RESOURCE && usage.count() == 2));
        assertTrue(usages.stream().anyMatch(usage -> usage.kind() == ClassSpellUsageKind.REST && usage.count() == 1));
        assertTrue(usages.stream().anyMatch(usage -> usage.kind() == ClassSpellUsageKind.DAILY && "int".equals(usage.ability())));
        assertTrue(usages.stream().anyMatch(usage -> Boolean.TRUE.equals(usage.each())));
    }

    @Test
    void rejectsUnsupportedUsageGatesAndFilterFields() throws Exception {
        var mapper = new ObjectMapper();
        for (String json : List.of(
                "{\"innate\":{\"bad\":[\"light\"]}}",
                "{\"innate\":{\"3\":{\"daily\":{\"many\":[\"light\"]}}}}",
                "{\"known\":{\"1\":[{\"choose\":\"unknown=some\"}]}}")) {
            var source = mapper.readValue(json, ClassAdditionalSpellsDte.class);
            assertThrows(RuntimeException.class, () -> normalizer.groups(List.of(source)));
        }
    }

    private ClassSpellcasting spells(String name, String source) {
        return normalizer.normalize(classSource(name, source), List.of());
    }

    private static ClassDte classSource(String name, String source) {
        return corpus.records().stream().map(record -> record.data())
                .filter(ClassDte.class::isInstance).map(ClassDte.class::cast)
                .filter(value -> value.name().equals(name) && value.source().equals(source)).findFirst().orElseThrow();
    }
}
