package com.thedndpub.data.mechanics.spell;

import com.thedndpub.data.dto.spell.SpellDefinition;
import com.thedndpub.data.mechanics.model.ActionActivatedDamage;
import com.thedndpub.data.mechanics.model.AppliesOnAttackRollOutcome;
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
import com.thedndpub.data.mechanics.model.FixedDamageType;
import com.thedndpub.data.mechanics.model.HitGatedEffectEstablishment;
import com.thedndpub.data.mechanics.model.MechanicContext;
import com.thedndpub.data.mechanics.model.MechanicDerivation;
import com.thedndpub.data.mechanics.model.MechanicEvidenceAssociation;
import com.thedndpub.data.mechanics.model.MechanicFamily;
import com.thedndpub.data.mechanics.model.MechanicId;
import com.thedndpub.data.mechanics.model.MechanicRecord;
import com.thedndpub.data.mechanics.model.MechanicsAssessment;
import com.thedndpub.data.mechanics.model.MechanicsCompleteness;
import com.thedndpub.data.mechanics.model.MechanicsCoverage;
import com.thedndpub.data.mechanics.model.MechanicsDocument;
import com.thedndpub.data.mechanics.model.MechanicsEvidence;
import com.thedndpub.data.mechanics.model.MechanicsProcessing;
import com.thedndpub.data.mechanics.model.MechanicsSupport;
import com.thedndpub.data.mechanics.model.PersistentEffectTerminationCondition;
import com.thedndpub.data.mechanics.model.PersistentSpellEffectMechanic;
import com.thedndpub.data.mechanics.model.ProcessingState;
import com.thedndpub.data.mechanics.model.SourceArtifactRevision;
import com.thedndpub.data.mechanics.model.SpellSlotDamageScaling;
import com.thedndpub.data.mechanics.model.SpellTargetSubject;
import com.thedndpub.data.mechanics.model.StructuredFieldMechanicsEvidence;
import com.thedndpub.data.mechanics.model.TargetScope;
import com.thedndpub.data.mechanics.model.TextMechanicsEvidence;
import com.thedndpub.data.mechanics.model.UnsupportedMechanicsConstruct;
import com.thedndpub.data.mechanics.model.UnsupportedReason;

import java.util.List;

/** PHB-only, source-bounded Witch Bolt interpretation. */
final class PhbWitchBoltRules implements SpellMechanicsPattern {
    static final String OWNER_ID = "spell:phb:witch-bolt";
    private static final String BODY = "/entries/0";
    private static final String HIGHER = "/entriesHigherLevel/0/entries/0";

    @Override
    public String patternId() {
        return "phb-witch-bolt-hit-gated-damage-v1";
    }

    @Override
    public boolean supports(SpellDefinition spell) {
        return OWNER_ID.equals(spell.id().value());
    }

    @Override
    public MechanicsDocument extract(
            SpellDefinition spell, SpellMechanicsEvidenceSource source, MechanicsDocument original) {
        if (!supports(spell) || !spell.source().identity().code().equals("PHB")) {
            throw new IllegalArgumentException("Only the pinned PHB Witch Bolt owner is supported");
        }
        if (original.processing().state() != ProcessingState.NOT_ATTEMPTED) {
            throw new IllegalArgumentException("Witch Bolt extraction requires an unattempted owner");
        }

        TextMechanicsEvidence opening = source.textEvidence(BODY,
                "A beam of crackling, blue energy lances out toward a creature within range, forming a sustained arc of lightning between you and the target.");
        TextMechanicsEvidence attackText = source.textEvidence(BODY,
                "Make a ranged spell attack against that creature.");
        TextMechanicsEvidence hitText = source.textEvidence(BODY, "On a hit");
        TextMechanicsEvidence initialDamageText = source.textEvidence(BODY,
                "the target takes {@damage 1d12} lightning damage");
        TextMechanicsEvidence laterDamageText = source.textEvidence(BODY,
                "on each of your turns for the duration, you can use your action to deal {@damage 1d12} lightning damage to the target automatically.");
        TextMechanicsEvidence actionEndText = source.textEvidence(BODY,
                "The spell ends if you use your action to do anything else.");
        TextMechanicsEvidence rangeEndText = source.textEvidence(BODY,
                "The spell also ends if the target is ever outside the spell's range");
        TextMechanicsEvidence coverEndText = source.textEvidence(BODY,
                "if it has {@quickref Cover||3||total cover} from you.");
        TextMechanicsEvidence scalingText = source.textEvidence(HIGHER,
                "When you cast this spell using a spell slot of 2nd level or higher, the initial damage increases by {@scaledamage 1d12|1-9|1d12} for each slot level above 1st.");
        TextMechanicsEvidence scalingThreshold = source.textEvidence(HIGHER,
                "a spell slot of 2nd level or higher");
        TextMechanicsEvidence scalingInterval = source.textEvidence(HIGHER,
                "for each slot level above 1st");

        StructuredFieldMechanicsEvidence timeField = source.fieldEvidence("/time/0");
        StructuredFieldMechanicsEvidence rangeField = source.fieldEvidence("/range");
        StructuredFieldMechanicsEvidence durationField = source.fieldEvidence("/duration/0");
        StructuredFieldMechanicsEvidence attackKindField = source.fieldEvidence("/spellAttack/0");
        StructuredFieldMechanicsEvidence damageTypeField = source.fieldEvidence("/damageInflict/0");
        List<MechanicsEvidence> evidence = source.evidence();
        SpellTargetSubject subject = new SpellTargetSubject(TargetScope.SINGLE_TARGET_CREATURE);

        MechanicRecord attack = claim(source, "attack-roll", "witch-bolt-attack-v1",
                new AttackRollMechanic(AttackKind.RANGED_SPELL, AttackActorRole.CASTER, 1,
                        false, AttackActivationTiming.CAST_TIME),
                List.of(association(opening, "/context/subject", EvidenceRole.SUBJECT),
                        association(attackText, "/payload", EvidenceRole.EFFECT_ROLE),
                        association(attackText, "/payload/attackKind", EvidenceRole.VALUE),
                        association(attackKindField, "/payload/attackKind", EvidenceRole.VALUE),
                        association(timeField, "/payload/activationTiming", EvidenceRole.TRIGGER)),
                evidence, subject, MechanicsCompleteness.PARTIAL);
        var attackReference = attack.revision().reference(attack.id());

        MechanicRecord effect = claim(source, "persistent-link", "witch-bolt-link-v1",
                new PersistentSpellEffectMechanic(
                        new DeclaredSpellDurationReference(spell.id(), 0, source.source().revision()),
                        true, false, true, null, new HitGatedEffectEstablishment(attackReference),
                        List.of(PersistentEffectTerminationCondition.CONCENTRATION_ENDS,
                                PersistentEffectTerminationCondition.DURATION_EXPIRES,
                                PersistentEffectTerminationCondition.TARGET_OUTSIDE_SPELL_RANGE_FROM_CASTER,
                                PersistentEffectTerminationCondition.TARGET_HAS_TOTAL_COVER_FROM_CASTER,
                                PersistentEffectTerminationCondition.CASTER_USES_SUBSEQUENT_ACTION_FOR_ANOTHER_PURPOSE)),
                List.of(association(opening, "/payload", EvidenceRole.EFFECT_ROLE),
                        association(hitText, "/payload/establishment", EvidenceRole.TRIGGER),
                        association(attackText, "/payload/establishment/attackRoll", EvidenceRole.TRIGGER),
                        association(durationField, "/payload/duration", EvidenceRole.DURATION),
                        association(durationField, "/payload/concentrationRequired", EvidenceRole.QUALIFIER),
                        association(rangeEndText, "/payload/terminationConditions/2", EvidenceRole.QUALIFIER),
                        association(coverEndText, "/payload/terminationConditions/3", EvidenceRole.QUALIFIER),
                        association(actionEndText, "/payload/terminationConditions/4", EvidenceRole.QUALIFIER)),
                evidence, subject, MechanicsCompleteness.COMPLETE_WITHIN_CLAIM);
        var effectReference = effect.revision().reference(effect.id());

        MechanicRecord initialDamage = claim(source, "initial-damage", "witch-bolt-initial-damage-v1",
                new DamageMechanic(new DiceAmount(1, 12, 0), new FixedDamageType("lightning"), null,
                        new SpellSlotDamageScaling(new DiceAmount(1, 12, 0), 1, 2, 1), null),
                List.of(association(initialDamageText, "/payload", EvidenceRole.EFFECT_ROLE),
                        association(initialDamageText, "/payload/amount", EvidenceRole.VALUE),
                        association(damageTypeField, "/payload/damageType", EvidenceRole.VALUE),
                        association(hitText, "/context/bindings/0", EvidenceRole.TRIGGER),
                        association(scalingText, "/payload/spellSlotScaling", EvidenceRole.SCALING),
                        association(scalingThreshold, "/payload/spellSlotScaling/minimumCastingSlotLevel",
                                EvidenceRole.SCALING),
                        association(scalingInterval, "/payload/spellSlotScaling/slotLevelInterval",
                                EvidenceRole.SCALING)),
                evidence, subject,
                List.of(new AppliesOnAttackRollOutcome(attackReference, AttackRollOutcome.HIT,
                        List.of(hitText.id()))), MechanicsCompleteness.COMPLETE_WITHIN_CLAIM);

        MechanicRecord subsequentDamage = claim(source, "subsequent-damage", "witch-bolt-subsequent-damage-v1",
                new DamageMechanic(new DiceAmount(1, 12, 0), new FixedDamageType("lightning"),
                        null, null, null, ActionActivatedDamage.witchBolt(effectReference)),
                List.of(association(laterDamageText, "/payload", EvidenceRole.EFFECT_ROLE),
                        association(laterDamageText, "/payload/actionActivation", EvidenceRole.TRIGGER),
                        association(laterDamageText, "/payload/amount", EvidenceRole.VALUE),
                        association(damageTypeField, "/payload/damageType", EvidenceRole.VALUE),
                        association(laterDamageText, "/payload/actionActivation/resolution",
                                EvidenceRole.QUALIFIER),
                        association(laterDamageText, "/payload/actionActivation/target", EvidenceRole.SUBJECT)),
                evidence, subject, MechanicsCompleteness.COMPLETE_WITHIN_CLAIM);

        List<UnsupportedMechanicsConstruct> unsupported = List.of(
                new UnsupportedMechanicsConstruct("witch-bolt-unsupported:initial-attack-range",
                        MechanicFamily.ATTACK_ROLL, UnsupportedReason.FAMILY_NOT_IMPLEMENTED,
                        List.of(opening.id(), rangeField.id()), List.of()));
        List<MechanicRecord> records = List.of(attack, effect, initialDamage, subsequentDamage);
        return MechanicsDocument.create(source.owner(), 1, List.of(source.source().revision()),
                new MechanicsProcessing(ProcessingState.PROCESSED, "phb-witch-bolt", "1"), records,
                original.observations(), unsupported, evidence,
                new MechanicsCoverage(CoverageStatus.PARTIAL, "phb-witch-bolt", "1",
                        List.of(source.source().jsonPointer() + "/time", source.source().jsonPointer() + "/range",
                                source.source().jsonPointer() + "/duration", source.source().jsonPointer() + "/entries",
                                source.source().jsonPointer() + "/entriesHigherLevel"),
                        List.of(source.source().jsonPointer() + "/components"), unsupported.size(),
                        CoverageScopeExtent.DECLARED_SUBSET));
    }

    private static MechanicRecord claim(
            SpellMechanicsEvidenceSource source, String slot, String pattern,
            com.thedndpub.data.mechanics.model.MechanicPayload payload,
            List<MechanicEvidenceAssociation> associations, List<MechanicsEvidence> evidence,
            SpellTargetSubject subject, MechanicsCompleteness completeness) {
        return claim(source, slot, pattern, payload, associations, evidence, subject, List.of(), completeness);
    }

    private static MechanicRecord claim(
            SpellMechanicsEvidenceSource source, String slot, String pattern,
            com.thedndpub.data.mechanics.model.MechanicPayload payload,
            List<MechanicEvidenceAssociation> associations, List<MechanicsEvidence> evidence,
            SpellTargetSubject subject, List<com.thedndpub.data.mechanics.model.MechanicContextBinding> bindings,
            MechanicsCompleteness completeness) {
        MechanicId id = MechanicId.of(source.owner(), slot);
        var derivation = new MechanicDerivation(DerivationMethod.BOUNDED_CONTEXT_INTERPRETATION,
                pattern, "1", "phb-witch-bolt-1", List.of(), List.of(source.source().revision()));
        var assessment = new MechanicsAssessment(completeness, MechanicsSupport.BOUNDED_CONTEXT, List.of(), List.of());
        return MechanicRecord.propose(id, payload, new MechanicContext(subject, bindings), associations,
                derivation, assessment);
    }

    private static MechanicRecord claim(
            SpellMechanicsEvidenceSource source, String slot, String pattern,
            com.thedndpub.data.mechanics.model.MechanicPayload payload,
            List<MechanicEvidenceAssociation> associations, List<MechanicsEvidence> evidence,
            SpellTargetSubject subject, MechanicsCompleteness completeness,
            com.thedndpub.data.mechanics.model.MechanicContextBinding binding) {
        return claim(source, slot, pattern, payload, associations, evidence, subject,
                List.of(binding), completeness);
    }

    private static MechanicEvidenceAssociation association(
            TextMechanicsEvidence evidence, String path, EvidenceRole role) {
        return MechanicEvidenceAssociation.of(evidence, path, role);
    }

    private static MechanicEvidenceAssociation association(
            StructuredFieldMechanicsEvidence evidence, String path, EvidenceRole role) {
        return MechanicEvidenceAssociation.of(evidence, path, role);
    }
}
