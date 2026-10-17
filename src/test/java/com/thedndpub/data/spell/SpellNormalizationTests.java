package com.thedndpub.data.spell;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.spell.SpellConsumeFlag;
import com.thedndpub.data.dto.spell.SpellConsumeNote;
import com.thedndpub.data.dto.spell.SpellDefinition;
import com.thedndpub.data.dto.spell.SpellMaterialDetails;
import com.thedndpub.data.dto.spell.SpellMaterialText;
import com.thedndpub.data.dte.common.entry.TextEntryDte;
import com.thedndpub.data.dte.spell.SpellFileDte;
import com.thedndpub.data.importer.spell.SpellDefinitionNormalizer;
import com.thedndpub.data.importer.spell.SpellSourceCorpus;
import com.thedndpub.data.importer.spell.SpellSourceDocument;
import com.thedndpub.data.importer.spell.SpellSourceLoader;
import com.thedndpub.data.services.SourceRegistry;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SpellNormalizationTests {
    private final ObjectMapper objectMapper = new ObjectMapper()
            .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    @Autowired
    private SourceRegistry sourceRegistry;

    private SpellSourceCorpus corpus;
    private List<SpellDefinition> definitions;

    @BeforeAll
    void loadAndNormalizeCompleteCorpus() {
        corpus = new SpellSourceLoader(objectMapper).load();
        definitions = new SpellDefinitionNormalizer(sourceRegistry).normalize(corpus).definitions();
    }

    @Test
    void normalizesEveryClasspathSpellAndValidatesAllObservedSources() {
        var result = new SpellDefinitionNormalizer(sourceRegistry).normalize(corpus);

        assertEquals(18, result.validation().sourceFileCount());
        assertEquals(969, result.validation().normalizedDefinitionCount());
        assertEquals(106, result.validation().validatedSourceIdentityCount());
        assertEquals(389, result.validation().resolvedReprintReferenceCount());
        assertEquals(969, result.definitions().size());
    }

    @Test
    void createsUniqueSourceAwareIdsAndKeepsPhbAndXphbSeparate() {
        Set<String> ids = definitions.stream()
                .map(definition -> definition.id().value())
                .collect(Collectors.toSet());

        assertEquals(969, ids.size());
        assertTrue(ids.contains("spell:phb:shield"));
        assertTrue(ids.contains("spell:xphb:shield"));
        assertNotEquals(find("Shield", "PHB").id(), find("Shield", "XPHB").id());

        long matchingNameDefinitions = definitions.stream()
                .filter(definition -> definition.identity().name().equals("Fire Bolt"))
                .count();
        assertEquals(2, matchingNameDefinitions);
    }

    @Test
    void resolvesEveryReprintToAnIndependentlyAddressableDefinition() {
        int reprintCount = definitions.stream()
                .mapToInt(definition -> definition.sourceRelationships().reprintedAs().size())
                .sum();

        assertEquals(389, reprintCount);
        for (SpellDefinition definition : definitions) {
            for (var reprint : definition.sourceRelationships().reprintedAs()) {
                assertTrue(definitions.stream().anyMatch(candidate -> candidate.id().equals(reprint.targetId())));
                assertEquals(reprint.targetId(), definitions.stream()
                        .filter(candidate -> candidate.identity().equals(reprint.target()))
                        .findFirst()
                        .orElseThrow()
                        .id());
            }
        }
    }

    @Test
    void retainsCastingAlternativesReactionsRitualsAndAreaRanges() {
        SpellDefinition phbPlantGrowth = find("Plant Growth", "PHB");
        SpellDefinition xphbPlantGrowth = find("Plant Growth", "XPHB");

        assertEquals(2, phbPlantGrowth.castingTimes().size());
        assertEquals(2, xphbPlantGrowth.castingTimes().size());
        assertTrue(xphbPlantGrowth.castingTimes().stream().anyMatch(time -> time.note() != null));
        assertTrue(definitions.stream().flatMap(definition -> definition.castingTimes().stream())
                .anyMatch(time -> "reaction".equals(time.unit()) && time.condition() != null));
        assertEquals(68, definitions.stream().filter(definition -> Boolean.TRUE.equals(definition.ritual())).count());

        Set<String> rangeTypes = definitions.stream()
                .map(SpellDefinition::range)
                .filter(range -> range != null)
                .map(range -> range.type())
                .collect(Collectors.toSet());
        assertTrue(rangeTypes.containsAll(Set.of("point", "cone", "line", "emanation", "special")));
        assertTrue(definitions.stream().map(SpellDefinition::range)
                .filter(range -> range != null && range.distance() != null)
                .anyMatch(range -> range.distance().amount() != null));
        assertEquals(0, find("Acid Splash", "PHB").level());
    }

    @Test
    void preservesMaterialCostsConsumptionAndDurationAlternatives() {
        assertTrue(definitions.stream().map(SpellDefinition::components)
                .filter(components -> components != null)
                .map(components -> components.material())
                .anyMatch(material -> material instanceof SpellMaterialDetails details
                        && details.cost() != null && details.consume() != null));
        assertTrue(definitions.stream().map(SpellDefinition::components)
                .filter(components -> components != null)
                .map(components -> components.material())
                .anyMatch(SpellMaterialText.class::isInstance));
        assertTrue(definitions.stream().map(SpellDefinition::components)
                .filter(components -> components != null)
                .map(components -> components.material())
                .filter(SpellMaterialDetails.class::isInstance)
                .map(SpellMaterialDetails.class::cast)
                .anyMatch(details -> details.consume() instanceof SpellConsumeNote));

        assertTrue(definitions.stream().flatMap(definition -> definition.durations().stream())
                .anyMatch(duration -> Boolean.TRUE.equals(duration.concentration())));
        assertTrue(definitions.stream().flatMap(definition -> definition.durations().stream())
                .anyMatch(duration -> "special".equals(duration.type())));
        assertTrue(definitions.stream().flatMap(definition -> definition.durations().stream())
                .anyMatch(duration -> "permanent".equals(duration.type())));
    }

    @Test
    void preservesStructuredTagsAndDoesNotDeriveEffectsFromEntries() throws Exception {
        assertEquals(361, definitions.stream()
                .filter(definition -> !definition.mechanics().damageInflicted().isEmpty())
                .count());
        assertEquals(406, definitions.stream()
                .filter(definition -> !definition.mechanics().savingThrows().isEmpty())
                .count());
        assertEquals(65, definitions.stream()
                .filter(definition -> !definition.mechanics().spellAttacks().isEmpty())
                .count());
        assertEquals(184, definitions.stream()
                .filter(definition -> !definition.mechanics().conditionsInflicted().isEmpty())
                .count());

        SpellDefinition narrativeOnly = normalize("""
                {"spell":[{"name":"Narrative Only","source":"PHB","level":1,"school":"A",
                  "entries":["This description mentions dealing 3d6 fire damage."]}]}
                """);
        assertTrue(narrativeOnly.mechanics().damageInflicted().isEmpty());
        assertEquals("This description mentions dealing 3d6 fire damage.",
                assertInstanceOf(TextEntryDte.class, narrativeOnly.entries().getFirst()).text());
    }

    @Test
    void preservesTypedScalingAndHigherLevelEntryContent() {
        long scaledSpellCount = definitions.stream()
                .filter(definition -> !definition.scaling().isEmpty())
                .count();
        assertEquals(41, scaledSpellCount);
        assertTrue(definitions.stream().flatMap(definition -> definition.scaling().stream())
                .flatMap(scaling -> scaling.values().stream())
                .anyMatch(value -> value.levelKey().equals("11") && value.formula().contains("d")));
        assertEquals(354, definitions.stream()
                .filter(definition -> !definition.entriesHigherLevel().isEmpty())
                .count());
    }

    @Test
    void preservesZeroFalseAndMakesNormalizedCollectionsImmutable() throws Exception {
        SpellDefinition definition = normalize("""
                {"spell":[{"name":"False and Zero","source":"PHB","level":0,"page":0,
                  "basicRules":false,"components":{"v":false,"m":{"text":"a mote","cost":0,"consume":false}},
                  "entries":[]}]}
                """);

        assertEquals(0, definition.level());
        assertEquals(0, definition.source().page());
        assertEquals(Boolean.FALSE, definition.importMetadata().basicRules());
        assertEquals(Boolean.FALSE, definition.components().verbal());
        SpellMaterialDetails material = assertInstanceOf(SpellMaterialDetails.class,
                definition.components().material());
        assertEquals(0, material.cost());
        assertFalse(((SpellConsumeFlag) material.consume()).value());

        assertThrows(UnsupportedOperationException.class, () -> definitions.clear());
        assertThrows(UnsupportedOperationException.class,
                () -> find("Shield", "PHB").castingTimes().clear());
        assertThrows(UnsupportedOperationException.class,
                () -> find("Shield", "PHB").mechanics().damageInflicted().clear());
    }

    @Test
    void normalizationIsDeterministicAndRejectsUnknownSourcesAndReprints() throws Exception {
        SpellDefinitionNormalizer normalizer = new SpellDefinitionNormalizer(sourceRegistry);
        assertEquals(normalizer.normalize(corpus), normalizer.normalize(corpus));

        SpellSourceCorpus unknownSource = sourceCorpus("""
                {"spell":[{"name":"Unknown Source","source":"ZZZ"}]}
                """);
        IllegalStateException sourceError = assertThrows(IllegalStateException.class,
                () -> normalizer.normalize(unknownSource));
        assertTrue(sourceError.getMessage().contains("Unknown spell source identity 'ZZZ'"));

        SpellSourceCorpus unresolvedReprint = sourceCorpus("""
                {"spell":[{"name":"Known Source","source":"PHB","reprintedAs":["Absent|XPHB"]}]}
                """);
        IllegalStateException reprintError = assertThrows(IllegalStateException.class,
                () -> normalizer.normalize(unresolvedReprint));
        assertTrue(reprintError.getMessage().contains("Unresolved spell reprint target"));
    }

    private SpellDefinition find(String name, String source) {
        return definitions.stream()
                .filter(definition -> definition.identity().name().equals(name))
                .filter(definition -> definition.identity().source().code().equals(source))
                .findFirst()
                .orElseThrow();
    }

    private SpellDefinition normalize(String json) throws Exception {
        SpellSourceCorpus source = sourceCorpus(json);
        return new SpellDefinitionNormalizer(sourceRegistry).normalize(source).definitions().getFirst();
    }

    private SpellSourceCorpus sourceCorpus(String json) throws Exception {
        SpellFileDte file = objectMapper.readValue(json, SpellFileDte.class);
        return new SpellSourceCorpus(List.of(new SpellSourceDocument("synthetic.json", file)));
    }
}
