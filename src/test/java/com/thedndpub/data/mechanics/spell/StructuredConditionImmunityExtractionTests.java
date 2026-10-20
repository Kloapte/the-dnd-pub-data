package com.thedndpub.data.mechanics.spell;

import com.thedndpub.data.dto.spell.SpellId;
import com.thedndpub.data.mechanics.model.AppliesForDuration;
import com.thedndpub.data.mechanics.model.ConditionChangeMechanic;
import com.thedndpub.data.mechanics.model.ConditionName;
import com.thedndpub.data.mechanics.model.ConditionOperation;
import com.thedndpub.data.mechanics.model.DeclaredSpellDurationReference;
import com.thedndpub.data.mechanics.model.MechanicEvidenceAssociation;
import com.thedndpub.data.mechanics.model.MechanicRecord;
import com.thedndpub.data.mechanics.model.MechanicsDocument;
import com.thedndpub.data.mechanics.model.MechanicsEvidence;
import com.thedndpub.data.mechanics.model.MechanicsHashes;
import com.thedndpub.data.mechanics.model.MechanicsLicensingStatus;
import com.thedndpub.data.mechanics.model.ProcessingState;
import com.thedndpub.data.mechanics.model.RequiresConcentration;
import com.thedndpub.data.mechanics.model.SpellTargetSubject;
import com.thedndpub.data.mechanics.model.StructuredFieldMechanicsEvidence;
import com.thedndpub.data.mechanics.model.TextMechanicsEvidence;
import com.thedndpub.data.mechanics.model.TargetScope;
import com.thedndpub.data.mechanics.model.UnsupportedReason;
import com.thedndpub.data.mechanics.review.MechanicsReviewStatus;
import com.thedndpub.data.services.SpellMechanicsInspectionCatalog;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StructuredConditionImmunityExtractionTests {
    @Autowired
    private SpellMechanicsInspectionCatalog inspection;

    @Test
    void extractsMultipleStructuredImmunitiesWithSourceBoundEvidence() {
        MechanicsDocument document = document("spell:au:iron-body");
        List<MechanicRecord> immunities = immunities(document);

        assertEquals(Set.of(ConditionName.PARALYZED, ConditionName.PETRIFIED, ConditionName.POISONED),
                immunities.stream().map(record -> condition(record).condition()).collect(Collectors.toSet()));
        assertConditionClaimsAreBound(document, immunities, "spell:au:iron-body", true);
        assertTrue(immunities.stream().allMatch(record -> condition(record).qualifiers().stream()
                .anyMatch(value -> value.toLowerCase().contains("willing creature you touch"))));
        assertTrue(document.unsupported().stream().anyMatch(value -> value.id()
                .equals("condition-immunity-existing-condition-removal")
                && value.reason() == UnsupportedReason.FAMILY_NOT_IMPLEMENTED));
        assertTrue(document.sources().stream().anyMatch(source -> source.resourcePath()
                .equals("data/spells/spells-au.json")
                && source.resourceSha256().equals("805a3ccb583e8415ee494e582c6172d900ebd63f85d599b785f260591992ead7")));
    }

    @Test
    void extractsSingleConditionImmunityWithoutTurningItIntoRemoval() {
        MechanicsDocument document = document("spell:xphb:heroism");
        List<MechanicRecord> immunities = immunities(document);
        MechanicsDocument restoration = document("spell:phb:lesser-restoration");

        assertEquals(1, immunities.size());
        assertEquals(ConditionName.FRIGHTENED, condition(immunities.getFirst()).condition());
        assertConditionClaimsAreBound(document, immunities, "spell:xphb:heroism", true);
        assertTrue(restoration.interpretations().stream().map(MechanicRecord::payload)
                .filter(ConditionChangeMechanic.class::isInstance).map(ConditionChangeMechanic.class::cast)
                .allMatch(value -> value.operation() == ConditionOperation.REMOVE));
        assertTrue(document.interpretations().stream().map(MechanicRecord::payload)
                .filter(ConditionChangeMechanic.class::isInstance).map(ConditionChangeMechanic.class::cast)
                .allMatch(value -> value.operation() == ConditionOperation.GRANT_IMMUNITY));
    }

    @Test
    void bindsTargetDurationAndConcentrationForConditionImmunity() {
        MechanicsDocument gaseousForm = document("spell:xphb:gaseous-form");
        MechanicsDocument mindBlank = document("spell:xphb:mind-blank");

        assertConditionClaimsAreBound(gaseousForm, immunities(gaseousForm), "spell:xphb:gaseous-form", true);
        assertConditionClaimsAreBound(mindBlank, immunities(mindBlank), "spell:xphb:mind-blank", false);
        assertTrue(condition(immunities(gaseousForm).getFirst()).qualifiers().stream()
                .anyMatch(value -> value.equalsIgnoreCase("while in this form")));
    }

    @Test
    void abstainsWhenStructuredTagsOverstatePreventionOrBeneficiaryScope() {
        MechanicsDocument freedom = document("spell:xphb:freedom-of-movement");
        MechanicsDocument feast = document("spell:xphb:heroes-feast");
        MechanicsDocument planarChoice = document("spell:tce:tasha-s-otherworldly-guise");

        assertAmbiguousConditionImmunity(freedom, "condition-immunity-source-restriction");
        assertAmbiguousConditionImmunity(feast, "condition-immunity-beneficiary-scope");
        assertAmbiguousConditionImmunity(planarChoice, "condition-immunity-planar-choice");
    }

    @Test
    void retainsPendingReviewAndLicensingAndExactEvidenceAssociations() {
        MechanicsDocument document = document("spell:xphb:feign-death");
        MechanicRecord record = immunities(document).getFirst();

        assertEquals(1, record.revision().number());
        assertNull(record.review().humanDecision());
        assertEquals(MechanicsReviewStatus.PENDING_REVIEW, record.review().effectiveStatus());
        assertEquals(MechanicsLicensingStatus.NEEDS_REVIEW, record.licensing().status());
        for (MechanicEvidenceAssociation association : record.evidenceAssociations()) {
            MechanicsEvidence evidence = document.evidence().stream()
                    .filter(value -> value.id().equals(association.evidenceId()))
                    .findFirst()
                    .orElseThrow();
            assertEquals(association.evidenceSha256(), MechanicsHashes.evidenceHash(evidence));
        }
        assertTrue(record.evidenceAssociations().stream().anyMatch(association -> evidenceAt(
                document, association.evidenceId()) instanceof StructuredFieldMechanicsEvidence structured
                && structured.jsonPointer().endsWith("/conditionImmune/0")));
        assertTrue(record.evidenceAssociations().stream().anyMatch(association -> evidenceAt(
                document, association.evidenceId()) instanceof TextMechanicsEvidence));
        assertEquals(ProcessingState.PROCESSED, document.processing().state());
    }

    private void assertConditionClaimsAreBound(MechanicsDocument document, List<MechanicRecord> records,
                                               String spellId, boolean concentrationExpected) {
        assertFalse(records.isEmpty());
        for (MechanicRecord record : records) {
            assertEquals(ConditionOperation.GRANT_IMMUNITY, condition(record).operation());
            assertEquals(new SpellTargetSubject(TargetScope.SINGLE_TARGET_CREATURE), record.context().subject());
            AppliesForDuration duration = record.context().bindings().stream()
                    .filter(AppliesForDuration.class::isInstance)
                    .map(AppliesForDuration.class::cast)
                    .findFirst()
                    .orElseThrow();
            DeclaredSpellDurationReference reference = assertInstanceOf(
                    DeclaredSpellDurationReference.class, duration.duration());
            assertEquals(spellId, reference.spellId().value());
            assertEquals(document.sources().getFirst(), reference.source());
            assertEquals(concentrationExpected, record.context().bindings().stream()
                    .anyMatch(RequiresConcentration.class::isInstance));
            assertTrue(condition(record).qualifiers().stream()
                    .anyMatch(value -> value.toLowerCase().contains("willing creature you touch")));
        }
    }

    private void assertAmbiguousConditionImmunity(MechanicsDocument document, String diagnosticId) {
        assertTrue(immunities(document).isEmpty());
        assertTrue(document.unsupported().stream().anyMatch(value -> value.id().equals(diagnosticId)
                && value.reason() == UnsupportedReason.AMBIGUOUS_INTERPRETATION));
    }

    private List<MechanicRecord> immunities(MechanicsDocument document) {
        return document.interpretations().stream()
                .filter(record -> record.payload() instanceof ConditionChangeMechanic change
                        && change.operation() == ConditionOperation.GRANT_IMMUNITY)
                .toList();
    }

    private ConditionChangeMechanic condition(MechanicRecord record) {
        return (ConditionChangeMechanic) record.payload();
    }

    private MechanicsEvidence evidenceAt(MechanicsDocument document, String evidenceId) {
        return document.evidence().stream().filter(value -> value.id().equals(evidenceId))
                .findFirst().orElseThrow();
    }

    private MechanicsDocument document(String spellId) {
        return inspection.documents().stream()
                .filter(value -> value.owner().id().equals(spellId))
                .findFirst()
                .orElseThrow();
    }
}
