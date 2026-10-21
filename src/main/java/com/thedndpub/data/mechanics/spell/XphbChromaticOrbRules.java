package com.thedndpub.data.mechanics.spell;

import com.thedndpub.data.mechanics.model.AppliesOnAttackRollOutcome;
import com.thedndpub.data.mechanics.model.AttackActivationTiming;
import com.thedndpub.data.mechanics.model.AttackActorRole;
import com.thedndpub.data.mechanics.model.AttackKind;
import com.thedndpub.data.mechanics.model.AttackRollMechanic;
import com.thedndpub.data.mechanics.model.AttackRollOutcome;
import com.thedndpub.data.mechanics.model.AdditionalAttackMissBehavior;
import com.thedndpub.data.mechanics.model.ChainDistanceOrigin;
import com.thedndpub.data.mechanics.model.ChainTargetCategory;
import com.thedndpub.data.mechanics.model.ChainTargetDistance;
import com.thedndpub.data.mechanics.model.ChainTargetReusePolicy;
import com.thedndpub.data.mechanics.model.ConditionalAttackChainMechanic;
import com.thedndpub.data.mechanics.model.CoverageStatus;
import com.thedndpub.data.mechanics.model.CoverageScopeExtent;
import com.thedndpub.data.mechanics.model.DamageDiceMatchRule;
import com.thedndpub.data.mechanics.model.DamageDicePopulation;
import com.thedndpub.data.mechanics.model.DamageMechanic;
import com.thedndpub.data.mechanics.model.DamageTypeChoice;
import com.thedndpub.data.mechanics.model.DamageTypeChoiceTiming;
import com.thedndpub.data.mechanics.model.DerivationMethod;
import com.thedndpub.data.mechanics.model.DiceAmount;
import com.thedndpub.data.mechanics.model.EvidenceRole;
import com.thedndpub.data.mechanics.model.ExternalDamageDiceTreatment;
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
import com.thedndpub.data.mechanics.model.SpellSlotDamageScaling;
import com.thedndpub.data.mechanics.model.SpellSlotLeapLimit;
import com.thedndpub.data.mechanics.model.SpellTargetSubject;
import com.thedndpub.data.mechanics.model.TargetScope;
import com.thedndpub.data.mechanics.model.TextMechanicsEvidence;
import com.thedndpub.data.mechanics.model.UnsupportedMechanicsConstruct;
import com.thedndpub.data.mechanics.model.UnsupportedReason;
import com.thedndpub.data.dto.spell.SpellDefinition;

import java.util.List;

/** Source-bounded interpretation of XPHB Chromatic Orb's damage-dice-triggered chain. */
final class XphbChromaticOrbRules implements SpellMechanicsPattern {
    static final String OWNER_ID = "spell:xphb:chromatic-orb";
    private static final String BODY = "/entries/0";
    private static final String CHAIN_TEXT = "/entries/1";
    private static final String HIGHER_LEVEL = "/entriesHigherLevel/0/entries/0";
    private static final String ORB_TARGET = "You hurl an orb of energy at a target within range.";
    private static final String CHOICE = "Choose Acid, Cold, Fire, Lightning, Poison, or Thunder for the type of orb you create,";
    private static final String INITIAL_ATTACK = "and then make a ranged spell attack against the target.";
    private static final String CHOICE_SEQUENCE = CHOICE + " " + INITIAL_ATTACK;
    private static final String INITIAL_DAMAGE = "On a hit, the target takes {@damage 3d8} damage of the chosen type.";
    private static final String MATCHING_DICE = "If you roll the same number on two or more of the d8s,";
    private static final String LEAP_TARGET = "the orb leaps to a different target of your choice within 30 feet of the target.";
    private static final String FOLLOWUP_ROLL = "Make an attack roll against the new target, and make a new damage roll.";
    private static final String LEVEL_TWO = "The orb can't leap again unless you cast the spell with a level 2+ spell slot.";
    private static final String SCALING = "The damage increases by {@scaledamage 3d8|1-9|1d8} for each spell slot level above 1.";
    private static final String LEAP_LIMIT = "The orb can leap a maximum number of times equal to the level of the slot expended,";
    private static final String TARGET_ONCE = "and a creature can be targeted only once by each casting of this spell.";

    @Override
    public String patternId() {
        return "xphb-chromatic-orb-conditional-chain-v1";
    }

    @Override
    public boolean supports(SpellDefinition spell) {
        return OWNER_ID.equals(spell.id().value()) && "XPHB".equals(spell.source().identity().code());
    }

    @Override
    public MechanicsDocument extract(
            SpellDefinition spell, SpellMechanicsEvidenceSource source, MechanicsDocument original) {
        if (!supports(spell) || original.processing().state() != ProcessingState.NOT_ATTEMPTED) {
            throw new IllegalArgumentException("Only the pinned XPHB Chromatic Orb owner is supported");
        }

        TextMechanicsEvidence targetText = source.textEvidence(BODY, ORB_TARGET);
        TextMechanicsEvidence choiceText = source.textEvidence(BODY, CHOICE);
        TextMechanicsEvidence choiceSequenceText = source.textEvidence(BODY, CHOICE_SEQUENCE);
        TextMechanicsEvidence attackText = source.textEvidence(BODY, INITIAL_ATTACK);
        TextMechanicsEvidence damageText = source.textEvidence(BODY, INITIAL_DAMAGE);
        TextMechanicsEvidence matchingText = source.textEvidence(CHAIN_TEXT, MATCHING_DICE);
        TextMechanicsEvidence leapTargetText = source.textEvidence(CHAIN_TEXT, LEAP_TARGET);
        TextMechanicsEvidence followupText = source.textEvidence(CHAIN_TEXT, FOLLOWUP_ROLL);
        TextMechanicsEvidence levelTwoText = source.textEvidence(CHAIN_TEXT, LEVEL_TWO);
        TextMechanicsEvidence scalingText = source.textEvidence(HIGHER_LEVEL, SCALING);
        TextMechanicsEvidence limitText = source.textEvidence(HIGHER_LEVEL, LEAP_LIMIT);
        TextMechanicsEvidence targetOnceText = source.textEvidence(HIGHER_LEVEL, TARGET_ONCE);
        TextMechanicsEvidence formulaText = source.textEvidence(BODY, "{@damage 3d8}");

        var castingTime = source.fieldEvidence("/time/0");
        var castingRange = source.fieldEvidence("/range");
        var spellAttack = source.fieldEvidence("/spellAttack/0");
        var damageTypes = source.fieldEvidence("/damageInflict");
        List<com.thedndpub.data.mechanics.model.MechanicsEvidence> evidence = source.evidence();
        String root = source.source().jsonPointer();

        MechanicRecord attack = claim(source, "attack-roll", "chromatic-orb-attack-v1",
                new AttackRollMechanic(AttackKind.RANGED_SPELL, AttackActorRole.CASTER,
                        1, false, AttackActivationTiming.CAST_TIME),
                List.of(association(attackText, "/payload", EvidenceRole.EFFECT_ROLE),
                        association(targetText, "/context/subject", EvidenceRole.SUBJECT),
                        association(castingTime, "/payload/activationTiming", EvidenceRole.TRIGGER),
                        association(spellAttack, "/payload/attackKind", EvidenceRole.VALUE)),
                evidence, new MechanicContext(
                        new SpellTargetSubject(TargetScope.SINGLE_TARGET_UNSPECIFIED_CATEGORY), List.of()),
                MechanicsCompleteness.PARTIAL);
        MechanicRevisionReference attackReference = attack.revision().reference(attack.id());

        MechanicRecord damage = claim(source, "damage", "chromatic-orb-damage-v1",
                new DamageMechanic(new DiceAmount(3, 8, 0), new DamageTypeChoice(
                        List.of("acid", "cold", "fire", "lightning", "poison", "thunder"), 1), null,
                        new SpellSlotDamageScaling(new DiceAmount(1, 8, 0), 1, 2, 1)),
                List.of(association(damageText, "/payload", EvidenceRole.EFFECT_ROLE),
                        association(formulaText, "/payload/amount", EvidenceRole.VALUE),
                        association(choiceText, "/payload/damageType", EvidenceRole.VALUE),
                        association(damageTypes, "/payload/damageType/alternatives", EvidenceRole.VALUE),
                        association(scalingText, "/payload/spellSlotScaling", EvidenceRole.VALUE),
                        association(damageText, "/context/bindings/0", EvidenceRole.TRIGGER)),
                evidence, new MechanicContext(new SpellTargetSubject(TargetScope.SINGLE_TARGET_UNSPECIFIED_CATEGORY),
                        List.of(new AppliesOnAttackRollOutcome(attackReference, AttackRollOutcome.HIT,
                                List.of(damageText.id())))), MechanicsCompleteness.COMPLETE_WITHIN_CLAIM);
        MechanicRevisionReference damageReference = damage.revision().reference(damage.id());

        MechanicRecord chain = claim(source, "conditional-chain", "chromatic-orb-chain-v1",
                new ConditionalAttackChainMechanic(attackReference, damageReference,
                        new DamageDiceMatchRule(damageReference, 8, 2, DamageDicePopulation.SPELL_DAMAGE_DICE_IN_ONE_ROLL,
                                ExternalDamageDiceTreatment.UNSPECIFIED_BY_SOURCE),
                        true, ChainTargetCategory.UNSPECIFIED_IN_SOURCE,
                        ChainTargetReusePolicy.DIFFERENT_FROM_PREVIOUS_AND_CREATURES_ONCE_PER_CASTING,
                        new ChainTargetDistance(ChainDistanceOrigin.PREVIOUS_TARGET, 30),
                        new SpellSlotLeapLimit(1, 1, 1, 1), DamageTypeChoiceTiming.WHEN_CASTING,
                        true, true, AdditionalAttackMissBehavior.UNSPECIFIED_IN_SOURCE, false),
                List.of(association(matchingText, "/payload/continuationTrigger", EvidenceRole.TRIGGER),
                        association(formulaText, "/payload/continuationTrigger/damage", EvidenceRole.QUALIFIER),
                        association(matchingText, "/payload/atMostOneLeapPerQualifyingRoll", EvidenceRole.QUALIFIER),
                        association(attackText, "/payload/initialAttack", EvidenceRole.TRIGGER),
                        association(followupText, "/payload/eachNewTargetRequiresAttackRoll", EvidenceRole.TRIGGER),
                        association(damageText, "/payload", EvidenceRole.EFFECT_ROLE),
                        association(damageText, "/payload/reusableDamage", EvidenceRole.QUALIFIER),
                        association(leapTargetText, "/payload/subsequentTargetCategory", EvidenceRole.SUBJECT),
                        association(leapTargetText, "/payload/targetDistance", EvidenceRole.QUALIFIER),
                        association(targetOnceText, "/payload/targetReusePolicy", EvidenceRole.QUALIFIER),
                        association(choiceSequenceText, "/payload/damageTypeChoiceTiming", EvidenceRole.TRIGGER),
                        association(choiceText, "/payload/damageTypePersistsAcrossLeaps", EvidenceRole.QUALIFIER),
                        association(leapTargetText, "/payload/damageTypePersistsAcrossLeaps", EvidenceRole.QUALIFIER),
                        association(levelTwoText, "/payload/leapLimit", EvidenceRole.QUALIFIER),
                        association(limitText, "/payload/leapLimit", EvidenceRole.VALUE),
                        association(limitText, "/payload/maximumLeapsCountsInitialAttack", EvidenceRole.QUALIFIER),
                        association(followupText, "/payload/additionalAttackMissBehavior", EvidenceRole.QUALIFIER)),
                evidence, new MechanicContext(
                        new SpellTargetSubject(TargetScope.SINGLE_TARGET_UNSPECIFIED_CATEGORY), List.of()),
                MechanicsCompleteness.PARTIAL);

        List<UnsupportedMechanicsConstruct> unsupported = List.of(
                new UnsupportedMechanicsConstruct("chromatic-orb-unsupported:attack-range", MechanicFamily.ATTACK_ROLL,
                        UnsupportedReason.FAMILY_NOT_IMPLEMENTED,
                        List.of(targetText.id(), castingRange.id()), List.of()),
                new UnsupportedMechanicsConstruct("chromatic-orb-unsupported:external-damage-dice",
                        MechanicFamily.CONDITIONAL_ATTACK_CHAIN, UnsupportedReason.SOURCE_UNSPECIFIED,
                        List.of(matchingText.id(), formulaText.id()), List.of()),
                new UnsupportedMechanicsConstruct("chromatic-orb-unsupported:additional-attack-miss",
                        MechanicFamily.CONDITIONAL_ATTACK_CHAIN, UnsupportedReason.SOURCE_UNSPECIFIED,
                        List.of(followupText.id(), matchingText.id()), List.of()),
                new UnsupportedMechanicsConstruct("chromatic-orb-unsupported:target-category",
                        MechanicFamily.CONDITIONAL_ATTACK_CHAIN, UnsupportedReason.SOURCE_UNSPECIFIED,
                        List.of(targetText.id(), leapTargetText.id(), targetOnceText.id()), List.of()));
        return MechanicsDocument.create(source.owner(), 1, List.of(source.source().revision()),
                new MechanicsProcessing(ProcessingState.PROCESSED, "xphb-chromatic-orb", "1"),
                List.of(attack, damage, chain), original.observations(), unsupported, evidence,
                new MechanicsCoverage(CoverageStatus.PARTIAL, "xphb-chromatic-orb", "1",
                        List.of(root + "/time", root + "/range", root + "/duration", root + "/entries",
                                root + "/entriesHigherLevel", root + "/spellAttack", root + "/damageInflict"),
                        List.of(), unsupported.size(), CoverageScopeExtent.DECLARED_SUBSET));
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
                DerivationMethod.BOUNDED_CONTEXT_INTERPRETATION, pattern, "1", "xphb-chromatic-orb-1",
                List.of(), List.of(source.source().revision()));
        var assessment = new MechanicsAssessment(completeness, MechanicsSupport.BOUNDED_CONTEXT,
                List.of(), List.of());
        return MechanicRecord.propose(MechanicId.of(source.owner(), slot), payload, context, associations,
                derivation, assessment);
    }

    private static MechanicEvidenceAssociation association(
            com.thedndpub.data.mechanics.model.MechanicsEvidence evidence, String path, EvidenceRole role) {
        return MechanicEvidenceAssociation.of(evidence, path, role);
    }
}
