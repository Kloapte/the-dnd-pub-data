package com.thedndpub.data.mechanics.spell;

import com.thedndpub.data.dto.spell.SpellDefinition;
import com.thedndpub.data.mechanics.model.AppliesOnAttackRollOutcome;
import com.thedndpub.data.mechanics.model.AmountSymbol;
import com.thedndpub.data.mechanics.model.AttackActivationPattern;
import com.thedndpub.data.mechanics.model.AttackActivationTiming;
import com.thedndpub.data.mechanics.model.AttackActorRole;
import com.thedndpub.data.mechanics.model.AttackKind;
import com.thedndpub.data.mechanics.model.AttackRollMechanic;
import com.thedndpub.data.mechanics.model.AttackRollOutcome;
import com.thedndpub.data.mechanics.model.CoverageStatus;
import com.thedndpub.data.mechanics.model.CoverageScopeExtent;
import com.thedndpub.data.mechanics.model.DamageMechanic;
import com.thedndpub.data.mechanics.model.DeclaredSpellDurationReference;
import com.thedndpub.data.mechanics.model.DerivationMethod;
import com.thedndpub.data.mechanics.model.DiceAmount;
import com.thedndpub.data.mechanics.model.EvidenceRole;
import com.thedndpub.data.mechanics.model.MechanicContext;
import com.thedndpub.data.mechanics.model.MechanicEvidenceAssociation;
import com.thedndpub.data.mechanics.model.MechanicFamily;
import com.thedndpub.data.mechanics.model.MechanicId;
import com.thedndpub.data.mechanics.model.MechanicRecord;
import com.thedndpub.data.mechanics.model.MechanicsAssessment;
import com.thedndpub.data.mechanics.model.MechanicsCompleteness;
import com.thedndpub.data.mechanics.model.MechanicsCoverage;
import com.thedndpub.data.mechanics.model.MechanicsDocument;
import com.thedndpub.data.mechanics.model.MechanicsProcessing;
import com.thedndpub.data.mechanics.model.MechanicsSupport;
import com.thedndpub.data.mechanics.model.PersistentSpellEffectMechanic;
import com.thedndpub.data.mechanics.model.ProcessingState;
import com.thedndpub.data.mechanics.model.SourceArtifactRevision;
import com.thedndpub.data.mechanics.model.SpellSlotDamageScaling;
import com.thedndpub.data.mechanics.model.SpellTargetSubject;
import com.thedndpub.data.mechanics.model.SumAmount;
import com.thedndpub.data.mechanics.model.SymbolicAmount;
import com.thedndpub.data.mechanics.model.TargetScope;
import com.thedndpub.data.mechanics.model.UnsupportedMechanicsConstruct;
import com.thedndpub.data.mechanics.model.UnsupportedReason;
import com.thedndpub.data.mechanics.model.TextMechanicsEvidence;
import com.thedndpub.data.mechanics.model.MechanicDerivation;
import com.thedndpub.data.mechanics.model.MechanicRevisionReference;
import com.thedndpub.data.mechanics.model.MechanicsEvidence;
import com.thedndpub.data.mechanics.model.FixedDamageType;
import com.thedndpub.data.mechanics.model.StructuredFieldMechanicsEvidence;

import java.util.ArrayList;
import java.util.List;

/** PHB-only, source-bounded Spiritual Weapon interpretation. */
final class PhbSpiritualWeaponRules implements SpellMechanicsPattern {
    static final String OWNER_ID = "spell:phb:spiritual-weapon";
    private static final String BODY = "/entries/0";
    private static final String REPEAT = "/entries/1";
    private static final String HIGHER = "/entriesHigherLevel/0/entries/0";

    @Override
    public String patternId() {
        return "phb-spiritual-weapon-persistent-attack-v1";
    }

    @Override
    public boolean supports(SpellDefinition spell) {
        return OWNER_ID.equals(spell.id().value());
    }

    @Override
    public MechanicsDocument extract(
            SpellDefinition spell, SpellMechanicsEvidenceSource source, MechanicsDocument original) {
        if (!supports(spell) || !spell.source().identity().code().equals("PHB")) {
            throw new IllegalArgumentException("Only the pinned PHB Spiritual Weapon owner is supported");
        }
        if (original.processing().state() != ProcessingState.NOT_ATTEMPTED) {
            throw new IllegalArgumentException("Spiritual Weapon extraction requires an unattempted owner");
        }

        TextMechanicsEvidence creation = source.textEvidence(BODY,
                "You create a floating, spectral weapon within range that lasts for the duration or until you cast this spell again.");
        TextMechanicsEvidence initialAttackText = source.textEvidence(BODY,
                "When you cast the spell, you can make a melee spell attack against a creature within 5 feet of the weapon.");
        TextMechanicsEvidence hitText = source.textEvidence(BODY, "On a hit");
        TextMechanicsEvidence damageText = source.textEvidence(BODY,
                "the target takes force damage equal to {@damage 1d8} + your spellcasting ability modifier.");
        TextMechanicsEvidence repeatText = source.textEvidence(REPEAT,
                "As a bonus action on your turn, you can move the weapon up to 20 feet and repeat the attack against a creature within 5 feet of it.");
        TextMechanicsEvidence scalingText = source.textEvidence(HIGHER,
                "When you cast this spell using a spell slot of 3rd level or higher, the damage increases by {@scaledamage 1d8|2,4,6,8|1d8} for every two slot levels above 2nd.");
        TextMechanicsEvidence slotThresholdText = source.textEvidence(HIGHER, "a spell slot of 3rd level or higher");
        TextMechanicsEvidence formulaText = source.textEvidence(HIGHER, "for every two slot levels above 2nd");
        StructuredFieldMechanicsEvidence durationField = source.fieldEvidence("/duration/0");
        StructuredFieldMechanicsEvidence castingTimeField = source.fieldEvidence("/time/0");
        StructuredFieldMechanicsEvidence attackKindField = source.fieldEvidence("/spellAttack/0");
        StructuredFieldMechanicsEvidence damageTypeField = source.fieldEvidence("/damageInflict/0");

        List<MechanicsEvidence> evidence = source.evidence();
        MechanicId effectId = MechanicId.of(source.owner(), "persistent-effect");
        MechanicRecord effect = claim(source, effectId, "persistent-effect", "spiritual-weapon-effect-v1",
                new PersistentSpellEffectMechanic(
                        new DeclaredSpellDurationReference(spell.id(), 0, source.source().revision()),
                        false, false, true, true),
                List.of(association(creation, "/payload", EvidenceRole.EFFECT_ROLE),
                        association(creation, "/payload/duration", EvidenceRole.DURATION),
                        association(creation, "/payload/endsWhenSpellIsCastAgain", EvidenceRole.QUALIFIER),
                        association(durationField, "/payload/concentrationRequired", EvidenceRole.QUALIFIER)), evidence);

        MechanicId attackId = MechanicId.of(source.owner(), "attack-roll");
        MechanicRevisionReference effectReference = effect.revision().reference(effect.id());
        AttackRollMechanic attackPayload = new AttackRollMechanic(AttackKind.MELEE_SPELL,
                AttackActorRole.SPELL_CREATED_EFFECT, 1, false, AttackActivationTiming.CAST_TIME,
                null, null, null, new AttackActivationPattern(AttackActivationTiming.CAST_TIME,
                        AttackActivationTiming.BONUS_ACTION, true, 1, effectReference));
        MechanicRecord attack = claim(source, attackId, "attack-roll", "spiritual-weapon-attack-v1",
                attackPayload, List.of(association(initialAttackText, "/payload", EvidenceRole.EFFECT_ROLE),
                        association(castingTimeField, "/payload/activationTiming", EvidenceRole.TRIGGER),
                        association(initialAttackText, "/payload/activationTiming", EvidenceRole.TRIGGER),
                        association(repeatText, "/payload/activationPattern", EvidenceRole.TRIGGER),
                        association(initialAttackText, "/payload/attackKind", EvidenceRole.VALUE),
                        association(attackKindField, "/payload/attackKind", EvidenceRole.VALUE)), evidence,
                new MechanicContext(new SpellTargetSubject(TargetScope.SINGLE_TARGET_CREATURE), List.of()),
                MechanicsCompleteness.PARTIAL);

        MechanicRevisionReference attackReference = attack.revision().reference(attack.id());
        DamageMechanic damage = new DamageMechanic(
                new SumAmount(List.of(new DiceAmount(1, 8, 0),
                        new SymbolicAmount(AmountSymbol.CASTER_SPELLCASTING_ABILITY_MODIFIER))),
                new FixedDamageType("force"), null,
                new SpellSlotDamageScaling(new DiceAmount(1, 8, 0), 2, 3, 2), null);
        MechanicRecord damageRecord = claim(source, MechanicId.of(source.owner(), "damage"), "damage",
                "spiritual-weapon-damage-v1", damage,
                List.of(association(damageText, "/payload", EvidenceRole.EFFECT_ROLE),
                        association(damageText, "/payload/amount", EvidenceRole.VALUE),
                        association(damageTypeField, "/payload/damageType", EvidenceRole.VALUE),
                        association(hitText, "/context/bindings/0", EvidenceRole.TRIGGER),
                        association(scalingText, "/payload/spellSlotScaling", EvidenceRole.SCALING),
                        association(slotThresholdText, "/payload/spellSlotScaling/minimumCastingSlotLevel",
                                EvidenceRole.SCALING),
                        association(formulaText, "/payload/spellSlotScaling/slotLevelInterval", EvidenceRole.SCALING)),
                evidence,
                new MechanicContext(new SpellTargetSubject(TargetScope.SINGLE_TARGET_CREATURE),
                        List.of(new AppliesOnAttackRollOutcome(attackReference, AttackRollOutcome.HIT,
                                List.of(hitText.id())))));

        List<UnsupportedMechanicsConstruct> unsupported = List.of(
                unsupported("weapon-spatial-relationship", MechanicFamily.ATTACK_ROLL,
                        List.of(creation.id(), initialAttackText.id(), repeatText.id())),
                unsupported("weapon-movement-and-attack-coupling", MechanicFamily.ATTACK_ROLL,
                        List.of(repeatText.id())));
        List<MechanicRecord> records = List.of(effect, attack, damageRecord);
        return MechanicsDocument.create(source.owner(), 1, List.of(source.source().revision()),
                new MechanicsProcessing(ProcessingState.PROCESSED, "phb-spiritual-weapon", "1"), records,
                original.observations(), unsupported, evidence,
                new MechanicsCoverage(CoverageStatus.PARTIAL, "phb-spiritual-weapon", "1",
                        List.of(source.source().jsonPointer() + "/time", source.source().jsonPointer() + "/duration",
                                source.source().jsonPointer() + "/entries", source.source().jsonPointer() + "/entriesHigherLevel"),
                        List.of(source.source().jsonPointer() + "/entries/2"), unsupported.size(),
                        CoverageScopeExtent.DECLARED_SUBSET));
    }

    private static MechanicRecord claim(
            SpellMechanicsEvidenceSource source,
            MechanicId id,
            String slot,
            String pattern,
            com.thedndpub.data.mechanics.model.MechanicPayload payload,
            List<MechanicEvidenceAssociation> associations,
            List<MechanicsEvidence> evidence) {
        return claim(source, id, slot, pattern, payload, associations, evidence,
                new MechanicContext(new SpellTargetSubject(TargetScope.SINGLE_TARGET_UNSPECIFIED_CATEGORY), List.of()));
    }

    private static MechanicRecord claim(
            SpellMechanicsEvidenceSource source,
            MechanicId id,
            String slot,
            String pattern,
            com.thedndpub.data.mechanics.model.MechanicPayload payload,
            List<MechanicEvidenceAssociation> associations,
            List<MechanicsEvidence> evidence,
            MechanicContext context) {
        return claim(source, id, slot, pattern, payload, associations, evidence, context,
                MechanicsCompleteness.COMPLETE_WITHIN_CLAIM);
    }

    private static MechanicRecord claim(
            SpellMechanicsEvidenceSource source,
            MechanicId id,
            String slot,
            String pattern,
            com.thedndpub.data.mechanics.model.MechanicPayload payload,
            List<MechanicEvidenceAssociation> associations,
            List<MechanicsEvidence> evidence,
            MechanicContext context,
            MechanicsCompleteness completeness) {
        var derivation = new MechanicDerivation(DerivationMethod.BOUNDED_CONTEXT_INTERPRETATION,
                pattern, "1", "phb-spiritual-weapon-1", List.of(), List.of(source.source().revision()));
        var assessment = new MechanicsAssessment(completeness,
                MechanicsSupport.BOUNDED_CONTEXT, List.of(), List.of());
        return MechanicRecord.propose(id, payload, context, associations, derivation, assessment);
    }

    private static MechanicEvidenceAssociation association(
            TextMechanicsEvidence text, String path, EvidenceRole role) {
        return MechanicEvidenceAssociation.of(text, path, role);
    }

    private static MechanicEvidenceAssociation association(
            StructuredFieldMechanicsEvidence field, String path, EvidenceRole role) {
        return MechanicEvidenceAssociation.of(field, path, role);
    }

    private static UnsupportedMechanicsConstruct unsupported(
            String id, MechanicFamily family, List<String> evidenceIds) {
        return new UnsupportedMechanicsConstruct("spiritual-weapon-unsupported:" + id, family,
                UnsupportedReason.FAMILY_NOT_IMPLEMENTED, evidenceIds, List.of());
    }
}
