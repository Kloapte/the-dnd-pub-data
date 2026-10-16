package com.thedndpub.data.classes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.classes.ClassAbilityThreshold;
import com.thedndpub.data.dto.classes.ClassDefinition;
import com.thedndpub.data.dto.classes.ClassEquipmentBranch;
import com.thedndpub.data.dto.classes.ClassEquipmentKind;
import com.thedndpub.data.dto.classes.ClassExternalFeatureNode;
import com.thedndpub.data.dto.classes.ClassExternalKind;
import com.thedndpub.data.dto.classes.ClassExternalReference;
import com.thedndpub.data.dto.classes.ClassFeatureChoice;
import com.thedndpub.data.dto.classes.ClassFeatureReferenceNode;
import com.thedndpub.data.dto.classes.ClassHitDice;
import com.thedndpub.data.dto.classes.ClassLevelCount;
import com.thedndpub.data.dto.classes.ClassNormalizationResult;
import com.thedndpub.data.dto.classes.ClassReferenceStatus;
import com.thedndpub.data.dto.classes.ClassRequirementKind;
import com.thedndpub.data.dto.classes.ClassTableCellKind;
import com.thedndpub.data.importer.classes.ClassDefinitionNormalizer;
import com.thedndpub.data.importer.classes.ClassExternalReferenceResolver;
import com.thedndpub.data.importer.classes.ClassIdentities;
import com.thedndpub.data.importer.classes.ClassSourceCorpus;
import com.thedndpub.data.services.FeatCatalog;
import com.thedndpub.data.services.ItemCatalog;
import com.thedndpub.data.services.OptionalFeatureCatalog;
import com.thedndpub.data.services.MonsterCatalog;
import com.thedndpub.data.services.SourceRegistry;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ClassesNormalizationTests {
    @Autowired
    private SourceRegistry registry;
    @Autowired
    private ItemCatalog items;
    @Autowired
    private OptionalFeatureCatalog optionalFeatures;
    @Autowired
    private FeatCatalog feats;
    @Autowired
    private MonsterCatalog monsters;

    private ClassSourceCorpus corpus;
    private ClassDefinitionNormalizer normalizer;
    private ClassNormalizationResult result;

    @BeforeAll
    void normalizeFullCorpus() throws Exception {
        corpus = ClassTestSources.fullCorpus();
        var external = ClassExternalReferenceResolver.fromExistingCatalogs(
                new ObjectMapper(), registry, items, optionalFeatures, feats, monsters);
        normalizer = new ClassDefinitionNormalizer(registry, external);
        result = normalizer.normalize(corpus);
    }

    @Test
    void producesAllFourDefinitionKindsWithNoDuplicateIds() {
        assertEquals(30, result.classes().size());
        assertEquals(330, result.subclasses().size());
        assertEquals(677, result.classFeatures().size());
        assertEquals(1499, result.subclassFeatures().size());
        assertEquals(13, result.metadata().size());
        assertEquals(18, result.metadata().stream().mapToInt(value -> value.internalCopyKinds().size()).sum());
        assertEquals(2536, allIds().size());
        assertEquals(124, result.validation().subclassCopies());
        assertEquals(75, result.validation().featureCopies());
        System.out.println("Classes Stage B validation: " + result.validation());
    }

    @Test
    void validatesAllAuthoredReferencesAndEveryInheritedEffectiveEdge() {
        assertEquals(2339, result.validation().authoredFeatureReferences());
        assertEquals(0, result.validation().orphanFeatures());
        assertTrue(result.validation().effectiveFeatureReferences() > 2339);
        Set<String> ids = allIds();
        assertTrue(result.featureGraph().stream().allMatch(edge -> ids.contains(edge.ownerId())
                && ids.contains(edge.target().targetId())));
        assertEquals(result.validation().effectiveFeatureReferences(), result.featureGraph().size());
    }

    @Test
    void preservesAllParentClassAndSubclassRelationships() {
        Set<String> classIds = result.classes().stream().map(value -> value.id().value()).collect(Collectors.toSet());
        Set<String> subclassIds = result.subclasses().stream().map(value -> value.id().value()).collect(Collectors.toSet());
        assertTrue(result.subclasses().stream().allMatch(value -> classIds.contains(value.parentClassId().value())));
        assertTrue(result.classFeatures().stream().allMatch(value -> classIds.contains(value.parentClassId().value())));
        assertTrue(result.subclassFeatures().stream().allMatch(value -> classIds.contains(value.parentClassId().value())
                && subclassIds.contains(value.parentSubclassId().value())));
        assertEquals(330, result.classes().stream().mapToInt(value -> value.subclasses().size()).sum());
    }

    @Test
    void keepsEditionIdentitiesAndSubclassMilestonesSeparate() {
        var classic = classById("class:phb:cleric");
        var revised = classById("class:xphb:cleric");
        assertEquals("classic", classic.declaredEdition());
        assertEquals("one", revised.declaredEdition());
        assertEquals(1, classic.features().stream().filter(value -> Boolean.TRUE.equals(value.gainSubclassFeature()))
                .findFirst().orElseThrow().level());
        assertEquals(3, revised.features().stream().filter(value -> Boolean.TRUE.equals(value.gainSubclassFeature()))
                .findFirst().orElseThrow().level());
        var adapted = result.subclasses().stream()
                .filter(value -> value.id().value().equals("subclass:phb:xphb:cleric:knowledge-domain"))
                .findFirst().orElseThrow();
        assertEquals("PHB", adapted.source().identity().code());
        assertEquals("PHB", adapted.features().getFirst().feature().identity().subclassSource().code());
        assertEquals("XPHB", adapted.features().getFirst().feature().identity().classSource().code());
    }

    @Test
    void preservesFeatureOrderAndVariantMetadata() {
        var fighter = classById("class:phb:fighter");
        assertEquals("Fighting Style", fighter.features().get(0).feature().identity().name());
        assertEquals("Second Wind", fighter.features().get(1).feature().identity().name());
        assertEquals(2, fighter.features().get(2).level());
        for (int index = 0; index < fighter.features().size(); index++) {
            assertEquals(index, fighter.features().get(index).order());
        }
        assertEquals(29, result.classFeatures().stream().filter(value -> Boolean.TRUE.equals(value.variant())).count());
        assertTrue(result.subclassFeatures().stream().anyMatch(value -> Boolean.TRUE.equals(value.variant())));
    }

    @Test
    void normalizesRequiredFeatureChoicesAndReferencePreservationFlags() throws Exception {
        var fixture = ClassTestSources.read("""
                {"class":[{"name":"Wizard","source":"PHB","classFeatures":["A|Wizard||1"]}],
                 "classFeature":[
                   {"name":"A","source":"PHB","className":"Wizard","classSource":"PHB","level":1,
                    "entries":[{"type":"options","count":1,"entries":[
                      {"type":"refClassFeature","classFeature":"B|Wizard||1"},
                      {"type":"refOptionalfeature","optionalfeature":"Dueling","data":{"isRequiredOption":true},
                       "preserve":{"consumes":true,"prerequisite":false}}
                    ]}]},
                   {"name":"B","source":"PHB","className":"Wizard","classSource":"PHB","level":1,"entries":["text"]}
                 ]}
                """);
        var normalized = normalizer.normalize(fixture);
        var feature = normalized.classFeatures().stream().filter(value -> value.name().equals("A")).findFirst().orElseThrow();
        var choice = assertInstanceOf(ClassFeatureChoice.class, feature.content().getFirst());
        assertEquals(1, choice.count());
        var target = assertInstanceOf(ClassFeatureReferenceNode.class, choice.options().getFirst());
        assertEquals("class-feature:phb:phb:wizard:1:b", target.reference().targetId());
        var optional = assertInstanceOf(ClassExternalFeatureNode.class, choice.options().getLast());
        assertEquals(ClassReferenceStatus.RESOLVED, optional.reference().status());
        assertEquals(true, optional.required());
        assertEquals(true, optional.preserveConsumes());
        assertEquals(false, optional.preservePrerequisite());
    }

    @Test
    void confirmsWhyNameSourceAloneCannotIdentifyTheseDefinitions() {
        var groups = corpus.records().stream().map(record -> ClassIdentities.identity(record.data()))
                .collect(Collectors.groupingBy(identity -> identity.kind() + ":" + identity.name().toLowerCase(java.util.Locale.ROOT)
                        + "|" + identity.source().code().toLowerCase(java.util.Locale.ROOT), Collectors.counting()));
        assertEquals(296, groups.values().stream().filter(count -> count > 1).count());
        assertEquals(2536, allIds().size());
    }

    @Test
    void bindsReliableOptionalFeatureFeatItemAndMonsterTargetsToExistingIds() {
        for (ClassExternalKind kind : List.of(ClassExternalKind.OPTIONAL_FEATURE, ClassExternalKind.FEAT, ClassExternalKind.MONSTER)) {
            var references = result.externalReferences().stream().filter(reference -> reference.kind() == kind).toList();
            assertFalse(references.isEmpty());
            assertTrue(references.stream().allMatch(reference -> reference.status() == ClassReferenceStatus.RESOLVED
                    && reference.targetId() != null));
        }
        assertTrue(result.externalReferences().stream().filter(reference -> reference.kind() == ClassExternalKind.ITEM)
                .allMatch(reference -> reference.status() == ClassReferenceStatus.RESOLVED));
    }

    @Test
    void validatesSourcesAndRetainsAuthoredMetadataInProvenance() {
        assertEquals(26, result.validation().allSourceIdentities());
        assertEquals(18, result.validation().primarySourceIdentities());
        assertNotNull(registry.find("UATheMysticClass"));
        assertNull(registry.find("UATheMysticClass").publication());
        assertTrue(result.subclasses().stream().allMatch(value -> value.provenance().authored() != null
                && value.provenance().effective() != null));
        assertTrue(result.classFeatures().stream().anyMatch(value -> !value.sources().otherSources().isEmpty()));
        assertTrue(classById("class:phb:fighter").sources().reprints().stream()
                .anyMatch(reference -> reference.targetId().equals("class:xphb:fighter")));
    }

    @Test
    void retainsDeferredItemGroupsAndUnqualifiedSkillsWithoutInventingIds() {
        var groups = result.externalReferences().stream()
                .filter(reference -> reference.kind() == ClassExternalKind.ITEM_GROUP).toList();
        assertEquals(3, groups.size());
        assertTrue(groups.stream().allMatch(reference -> reference.status() == ClassReferenceStatus.DEFERRED_ITEM_GROUP
                && reference.targetId() == null && reference.source().code().equals("xphb")));
        assertEquals(Set.of("holy symbol", "druidic focus"), groups.stream()
                .map(ClassExternalReference::name).collect(Collectors.toSet()));
        assertEquals(256, result.externalReferences().stream().filter(reference -> reference.kind() == ClassExternalKind.SKILL).count());
        assertTrue(result.externalReferences().stream().filter(reference -> reference.kind() == ClassExternalKind.SKILL)
                .allMatch(reference -> reference.source() == null && reference.targetId() == null));
    }

    @Test
    void preservesDirectSpellsAndTheThreeExplicitChoiceCandidates() {
        assertEquals(1068, result.validation().authoredDirectSpellReferences());
        assertEquals(3, result.validation().authoredSpellChoiceCandidates());
        assertTrue(result.externalReferences().stream().filter(reference -> reference.kind() == ClassExternalKind.SPELL)
                .allMatch(reference -> reference.status() == ClassReferenceStatus.DEFERRED_SPELL && reference.targetId() == null));
        assertTrue(result.externalReferences().stream().anyMatch(reference -> reference.kind() == ClassExternalKind.SPELL
                && reference.source() == null));
    }

    @Test
    void fullResultAndNestedSourceAndMechanicsCollectionsAreImmutable() {
        var fighter = classById("class:phb:fighter");
        assertThrows(UnsupportedOperationException.class, result.classes()::clear);
        assertThrows(UnsupportedOperationException.class, result.metadata()::clear);
        assertThrows(UnsupportedOperationException.class, result.metadata().getFirst().internalCopyKinds()::clear);
        assertThrows(UnsupportedOperationException.class, fighter.features()::clear);
        assertThrows(UnsupportedOperationException.class, fighter.startingOptions().equipment().getFirst().branches()::clear);
        assertThrows(UnsupportedOperationException.class, result.classFeatures().getFirst().content()::clear);
        var wizard = (com.thedndpub.data.dte.classes.ClassDte) classById("class:phb:wizard").provenance().effective();
        assertThrows(UnsupportedOperationException.class, wizard.spellsKnownProgressionFixed()::clear);
        var warlock = (com.thedndpub.data.dte.classes.ClassDte) classById("class:phb:warlock").provenance().effective();
        assertThrows(UnsupportedOperationException.class, warlock.spellsKnownProgressionFixedByLevel().get(11)::clear);
    }

    @Test
    void normalizationIsDeterministicWithoutRetainingMutableState() {
        var documents = new ArrayList<>(corpus.documents());
        Collections.reverse(documents);
        assertEquals(result, normalizer.normalize(new ClassSourceCorpus(documents)));
        assertEquals(result, normalizer.normalize(corpus));
    }

    @Test
    void rejectsUnknownPrimarySecondaryAndMarkupSourcesAndUnresolvedPlaceholders() throws Exception {
        for (String json : List.of(
                "{\"class\":[{\"name\":\"X\",\"source\":\"NotKnown\"}]}",
                "{\"class\":[{\"name\":\"X\",\"source\":\"PHB\",\"otherSources\":[{\"source\":\"NotKnown\"}]}]}",
                """
                {"classFeature":[{"name":"F","source":"PHB","className":"X","classSource":"PHB","level":1,
                  "entries":["{@spell test|NotKnown}"]}]}
                """,
                "{\"class\":[{\"name\":\"{{unresolved}}\",\"source\":\"PHB\"}]}",
                "{\"class\":[{\"name\":\"<$unresolved$>\",\"source\":\"PHB\"}]}")) {
            var fixture = ClassTestSources.read(json);
            assertThrows(IllegalStateException.class, () -> normalizer.normalize(fixture));
        }
    }

    @Test
    void preservesHitDiceSavingThrowsAndMissingSidekickStartingOptions() {
        var fighter = classById("class:phb:fighter");
        assertEquals(new ClassHitDice(1, 10), fighter.hitDice());
        assertEquals(List.of("str", "con"), fighter.savingThrows());
        var sidekick = classById("class:tce:warrior-sidekick");
        assertNull(sidekick.hitDice());
        assertNull(sidekick.startingOptions());
        assertTrue(sidekick.savingThrows().isEmpty());
    }

    @Test
    void preservesSkillCountsArmorWeaponsAndToolPackageAlternatives() {
        var fighter = classById("class:phb:fighter").startingOptions().proficiencies();
        assertEquals(2, fighter.skills().getFirst().count());
        assertTrue(fighter.weapons().stream().anyMatch(grant -> grant.proficiency().equals("martial")));
        assertEquals(true, fighter.armorPackages().getFirst().heavy());
        var monk = classById("class:phb:monk").startingOptions().proficiencies();
        assertEquals(2, monk.toolPackages().size());
        assertEquals(1, monk.toolPackages().getFirst().anyArtisansTool());
        assertEquals(1, monk.toolPackages().getLast().anyMusicalInstrument());
        var artificer = classById("class:tce:artificer").startingOptions().proficiencies();
        assertTrue(artificer.weapons().stream().anyMatch(grant -> grant.proficiency().equals("firearms")
                && Boolean.TRUE.equals(grant.optional())));
    }

    @Test
    void preservesSourceWeaponFiltersWithoutInterpretingTheirCandidateItems() {
        var packages = classById("class:xphb:rogue").startingOptions().proficiencies().weaponPackages();
        assertEquals(true, packages.getFirst().simple());
        assertEquals("type=martial weapon|property=light;finesse", packages.getFirst().allMatchingFilter());
    }

    @Test
    void normalizesTheUnusualFighterOrRequirementsAndPaladinConjunctionSeparately() {
        var fighter = classById("class:phb:fighter").multiclassing().requirements();
        assertEquals(ClassRequirementKind.ALL_OF, fighter.kind());
        assertTrue(fighter.thresholds().isEmpty());
        assertEquals(ClassRequirementKind.ANY_OF, fighter.groups().getFirst().kind());
        assertEquals(Set.of("str", "dex"), fighter.groups().getFirst().thresholds().stream()
                .map(ClassAbilityThreshold::ability).collect(Collectors.toSet()));
        var paladin = classById("class:phb:paladin").multiclassing().requirements();
        assertEquals(2, paladin.thresholds().size());
        assertTrue(paladin.groups().isEmpty());
        assertNull(classById("class:xphb:paladin").multiclassing().requirements());
    }

    @Test
    void preservesIndependentEquipmentGroupsAlternativesAndQuantities() {
        var equipment = classById("class:phb:fighter").startingOptions().equipment();
        assertEquals(4, equipment.size());
        assertEquals(List.of("a", "b"), equipment.getFirst().branches().stream().map(ClassEquipmentBranch::key).toList());
        assertTrue(equipment.stream().flatMap(group -> group.branches().stream()).flatMap(branch -> branch.entries().stream())
                .anyMatch(entry -> entry.quantity() != null && entry.quantity() == 2));
        var artificer = classById("class:tce:artificer").startingOptions();
        assertEquals(true, artificer.additionalFromBackground());
        assertTrue(artificer.equipment().getFirst().unconditional());
    }

    @Test
    void preservesFixedCopperPackagesMultipleEquipmentCategoriesAndUnboundSpellbook() {
        var artificer = classById("class:efa:artificer").startingOptions().equipment().getFirst();
        assertEquals(List.of("A", "B"), artificer.branches().stream().map(ClassEquipmentBranch::key).toList());
        assertEquals(1600, artificer.branches().getFirst().entries().stream()
                .filter(entry -> entry.kind() == ClassEquipmentKind.CURRENCY).findFirst().orElseThrow().valueCopper());
        assertEquals(15000, artificer.branches().getLast().entries().getFirst().valueCopper());
        var entries = result.classes().stream().filter(value -> value.startingOptions() != null)
                .flatMap(value -> value.startingOptions().equipment().stream()).flatMap(group -> group.branches().stream())
                .flatMap(branch -> branch.entries().stream()).toList();
        assertTrue(entries.stream().anyMatch(entry -> entry.kind() == ClassEquipmentKind.CATEGORY_ALTERNATIVES
                && entry.categories().size() == 2));
        var spellbook = entries.stream().filter(entry -> entry.kind() == ClassEquipmentKind.SPECIAL)
                .findFirst().orElseThrow();
        assertEquals("Spellbook", spellbook.special());
        assertNull(spellbook.reference());
    }

    @Test
    void preservesDenseSparseAndRequiredChoiceProgressionsWithoutAddingMilestones() {
        var infusions = classById("class:tce:artificer").choices().getFirst();
        assertEquals(20, infusions.milestones().size());
        assertEquals(new ClassLevelCount(1, 0), infusions.milestones().getFirst());
        var style = classById("class:phb:fighter").choices().getFirst();
        assertEquals(List.of(new ClassLevelCount(1, 1)), style.milestones());
        assertEquals(ClassExternalKind.OPTIONAL_FEATURE, style.targetKind());
        assertTrue(result.subclasses().stream().flatMap(value -> value.choices().stream())
                .flatMap(choice -> choice.required().stream()).anyMatch(required -> required.level() == 3
                        && required.reference().status() == ClassReferenceStatus.RESOLVED));
        assertTrue(classById("class:xphb:fighter").choices().stream()
                .anyMatch(choice -> choice.targetKind() == ClassExternalKind.FEAT));
    }

    @Test
    void retainsNumbersTextDiceBonusesAndSpeedCellsAsDistinctTableVariants() {
        var barbarian = classById("class:phb:barbarian").tables().getFirst();
        assertEquals(ClassTableCellKind.PRESENTATION_TEXT, barbarian.rows().getFirst().cells().getFirst().kind());
        assertEquals("2", barbarian.rows().getFirst().cells().getFirst().text());
        assertNull(barbarian.rows().getFirst().cells().getFirst().number());
        assertEquals("Unlimited", barbarian.rows().getLast().cells().getFirst().text());
        assertEquals(ClassTableCellKind.BONUS, barbarian.rows().getFirst().cells().get(1).kind());
        var classicMonk = classById("class:phb:monk").tables().getFirst().rows().getFirst();
        var revisedMonk = classById("class:xphb:monk").tables().getFirst().rows().getFirst();
        assertEquals(4, classicMonk.cells().getFirst().dice().getFirst().faces());
        assertEquals(6, revisedMonk.cells().getFirst().dice().getFirst().faces());
        assertEquals(ClassTableCellKind.SPEED_BONUS, classicMonk.cells().getLast().kind());
        assertEquals(0, classicMonk.cells().getLast().number());
    }

    @Test
    void distinguishesExplicitSlotMatricesFromPactPresentationAndRetainsZeroSlots() {
        var classic = classById("class:phb:paladin").spellcasting().slotTables().getFirst();
        var revised = classById("class:xphb:paladin").spellcasting().slotTables().getFirst();
        assertEquals(0, classic.rows().getFirst().cells().getFirst().number());
        assertEquals(2, revised.rows().getFirst().cells().getFirst().number());
        var warlock = classById("class:phb:warlock").spellcasting();
        assertTrue(warlock.slotTables().isEmpty());
        assertFalse(warlock.pactTables().isEmpty());
        assertEquals("pact", warlock.casterProgression());
        assertEquals(20, classic.rows().size());
    }

    private ClassDefinition classById(String id) {
        return result.classes().stream().filter(value -> value.id().value().equals(id)).findFirst().orElseThrow();
    }

    private Set<String> allIds() {
        return Stream.of(result.classes().stream().map(value -> value.id().value()),
                        result.subclasses().stream().map(value -> value.id().value()),
                        result.classFeatures().stream().map(value -> value.id().value()),
                        result.subclassFeatures().stream().map(value -> value.id().value()))
                .flatMap(value -> value).collect(Collectors.toCollection(HashSet::new));
    }
}
