package com.thedndpub.data.mechanics.spell;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thedndpub.data.dto.spell.SpellDefinition;
import com.thedndpub.data.mechanics.model.MechanicsDocument;
import com.thedndpub.data.services.SpellCatalog;
import com.thedndpub.data.services.SpellFormulaAnnotationCatalog;
import com.thedndpub.data.services.SpellMechanicsInspectionCatalog;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.DefaultResourceLoader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SpellCapabilityInventoryTests {
    private static SpellCapabilityInventory inventory;
    private static List<SpellDefinition> definitions;
    private static List<SpellMechanicsSourceOwner> owners;
    private static List<MechanicsDocument> currentDocuments;
    private static List<MechanicsDocument> unattemptedDocuments;
    private static SpellCapabilityInventory baseline;

    @Autowired private ObjectMapper mapper;
    @Autowired private SpellCatalog spells;
    @Autowired private SpellFormulaAnnotationCatalog formulas;
    @Autowired private SpellMechanicsInspectionCatalog mechanics;

    @BeforeAll
    void initialize() throws Exception {
        definitions = spells.findAll();
        currentDocuments = mechanics.documents();
        var factory = new SpellMechanicsDocumentFactory(mapper);
        owners = factory.loadOwners();
        unattemptedDocuments = factory.unattempted(definitions, formulas.index(), owners);
        inventory = new SpellCapabilityInventoryRunner(mapper).assess(
                definitions, currentDocuments, unattemptedDocuments, owners);
        baseline = mapper.readValue(Path.of("analysis/mechanics/step-7/"
                + "7.4.4B-spell-capability-inventory-baseline.json").toFile(), SpellCapabilityInventory.class);
    }

    @Test
    void inventoriesEverySourceAwareIdentityAndReconcilesCounts() {
        assertEquals(969, inventory.catalogTotal());
        assertEquals(969, inventory.spells().size());
        assertEquals(969, inventory.spells().stream().map(SpellCapabilityRecord::spellId).distinct().count());
        assertEquals(definitions.stream().map(definition -> definition.id().value()).collect(Collectors.toSet()),
                inventory.spells().stream().map(SpellCapabilityRecord::spellId).collect(Collectors.toSet()));
        assertEquals(969, inventory.classificationCounts().values().stream().mapToInt(Integer::intValue).sum());
        assertEquals(Map.of("NOT_ASSESSED", 366, "PARTIALLY_SUPPORTED", 174, "SUPPORTED", 0,
                "UNSUPPORTED", 429), inventory.classificationCounts());
        assertEquals(129, inventory.currentlyProcessedCount());
        assertEquals(351, inventory.potentialTypedClaimCount());
        assertEquals(279, currentDocuments.stream().mapToInt(document -> document.interpretations().size()).sum());

        var byId = inventory.spells().stream().collect(Collectors.toMap(SpellCapabilityRecord::spellId,
                Function.identity()));
        assertEquals("PHB", byId.get("spell:phb:shield").source());
        assertEquals("XPHB", byId.get("spell:xphb:shield").source());
        assertNotEquals(byId.get("spell:phb:shield"), byId.get("spell:xphb:shield"));
        assertEquals(SpellCapabilityClassification.PARTIALLY_SUPPORTED,
                byId.get("spell:phb:magic-missile").classification());
        assertEquals(List.of("DAMAGE_INSTANCE_SERIES"),
                byId.get("spell:phb:magic-missile").candidateMechanicFamilies());
        SpellCapabilityRecord steelWindStrike = byId.get("spell:xge:steel-wind-strike");
        assertEquals("PROCESSED", steelWindStrike.processingState());
        assertEquals(SpellCapabilityClassification.PARTIALLY_SUPPORTED, steelWindStrike.classification());
        assertEquals(2, steelWindStrike.potentialMechanicCount());
        assertEquals(List.of("ATTACK_ROLL", "DAMAGE"), steelWindStrike.candidateMechanicFamilies());
        assertTrue(steelWindStrike.unsupportedReasons().contains("UNDETERMINED:FAMILY_NOT_IMPLEMENTED"));
        SpellCapabilityRecord acidArrow = byId.get("spell:phb:melf-s-acid-arrow");
        assertEquals("PROCESSED", acidArrow.processingState());
        assertEquals(SpellCapabilityClassification.PARTIALLY_SUPPORTED, acidArrow.classification());
        assertEquals(4, acidArrow.potentialMechanicCount());
        assertEquals(List.of("ATTACK_ROLL", "DAMAGE"), acidArrow.candidateMechanicFamilies());
        SpellCapabilityRecord spiritualWeapon = byId.get("spell:phb:spiritual-weapon");
        assertEquals(SpellCapabilityClassification.PARTIALLY_SUPPORTED, spiritualWeapon.classification());
        assertEquals(3, spiritualWeapon.potentialMechanicCount());
        assertEquals(List.of("ATTACK_ROLL", "DAMAGE", "DURATION"),
                spiritualWeapon.candidateMechanicFamilies());
        assertEquals(Map.of("COMPLETE_WITHIN_CLAIM", 2, "PARTIAL", 1),
                spiritualWeapon.claimCompletenessCounts());
        SpellCapabilityRecord iceKnife = byId.get("spell:xge:ice-knife");
        assertEquals("PROCESSED", iceKnife.processingState());
        assertEquals(SpellCapabilityClassification.PARTIALLY_SUPPORTED, iceKnife.classification());
        assertEquals(5, iceKnife.potentialMechanicCount());
        assertEquals(List.of("AREA_EFFECT", "ATTACK_ROLL", "DAMAGE", "SAVING_THROW_REQUIREMENT"),
                iceKnife.candidateMechanicFamilies());
        SpellCapabilityRecord magicStone = byId.get("spell:xge:magic-stone");
        assertEquals(SpellCapabilityClassification.PARTIALLY_SUPPORTED, magicStone.classification());
        assertEquals(3, magicStone.potentialMechanicCount());
        assertEquals(List.of("ATTACK_ROLL", "DAMAGE", "DURATION"), magicStone.candidateMechanicFamilies());
        assertTrue(magicStone.unsupportedReasons().contains("ATTACK_ROLL:SOURCE_UNSPECIFIED"));
        SpellCapabilityRecord witchBolt = byId.get("spell:phb:witch-bolt");
        assertEquals("PROCESSED", witchBolt.processingState());
        assertEquals(SpellCapabilityClassification.PARTIALLY_SUPPORTED, witchBolt.classification());
        assertEquals(4, witchBolt.potentialMechanicCount());
        assertEquals(List.of("ATTACK_ROLL", "DAMAGE", "DURATION"), witchBolt.candidateMechanicFamilies());
        assertTrue(witchBolt.unsupportedReasons().contains("ATTACK_ROLL:FAMILY_NOT_IMPLEMENTED"));
        assertEquals(inventory.catalogTotal(), inventory.sourceCounts().values().stream().mapToInt(Integer::intValue).sum());
        assertEquals(currentDocuments, mechanics.documents(), "Inventory assessment must not mutate inspection documents");
    }

    @Test
    void knownSpellsAreClassifiedFromActualPatternOutputs() {
        Map<String, SpellCapabilityRecord> byId = inventory.spells().stream()
                .collect(Collectors.toMap(SpellCapabilityRecord::spellId, Function.identity()));
        for (String id : List.of("spell:phb:shield", "spell:phb:fireball", "spell:phb:hold-person",
                "spell:phb:blindness-deafness", "spell:phb:bless", "spell:phb:mage-armor",
                "spell:phb:protection-from-energy")) {
            assertTrue(byId.containsKey(id), "Missing representative " + id);
            assertTrue(byId.get(id).patternMatched(), "Expected existing pattern match for " + id);
        }
        SpellCapabilityRecord mageArmor = byId.get("spell:phb:mage-armor");
        assertEquals(1, mageArmor.potentialMechanicCount());
        assertFalse(mageArmor.unassessedSourceContent().isEmpty());
        assertNotEquals(SpellCapabilityClassification.SUPPORTED, mageArmor.classification());

        assertEquals(5, byId.get("spell:phb:lesser-restoration").potentialMechanicCount());
        assertEquals(3, byId.get("spell:phb:greater-restoration").potentialMechanicCount());
        assertEquals(4, byId.get("spell:phb:power-word-heal").potentialMechanicCount());
        SpellCapabilityRecord fireBolt = byId.get("spell:phb:fire-bolt");
        assertEquals("PROCESSED", fireBolt.processingState());
        assertEquals(SpellCapabilityClassification.PARTIALLY_SUPPORTED, fireBolt.classification());
        assertEquals(2, fireBolt.potentialMechanicCount());
        assertEquals(List.of("ATTACK_ROLL", "DAMAGE"), fireBolt.candidateMechanicFamilies());
        assertTrue(fireBolt.unsupportedReasons().contains("UNDETERMINED:FAMILY_NOT_IMPLEMENTED"));
        SpellCapabilityRecord eldritchBlast = byId.get("spell:phb:eldritch-blast");
        assertEquals("PROCESSED", eldritchBlast.processingState());
        assertEquals(SpellCapabilityClassification.PARTIALLY_SUPPORTED, eldritchBlast.classification());
        assertEquals(2, eldritchBlast.potentialMechanicCount());
        assertEquals(List.of("ATTACK_ROLL", "DAMAGE"), eldritchBlast.candidateMechanicFamilies());
        assertFalse(eldritchBlast.unsupportedReasons().contains("ATTACK_ROLL:FAMILY_NOT_IMPLEMENTED"));
        SpellCapabilityRecord scorchingRay = byId.get("spell:phb:scorching-ray");
        assertEquals("PROCESSED", scorchingRay.processingState());
        assertEquals(SpellCapabilityClassification.PARTIALLY_SUPPORTED, scorchingRay.classification());
        assertEquals(2, scorchingRay.potentialMechanicCount());
        assertEquals(List.of("ATTACK_ROLL", "DAMAGE"), scorchingRay.candidateMechanicFamilies());
        assertTrue(scorchingRay.unsupportedReasons().contains("ATTACK_ROLL:SOURCE_UNSPECIFIED"));
        SpellCapabilityRecord chillTouch = byId.get("spell:phb:chill-touch");
        assertEquals("PROCESSED", chillTouch.processingState());
        assertEquals(SpellCapabilityClassification.PARTIALLY_SUPPORTED, chillTouch.classification());
        assertEquals(4, chillTouch.potentialMechanicCount());
        assertEquals(List.of("ATTACK_ROLL", "ATTACK_ROLL_MODIFIER", "DAMAGE", "HEALING_PREVENTION"),
                chillTouch.candidateMechanicFamilies());
        assertTrue(chillTouch.unsupportedReasons().contains("ATTACK_ROLL:FAMILY_NOT_IMPLEMENTED"));
        SpellCapabilityRecord shockingGrasp = byId.get("spell:phb:shocking-grasp");
        assertEquals("PROCESSED", shockingGrasp.processingState());
        assertEquals(SpellCapabilityClassification.PARTIALLY_SUPPORTED, shockingGrasp.classification());
        assertEquals(4, shockingGrasp.potentialMechanicCount());
        assertEquals(List.of("ATTACK_ROLL", "ATTACK_ROLL_MODIFIER", "DAMAGE", "REACTION_RESTRICTION"),
                shockingGrasp.candidateMechanicFamilies());
        assertTrue(shockingGrasp.unsupportedReasons().contains("ATTACK_ROLL:FAMILY_NOT_IMPLEMENTED"));
        SpellCapabilityRecord negatedCondition = byId.get("spell:phb:animal-shapes");
        assertEquals(0, negatedCondition.potentialMechanicCount());
        assertEquals(SpellCapabilityClassification.UNSUPPORTED, negatedCondition.classification());
        assertTrue(negatedCondition.unsupportedReasons().contains("CONDITION:AMBIGUOUS_INTERPRETATION"));
        assertEquals(0, byId.get("spell:phb:aura-of-purity").potentialMechanicCount(),
                "Condition resistance text must not become unconditional application or immunity");
        assertFalse(byId.get("spell:phb:enthrall").candidateMechanicFamilies().contains("CONDITION"),
                "A condition used as a prerequisite must not become an application claim");
        assertEquals(0, byId.get("spell:phb:protection-from-evil-and-good").potentialMechanicCount());

        Map<String, Integer> immunityClaimCounts = Map.of(
                "spell:au:iron-body", 3,
                "spell:xphb:feign-death", 1,
                "spell:xphb:gaseous-form", 1,
                "spell:xphb:heroism", 1,
                "spell:xphb:mind-blank", 1);
        immunityClaimCounts.forEach((id, count) -> {
            assertEquals(SpellCapabilityClassification.PARTIALLY_SUPPORTED, byId.get(id).classification(), id);
            assertEquals(count, byId.get(id).potentialMechanicCount(), id);
        });
        for (String id : List.of("spell:xphb:freedom-of-movement", "spell:xphb:heroes-feast")) {
            assertEquals(SpellCapabilityClassification.UNSUPPORTED, byId.get(id).classification(), id);
            assertTrue(byId.get(id).unsupportedReasons().stream()
                    .anyMatch(reason -> reason.equals("CONDITION:AMBIGUOUS_INTERPRETATION")), id);
        }
        SpellCapabilityRecord planarChoice = byId.get("spell:tce:tasha-s-otherworldly-guise");
        assertTrue(planarChoice.patternMatched());
        assertEquals(0, planarChoice.potentialMechanicCount());
        assertTrue(planarChoice.unsupportedReasons().contains("CONDITION:AMBIGUOUS_INTERPRETATION"));
    }

    @Test
    void noMatchRemainsUnassessedUnlessStructuredSourceEvidenceIdentifiesAGap() {
        assertTrue(inventory.spells().stream().anyMatch(record -> !record.patternMatched()
                && record.processingState().equals("NOT_ATTEMPTED")
                && record.classification() == SpellCapabilityClassification.NOT_ASSESSED));
        assertTrue(inventory.spells().stream().anyMatch(record -> !record.patternMatched()
                && record.classification() == SpellCapabilityClassification.UNSUPPORTED));
        assertTrue(inventory.spells().stream().filter(record -> !record.patternMatched())
                .allMatch(record -> !record.candidateMechanicFamilies().isEmpty()
                        || record.classification() != SpellCapabilityClassification.SUPPORTED));
    }

    @Test
    void reportSerializationAndMarkdownAreDeterministic(@TempDir Path temp) throws Exception {
        Path json1 = temp.resolve("first.json");
        Path md1 = temp.resolve("first.md");
        Path json2 = temp.resolve("second.json");
        Path md2 = temp.resolve("second.md");
        SpellCapabilityInventoryReports.write(inventory, mapper, json1, md1);
        SpellCapabilityInventoryReports.write(inventory, mapper, json2, md2);
        assertArrayEquals(Files.readAllBytes(json1), Files.readAllBytes(json2));
        assertArrayEquals(Files.readAllBytes(md1), Files.readAllBytes(md2));

        String comparison = SpellConditionInventoryComparisonReports.markdown(baseline, inventory, 190,
                currentDocuments.stream().mapToInt(document -> document.interpretations().size()).sum());
        String repeatedComparison = SpellConditionInventoryComparisonReports.markdown(baseline, inventory, 190,
                currentDocuments.stream().mapToInt(document -> document.interpretations().size()).sum());
        assertEquals(comparison, repeatedComparison);
        Path comparisonFirst = temp.resolve("condition-comparison-first.md");
        Path comparisonSecond = temp.resolve("condition-comparison-second.md");
        Files.writeString(comparisonFirst, comparison);
        Files.writeString(comparisonSecond, repeatedComparison);
        assertArrayEquals(Files.readAllBytes(comparisonFirst), Files.readAllBytes(comparisonSecond));

        SpellCapabilityInventory repeated = new SpellCapabilityInventoryRunner(mapper).assess(
                definitions, currentDocuments, unattemptedDocuments, owners);
        assertEquals(mapper.writeValueAsString(inventory), mapper.writeValueAsString(repeated));
        assertEquals(SpellCapabilityInventoryReports.markdown(inventory),
                SpellCapabilityInventoryReports.markdown(repeated));
    }

    @Test
    @EnabledIfSystemProperty(named = "mechanics.inventory.regenerate", matches = "true")
    void regenerateAuthoritativeReports(@TempDir Path temp) throws Exception {
        Path inventoryJson = temp.resolve("7.4.4A-spell-capability-inventory.json");
        Path inventoryMarkdown = temp.resolve("7.4.4A-spell-capability-inventory.md");
        SpellCapabilityInventoryReports.write(inventory, mapper, inventoryJson, inventoryMarkdown);

        String comparison = SpellConditionInventoryComparisonReports.markdown(baseline, inventory, 190,
                currentDocuments.stream().mapToInt(document -> document.interpretations().size()).sum());
        Path comparisonMarkdown = temp.resolve("7.4.4C-condition-extraction-comparison.md");
        Files.writeString(comparisonMarkdown, comparison);

        Path reportRoot = Path.of(System.getProperty("mechanics.inventory.reportDirectory",
                "analysis/mechanics/step-7"));
        Files.createDirectories(reportRoot);
        Files.copy(inventoryJson, reportRoot.resolve(inventoryJson.getFileName()),
                java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        Files.copy(inventoryMarkdown, reportRoot.resolve(inventoryMarkdown.getFileName()),
                java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        Files.copy(comparisonMarkdown, reportRoot.resolve(comparisonMarkdown.getFileName()),
                java.nio.file.StandardCopyOption.REPLACE_EXISTING);
    }

    @Test
    void aSingleOwnerFailureDoesNotStopTheRemainingCorpus() {
        SpellMechanicsPattern broken = new SpellMechanicsPattern() {
            @Override public String patternId() { return "inventory-failure-test"; }
            @Override public boolean supports(SpellDefinition spell) {
                return spell.id().value().equals("spell:phb:magic-missile");
            }
            @Override public MechanicsDocument extract(SpellDefinition spell, SpellMechanicsEvidenceSource source,
                                                        MechanicsDocument original) {
                throw new IllegalStateException("controlled inventory test failure");
            }
        };
        SpellCapabilityInventory withFailure = new SpellCapabilityInventoryRunner(mapper,
                new DefaultResourceLoader(), List.of(new ShieldMechanicsExtractor(),
                new PhbSpellMechanicsExtractor(), broken)).assess(
                definitions, currentDocuments, unattemptedDocuments, owners);
        assertEquals(969, withFailure.spells().size());
        SpellCapabilityRecord failed = withFailure.spells().stream()
                .filter(record -> record.spellId().equals("spell:phb:magic-missile")).findFirst().orElseThrow();
        assertEquals(List.of("EXTRACTION_FAILURE"), failed.diagnosticCodes());
        assertEquals(SpellCapabilityClassification.NOT_ASSESSED, failed.classification());
        assertTrue(withFailure.spells().stream().anyMatch(record -> record.spellId().equals("spell:phb:fireball")
                && record.potentialMechanicCount() > 0));
    }
}
