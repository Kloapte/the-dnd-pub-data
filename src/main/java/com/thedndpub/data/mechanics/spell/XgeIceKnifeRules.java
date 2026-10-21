package com.thedndpub.data.mechanics.spell;

import com.thedndpub.data.dto.spell.SpellDefinition;
import com.thedndpub.data.mechanics.model.AppliesOnAttackRollOutcome;
import com.thedndpub.data.mechanics.model.AppliesOnSaveOutcome;
import com.thedndpub.data.mechanics.model.AppliesToAreaEffect;
import com.thedndpub.data.mechanics.model.AreaEffectCenter;
import com.thedndpub.data.mechanics.model.AreaEffectFrequency;
import com.thedndpub.data.mechanics.model.AttackActivationTiming;
import com.thedndpub.data.mechanics.model.AttackActorRole;
import com.thedndpub.data.mechanics.model.AttackEffectTrigger;
import com.thedndpub.data.mechanics.model.AttackKind;
import com.thedndpub.data.mechanics.model.AttackRollMechanic;
import com.thedndpub.data.mechanics.model.AttackRollOutcome;
import com.thedndpub.data.mechanics.model.AttackTriggeredAreaEffectMechanic;
import com.thedndpub.data.mechanics.model.CoverageStatus;
import com.thedndpub.data.mechanics.model.CoverageScopeExtent;
import com.thedndpub.data.mechanics.model.DamageMechanic;
import com.thedndpub.data.mechanics.model.DamageOutcome;
import com.thedndpub.data.mechanics.model.DiceAmount;
import com.thedndpub.data.mechanics.model.EvidenceRole;
import com.thedndpub.data.mechanics.model.FixedAbility;
import com.thedndpub.data.mechanics.model.FixedDamageType;
import com.thedndpub.data.mechanics.model.MechanicContext;
import com.thedndpub.data.mechanics.model.MechanicEvidenceAssociation;
import com.thedndpub.data.mechanics.model.MechanicFamily;
import com.thedndpub.data.mechanics.model.MechanicId;
import com.thedndpub.data.mechanics.model.MechanicRecord;
import com.thedndpub.data.mechanics.model.MechanicRevisionReference;
import com.thedndpub.data.mechanics.model.MechanicsAssessment;
import com.thedndpub.data.mechanics.model.MechanicsCompleteness;
import com.thedndpub.data.mechanics.model.MechanicsCoverage;
import com.thedndpub.data.mechanics.model.MechanicsDocument;
import com.thedndpub.data.mechanics.model.MechanicsProcessing;
import com.thedndpub.data.mechanics.model.MechanicsSupport;
import com.thedndpub.data.mechanics.model.ProcessingState;
import com.thedndpub.data.mechanics.model.SaveOutcome;
import com.thedndpub.data.mechanics.model.SavingThrowRequirementMechanic;
import com.thedndpub.data.mechanics.model.SpellSlotDamageScaling;
import com.thedndpub.data.mechanics.model.SpellTargetSubject;
import com.thedndpub.data.mechanics.model.SumAmount;
import com.thedndpub.data.mechanics.model.TargetCategory;
import com.thedndpub.data.mechanics.model.TargetScope;
import com.thedndpub.data.mechanics.model.TextMechanicsEvidence;
import com.thedndpub.data.mechanics.model.UnknownSaveDifficulty;
import com.thedndpub.data.mechanics.model.UnknownValueReason;
import com.thedndpub.data.mechanics.model.UnsupportedMechanicsConstruct;
import com.thedndpub.data.mechanics.model.UnsupportedReason;

import java.util.List;

/** Source-bounded XGE Ice Knife interpretation. */
final class XgeIceKnifeRules implements SpellMechanicsPattern {
    static final String OWNER_ID = "spell:xge:ice-knife";
    private static final String BODY = "/entries/0";
    private static final String HIGHER_LEVEL = "/entriesHigherLevel/0/entries/0";
    private static final String ATTACK_TEXT = "Make a ranged spell attack against the target.";
    private static final String INITIAL_TARGET = "at one creature within range.";
    private static final String HIT = "On a hit";
    private static final String PIERCING = "the target takes {@damage 1d10} piercing damage.";
    private static final String OUTCOME_INDEPENDENT = "Hit or miss, the shard then explodes.";
    private static final String AREA_TEXT = "The target and each creature within 5 feet of it";
    private static final String SAVE_TEXT = "must succeed on a Dexterity saving throw";
    private static final String COLD_DAMAGE = "or take {@damage 2d6} cold damage.";
    private static final String SLOT_SCALING = "When you cast this spell using a spell slot of 2nd level or higher, "
            + "the cold damage increases by {@scaledamage 2d6|1-9|1d6} for each slot level above 1st.";

    @Override
    public String patternId() {
        return "xge-ice-knife-outcome-independent-explosion-v1";
    }

    @Override
    public boolean supports(SpellDefinition spell) {
        return OWNER_ID.equals(spell.id().value()) && "XGE".equals(spell.source().identity().code());
    }

    @Override
    public MechanicsDocument extract(
            SpellDefinition spell, SpellMechanicsEvidenceSource source, MechanicsDocument original) {
        if (!supports(spell) || original.processing().state() != ProcessingState.NOT_ATTEMPTED) {
            throw new IllegalArgumentException("Only the pinned XGE Ice Knife owner is supported");
        }

        TextMechanicsEvidence attackText = source.textEvidence(BODY, ATTACK_TEXT);
        TextMechanicsEvidence targetText = source.textEvidence(BODY, INITIAL_TARGET);
        TextMechanicsEvidence hitText = source.textEvidence(BODY, HIT);
        TextMechanicsEvidence piercingText = source.textEvidence(BODY, PIERCING);
        TextMechanicsEvidence explosionText = source.textEvidence(BODY, OUTCOME_INDEPENDENT);
        TextMechanicsEvidence areaText = source.textEvidence(BODY, AREA_TEXT);
        TextMechanicsEvidence saveText = source.textEvidence(BODY, SAVE_TEXT);
        TextMechanicsEvidence coldText = source.textEvidence(BODY, COLD_DAMAGE);
        TextMechanicsEvidence scalingText = source.textEvidence(HIGHER_LEVEL, SLOT_SCALING);
        TextMechanicsEvidence piercingFormula = source.textEvidence(BODY, "{@damage 1d10}");
        TextMechanicsEvidence coldFormula = source.textEvidence(BODY, "{@damage 2d6}");

        var castingTime = source.fieldEvidence("/time/0");
        var castingRange = source.fieldEvidence("/range");
        var duration = source.fieldEvidence("/duration/0");
        var spellAttack = source.fieldEvidence("/spellAttack/0");
        var piercingType = source.fieldEvidence("/damageInflict/1");
        var coldType = source.fieldEvidence("/damageInflict/0");
        var saveAbility = source.fieldEvidence("/savingThrow/0");
        List<com.thedndpub.data.mechanics.model.MechanicsEvidence> evidence = source.evidence();
        String root = source.source().jsonPointer();

        MechanicRecord attack = claim(source, "attack-roll", "ice-knife-attack-v1",
                new AttackRollMechanic(AttackKind.RANGED_SPELL, AttackActorRole.CASTER,
                        1, false, AttackActivationTiming.CAST_TIME),
                List.of(association(attackText, "/payload", EvidenceRole.EFFECT_ROLE),
                        association(targetText, "/context/subject", EvidenceRole.SUBJECT),
                        association(castingTime, "/payload/activationTiming", EvidenceRole.TRIGGER),
                        association(spellAttack, "/payload/attackKind", EvidenceRole.VALUE)),
                evidence, new MechanicContext(new SpellTargetSubject(TargetScope.SINGLE_TARGET_CREATURE), List.of()),
                MechanicsCompleteness.PARTIAL);
        MechanicRevisionReference attackReference = attack.revision().reference(attack.id());

        MechanicRecord piercing = claim(source, "piercing-damage", "ice-knife-piercing-damage-v1",
                new DamageMechanic(new DiceAmount(1, 10, 0), new FixedDamageType("piercing")),
                List.of(association(piercingText, "/payload", EvidenceRole.EFFECT_ROLE),
                        association(piercingFormula, "/payload/amount", EvidenceRole.VALUE),
                        association(piercingType, "/payload/damageType", EvidenceRole.VALUE),
                        association(hitText, "/context/bindings/0", EvidenceRole.TRIGGER)),
                evidence, new MechanicContext(new SpellTargetSubject(TargetScope.SINGLE_TARGET_CREATURE),
                        List.of(new AppliesOnAttackRollOutcome(attackReference, AttackRollOutcome.HIT,
                                List.of(hitText.id())))), MechanicsCompleteness.COMPLETE_WITHIN_CLAIM);

        MechanicRecord explosion = claim(source, "explosion", "ice-knife-explosion-v1",
                new AttackTriggeredAreaEffectMechanic(attackReference,
                        AttackEffectTrigger.AFTER_ATTACK_REGARDLESS_OF_OUTCOME,
                        AreaEffectCenter.ATTACK_TARGET, 5, true, List.of(TargetCategory.CREATURE),
                        AreaEffectFrequency.ONCE_PER_ATTACK),
                List.of(association(explosionText, "/payload/trigger", EvidenceRole.TRIGGER),
                        association(explosionText, "/payload", EvidenceRole.EFFECT_ROLE),
                        association(areaText, "/payload/center", EvidenceRole.SUBJECT),
                        association(areaText, "/payload/radiusFeet", EvidenceRole.VALUE),
                        association(areaText, "/payload/includesAttackTarget", EvidenceRole.QUALIFIER),
                        association(areaText, "/payload/affectedTargetCategories", EvidenceRole.QUALIFIER),
                        association(duration, "/payload/frequency", EvidenceRole.QUALIFIER)),
                evidence, new MechanicContext(new SpellTargetSubject(TargetScope.AREA_AFFECTED_CREATURES), List.of()),
                MechanicsCompleteness.COMPLETE_WITHIN_CLAIM);
        MechanicRevisionReference explosionReference = explosion.revision().reference(explosion.id());

        MechanicRecord save = claim(source, "explosion-save", "ice-knife-explosion-save-v1",
                new SavingThrowRequirementMechanic(new FixedAbility(
                        com.thedndpub.data.mechanics.model.Ability.DEXTERITY),
                        new UnknownSaveDifficulty(UnknownValueReason.NOT_AUTHORED)),
                List.of(association(saveText, "/payload", EvidenceRole.EFFECT_ROLE),
                        association(saveAbility, "/payload/ability", EvidenceRole.VALUE),
                        association(areaText, "/context/subject", EvidenceRole.SUBJECT),
                        association(areaText, "/context/bindings/0", EvidenceRole.TRIGGER)), evidence,
                new MechanicContext(new SpellTargetSubject(TargetScope.AREA_AFFECTED_CREATURES),
                        List.of(new AppliesToAreaEffect(explosionReference, List.of(areaText.id())))),
                MechanicsCompleteness.PARTIAL);
        MechanicRevisionReference saveReference = save.revision().reference(save.id());

        MechanicRecord cold = claim(source, "explosion-damage", "ice-knife-explosion-damage-v1",
                new DamageMechanic(new DiceAmount(2, 6, 0), new FixedDamageType("cold"), null,
                        new SpellSlotDamageScaling(new DiceAmount(1, 6, 0), 1, 2, 1)),
                List.of(association(coldText, "/payload", EvidenceRole.EFFECT_ROLE),
                        association(coldFormula, "/payload/amount", EvidenceRole.VALUE),
                        association(coldType, "/payload/damageType", EvidenceRole.VALUE),
                        association(scalingText, "/payload/spellSlotScaling", EvidenceRole.VALUE),
                        association(areaText, "/context/bindings/0", EvidenceRole.TRIGGER),
                        association(coldText, "/context/bindings/1", EvidenceRole.TRIGGER),
                        association(coldText, "/context/bindings/2", EvidenceRole.TRIGGER),
                        association(coldText, "/context/subject", EvidenceRole.SUBJECT)), evidence,
                new MechanicContext(new SpellTargetSubject(TargetScope.AREA_AFFECTED_CREATURES), List.of(
                        new AppliesToAreaEffect(explosionReference, List.of(areaText.id())),
                        new AppliesOnSaveOutcome(saveReference, SaveOutcome.FAILURE, DamageOutcome.FULL_DAMAGE,
                                List.of(coldText.id())),
                        new AppliesOnSaveOutcome(saveReference, SaveOutcome.SUCCESS, DamageOutcome.NO_DAMAGE,
                                List.of(coldText.id())))), MechanicsCompleteness.COMPLETE_WITHIN_CLAIM);

        List<UnsupportedMechanicsConstruct> unsupported = List.of(
                new UnsupportedMechanicsConstruct("ice-knife-unsupported:attack-range", MechanicFamily.ATTACK_ROLL,
                        UnsupportedReason.FAMILY_NOT_IMPLEMENTED,
                        List.of(targetText.id(), castingRange.id()), List.of()),
                new UnsupportedMechanicsConstruct("ice-knife-unsupported:save-dc-attribution",
                        MechanicFamily.SAVING_THROW_REQUIREMENT, UnsupportedReason.SOURCE_UNSPECIFIED,
                        List.of(saveText.id(), saveAbility.id()), List.of()));
        return MechanicsDocument.create(source.owner(), 1, List.of(source.source().revision()),
                new MechanicsProcessing(ProcessingState.PROCESSED, "xge-ice-knife", "1"),
                List.of(attack, piercing, explosion, save, cold), original.observations(), unsupported, evidence,
                new MechanicsCoverage(CoverageStatus.PARTIAL, "xge-ice-knife", "1",
                        List.of(root + "/time", root + "/range", root + "/duration", root + "/entries",
                                root + "/entriesHigherLevel", root + "/spellAttack", root + "/damageInflict",
                                root + "/savingThrow"), List.of(), unsupported.size(),
                        CoverageScopeExtent.DECLARED_SUBSET));
    }

    private static MechanicRecord claim(
            SpellMechanicsEvidenceSource source,
            String slot,
            String pattern,
            com.thedndpub.data.mechanics.model.MechanicPayload payload,
            List<MechanicEvidenceAssociation> associations,
            List<com.thedndpub.data.mechanics.model.MechanicsEvidence> evidence,
            MechanicContext context,
            MechanicsCompleteness completeness) {
        var derivation = new com.thedndpub.data.mechanics.model.MechanicDerivation(
                com.thedndpub.data.mechanics.model.DerivationMethod.BOUNDED_CONTEXT_INTERPRETATION,
                pattern, "1", "xge-ice-knife-1", List.of(), List.of(source.source().revision()));
        var assessment = new MechanicsAssessment(completeness, MechanicsSupport.BOUNDED_CONTEXT, List.of(), List.of());
        return MechanicRecord.propose(MechanicId.of(source.owner(), slot), payload, context, associations,
                derivation, assessment);
    }

    private static MechanicEvidenceAssociation association(
            com.thedndpub.data.mechanics.model.MechanicsEvidence evidence, String path, EvidenceRole role) {
        return MechanicEvidenceAssociation.of(evidence, path, role);
    }
}
