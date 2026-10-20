package com.thedndpub.data.mechanics.spell;

import com.thedndpub.data.dte.common.entry.TextEntryDte;
import com.thedndpub.data.dto.spell.SpellDefinition;
import com.thedndpub.data.mechanics.model.AppliesForDuration;
import com.thedndpub.data.mechanics.model.AppliesOnAttackRollOutcome;
import com.thedndpub.data.mechanics.model.AppliesOnEachAttackRollOutcome;
import com.thedndpub.data.mechanics.model.AppliesWhen;
import com.thedndpub.data.mechanics.model.ApplicabilityCondition;
import com.thedndpub.data.mechanics.model.AttackActivationTiming;
import com.thedndpub.data.mechanics.model.AttackActorRole;
import com.thedndpub.data.mechanics.model.AttackKind;
import com.thedndpub.data.mechanics.model.AttackRollMechanic;
import com.thedndpub.data.mechanics.model.AttackRollModifier;
import com.thedndpub.data.mechanics.model.AttackRollModifierMechanic;
import com.thedndpub.data.mechanics.model.AttackRollOutcome;
import com.thedndpub.data.mechanics.model.AttackRollInstanceScope;
import com.thedndpub.data.mechanics.model.AttackRollParticipant;
import com.thedndpub.data.mechanics.model.FutureAttackRollSelector;
import com.thedndpub.data.mechanics.model.CharacterLevelDamageScaling;
import com.thedndpub.data.mechanics.model.DiceAmount;
import com.thedndpub.data.mechanics.model.DamageTiming;
import com.thedndpub.data.mechanics.model.DamageMechanic;
import com.thedndpub.data.mechanics.model.EvidenceRole;
import com.thedndpub.data.mechanics.model.FixedDamageType;
import com.thedndpub.data.mechanics.model.FractionOfAmount;
import com.thedndpub.data.mechanics.model.HitPointRecoveryPreventionMechanic;
import com.thedndpub.data.mechanics.model.IntegerAmount;
import com.thedndpub.data.mechanics.model.MechanicContext;
import com.thedndpub.data.mechanics.model.MechanicContextBinding;
import com.thedndpub.data.mechanics.model.MechanicEvidenceAssociation;
import com.thedndpub.data.mechanics.model.MechanicFamily;
import com.thedndpub.data.mechanics.model.MechanicRecord;
import com.thedndpub.data.mechanics.model.CharacterLevelInstanceCount;
import com.thedndpub.data.mechanics.model.CharacterLevelInstanceCountTier;
import com.thedndpub.data.mechanics.model.InstanceCountProgression;
import com.thedndpub.data.mechanics.model.InstanceTargetAllocation;
import com.thedndpub.data.mechanics.model.MechanicRevisionReference;
import com.thedndpub.data.mechanics.model.MechanicsDocument;
import com.thedndpub.data.mechanics.model.ModifiedStatistic;
import com.thedndpub.data.mechanics.model.ModifierOperation;
import com.thedndpub.data.mechanics.model.ProcessingState;
import com.thedndpub.data.mechanics.model.ReferencedAttackRollSelector;
import com.thedndpub.data.mechanics.model.ReactionPreventionMechanic;
import com.thedndpub.data.mechanics.model.SpellTargetSubject;
import com.thedndpub.data.mechanics.model.SpellSlotDamageScaling;
import com.thedndpub.data.mechanics.model.SpellSlotInstanceCount;
import com.thedndpub.data.mechanics.model.StatisticModifierMechanic;
import com.thedndpub.data.mechanics.model.StructuredFieldMechanicsEvidence;
import com.thedndpub.data.mechanics.model.TargetScope;
import com.thedndpub.data.mechanics.model.TargetCategory;
import com.thedndpub.data.mechanics.model.TargetReusePolicy;
import com.thedndpub.data.mechanics.model.TargetSelectionCardinality;
import com.thedndpub.data.mechanics.model.TargetCategoryEligibilityStatus;
import com.thedndpub.data.mechanics.model.UnknownSubject;
import com.thedndpub.data.mechanics.model.UnknownValueReason;
import com.thedndpub.data.mechanics.model.TextMechanicsEvidence;
import com.thedndpub.data.mechanics.model.TurnActor;
import com.thedndpub.data.mechanics.model.TurnBoundary;
import com.thedndpub.data.mechanics.model.UntilTurnBoundary;
import com.thedndpub.data.mechanics.model.UnsupportedReason;
import com.thedndpub.data.mechanics.syntax.MarkupTag;
import com.thedndpub.data.mechanics.syntax.BalancedMarkupParser;
import com.thedndpub.data.mechanics.syntax.MarkupSyntaxException;

import java.util.List;
import java.util.Set;

/** Bounded PHB rules for reviewed, single cast-time spell attacks. */
final class PhbSpellAttackRules {
    static final Set<String> PRODUCTION_OWNERS = Set.of(
            "spell:phb:inflict-wounds", "spell:phb:ray-of-frost", "spell:phb:fire-bolt",
            "spell:phb:chill-touch", "spell:phb:shocking-grasp", "spell:phb:eldritch-blast",
            "spell:phb:scorching-ray", "spell:phb:melf-s-acid-arrow");

    private static final String BODY = "/entries/0";
    private static final String INFLECT_WOUNDS = "spell:phb:inflict-wounds";
    private static final String RAY_OF_FROST = "spell:phb:ray-of-frost";
    private static final String FIRE_BOLT = "spell:phb:fire-bolt";
    private static final String CHILL_TOUCH = "spell:phb:chill-touch";
    private static final String SHOCKING_GRASP = "spell:phb:shocking-grasp";
    private static final String ELDRITCH_BLAST = "spell:phb:eldritch-blast";
    private static final String SCORCHING_RAY = "spell:phb:scorching-ray";
    private static final String MELFS_ACID_ARROW = "spell:phb:melf-s-acid-arrow";
    private static final Rule INFLECT_WOUNDS_RULE = new Rule(
            "phb-inflict-wounds-attack-roll",
            "attack-roll",
            "damage",
            "inflict-wounds-attack-roll-v1",
            "inflict-wounds-hit-damage-v1",
            AttackKind.MELEE_SPELL,
            "M",
            "necrotic",
            "Make a melee spell attack against a creature you can reach",
            "the target takes {@damage 3d10} necrotic damage",
            "the target",
            null,
            "On a hit",
            new SpellTargetSubject(TargetScope.SINGLE_TARGET_CREATURE),
            null,
            null,
            1,
            null);
    private static final Rule RAY_OF_FROST_RULE = new Rule(
            "phb-ray-of-frost-attack-roll",
            "attack-roll",
            "damage",
            "ray-of-frost-attack-roll-v1",
            "ray-of-frost-hit-damage-v1",
            AttackKind.RANGED_SPELL,
            "R",
            "cold",
            "Make a ranged spell attack against the target",
            "it takes {@damage 1d8} cold damage",
            "a creature within range",
            "a creature within range",
            "On a hit",
            new SpellTargetSubject(TargetScope.SINGLE_TARGET_CREATURE),
            "cold damage",
            "ray-of-frost-cantrip-scaling",
            2,
            "/entries/1");
    private static final Rule FIRE_BOLT_RULE = new Rule(
            "phb-fire-bolt-pilot",
            "attack-roll",
            "damage",
            "phb-fire-bolt-attack-roll-v1",
            "phb-fire-bolt-hit-damage-v1",
            AttackKind.RANGED_SPELL,
            "R",
            "fire",
            "Make a ranged spell attack against the target",
            "the target takes {@damage 1d10} fire damage",
            "creature or object within range",
            "creature or object within range",
            "On a hit",
            new SpellTargetSubject(TargetScope.SINGLE_TARGET,
                    List.of(TargetCategory.CREATURE, TargetCategory.OBJECT)),
            "fire damage",
            "fire-bolt-cantrip-scaling",
            2,
            "/entries/1");
    private static final Rule CHILL_TOUCH_RULE = new Rule(
            "phb-chill-touch-attack-roll",
            "attack-roll",
            "damage",
            "chill-touch-attack-roll-v1",
            "chill-touch-hit-damage-v1",
            AttackKind.RANGED_SPELL,
            "R",
            "necrotic",
            "Make a ranged spell attack against the creature",
            "the target takes {@damage 1d8} necrotic damage",
            "a creature within range",
            "a creature within range",
            "On a hit",
            new SpellTargetSubject(TargetScope.SINGLE_TARGET_CREATURE),
            "necrotic damage",
            "chill-touch-cantrip-scaling",
            3,
            "/entries/2");
    private static final Rule SHOCKING_GRASP_RULE = new Rule(
            "phb-shocking-grasp-attack-roll",
            "attack-roll",
            "damage",
            "shocking-grasp-attack-roll-v1",
            "shocking-grasp-hit-damage-v1",
            AttackKind.MELEE_SPELL,
            "M",
            "lightning",
            "Make a melee spell attack against the target",
            "the target takes {@damage 1d8} lightning damage",
            "a creature you try to touch",
            "a creature you try to touch",
            "On a hit",
            new SpellTargetSubject(TargetScope.SINGLE_TARGET_CREATURE),
            "lightning damage",
            "shocking-grasp-cantrip-scaling",
            2,
            "/entries/1");

    private PhbSpellAttackRules() {
    }

    static MechanicsDocument extract(
            SpellDefinition spell,
            SpellMechanicsEvidenceSource source,
            MechanicsDocument original) {
        if (spell.id().value().equals(SCORCHING_RAY)) {
            return extractScorchingRay(spell, source, original);
        }
        if (spell.id().value().equals(MELFS_ACID_ARROW)) {
            return extractMelfsAcidArrow(spell, source, original);
        }
        if (spell.id().value().equals(ELDRITCH_BLAST)) {
            return extractEldritchBlast(spell, source, original);
        }
        Rule rule = ruleFor(spell.id().value());
        require(rule != null, BODY);
        require(original.owner().equals(source.owner())
                        && original.owner().id().equals(spell.id().value())
                        && original.sources().equals(List.of(source.source().revision())),
                BODY);
        require(original.processing().state() == ProcessingState.NOT_ATTEMPTED, BODY);
        require(spell.entries().size() == rule.expectedEntryCount()
                        && spell.entries().stream().allMatch(TextEntryDte.class::isInstance),
                BODY);
        require(spell.mechanics().spellAttacks().equals(List.of(rule.attackMarker())), "/spellAttack");
        require(spell.mechanics().damageInflicted().equals(List.of(rule.damageType())), "/damageInflict");

        SpellMechanicsAssembly assembly = new SpellMechanicsAssembly(source, original, rule.adapterId());
        MechanicRecord attackRoll = assembly.attempt(rule.attackPattern(), MechanicFamily.ATTACK_ROLL,
                () -> attackRoll(rule, source, assembly));
        assembly.attempt(rule.damagePattern(), MechanicFamily.DAMAGE,
                () -> hitDamage(spell, rule, source, assembly, attackRoll));

        if (spell.id().value().equals(RAY_OF_FROST)) {
            assembly.attempt("ray-of-frost-hit-speed-reduction-v1", MechanicFamily.MODIFIER,
                    () -> rayOfFrostSpeedReduction(source, assembly, attackRoll));
        }
        if (spell.id().value().equals(FIRE_BOLT)) {
            fireBoltObjectIgnition(source, assembly);
        }
        if (spell.id().value().equals(CHILL_TOUCH)) {
            assembly.attempt("chill-touch-recovery-prevention-v1", MechanicFamily.HEALING_PREVENTION,
                    () -> chillTouchRecoveryPrevention(source, assembly, attackRoll));
            assembly.attempt("chill-touch-undead-attack-disadvantage-v1", MechanicFamily.ATTACK_ROLL_MODIFIER,
                    () -> chillTouchUndeadDisadvantage(source, assembly, attackRoll));
        }
        if (spell.id().value().equals(SHOCKING_GRASP)) {
            assembly.attempt("shocking-grasp-reaction-prevention-v1", MechanicFamily.REACTION_RESTRICTION,
                    () -> shockingGraspReactionPrevention(source, assembly, attackRoll));
            assembly.attempt("shocking-grasp-metal-armor-advantage-v1", MechanicFamily.ATTACK_ROLL_MODIFIER,
                    () -> shockingGraspMetalArmorAdvantage(source, assembly, attackRoll));
        }

        String root = source.source().jsonPointer();
        List<String> considered = spell.id().value().equals(INFLECT_WOUNDS)
                ? List.of(root + "/spellAttack", root + "/damageInflict", root + "/entries",
                        root + "/entriesHigherLevel")
                : List.of(root + "/spellAttack", root + "/damageInflict", root + "/entries",
                        root + "/scalingLevelDice");
        List<String> unassessed = spell.id().value().equals(INFLECT_WOUNDS)
                ? List.of(root + "/entriesHigherLevel") : List.of();
        return assembly.finish(considered, unassessed);
    }

    private static MechanicsDocument extractScorchingRay(
            SpellDefinition spell,
            SpellMechanicsEvidenceSource source,
            MechanicsDocument original) {
        require(original.owner().equals(source.owner())
                        && original.owner().id().equals(SCORCHING_RAY)
                        && original.sources().equals(List.of(source.source().revision())),
                BODY);
        require(original.processing().state() == ProcessingState.NOT_ATTEMPTED, BODY);
        require(spell.entries().size() == 2
                        && spell.entries().stream().allMatch(TextEntryDte.class::isInstance),
                "/entries");
        require(spell.mechanics().spellAttacks().equals(List.of("R")), "/spellAttack");
        require(spell.mechanics().damageInflicted().equals(List.of("fire")), "/damageInflict");

        UnknownSubject target = new UnknownSubject(UnknownValueReason.NOT_AUTHORED);
        InstanceCountProgression progression = new SpellSlotInstanceCount(2, 3, 1);
        InstanceTargetAllocation allocation = new InstanceTargetAllocation(
                TargetSelectionCardinality.EXACTLY_ONE_PER_INSTANCE,
                null,
                TargetReusePolicy.REUSE_ALLOWED,
                true,
                TargetCategoryEligibilityStatus.UNSPECIFIED_IN_SOURCE);
        SpellMechanicsAssembly assembly = new SpellMechanicsAssembly(source, original,
                "phb-scorching-ray-multi-attack");
        MechanicRecord attack = assembly.attempt("scorching-ray-attack-roll-v1", MechanicFamily.ATTACK_ROLL,
                () -> scorchingRayAttack(source, assembly, target, progression, allocation));
        assembly.attempt("scorching-ray-hit-damage-v1", MechanicFamily.DAMAGE,
                () -> scorchingRayDamage(source, assembly, target, attack));

        TextMechanicsEvidence targetCategoryText = source.textEvidence(BODY, "targets within range");
        assembly.unsupported("scorching-ray-target-category-unspecified", MechanicFamily.ATTACK_ROLL,
                UnsupportedReason.SOURCE_UNSPECIFIED, List.of(targetCategoryText.id()));
        String root = source.source().jsonPointer();
        return assembly.finish(List.of(root + "/spellAttack", root + "/damageInflict", root + "/entries",
                        root + "/entriesHigherLevel"),
                List.of(root + "/entries"));
    }

    private static MechanicsDocument extractMelfsAcidArrow(
            SpellDefinition spell,
            SpellMechanicsEvidenceSource source,
            MechanicsDocument original) {
        String root = source.source().jsonPointer();
        require(original.owner().equals(source.owner())
                        && original.owner().id().equals(MELFS_ACID_ARROW)
                        && original.sources().equals(List.of(source.source().revision())), BODY);
        require(original.processing().state() == ProcessingState.NOT_ATTEMPTED, BODY);
        require(spell.level() == 2 && spell.entries().size() == 1,
                "/level");
        require(spell.mechanics().spellAttacks().equals(List.of("R")), "/spellAttack");
        require(spell.mechanics().damageInflicted().equals(List.of("acid")), "/damageInflict");

        SpellTargetSubject target = new SpellTargetSubject(TargetScope.SINGLE_TARGET_UNSPECIFIED_CATEGORY);
        SpellMechanicsAssembly assembly = new SpellMechanicsAssembly(source, original,
                "phb-melfs-acid-arrow-outcome-branches");
        MechanicRecord attack = assembly.attempt("melfs-acid-arrow-attack-roll-v1", MechanicFamily.ATTACK_ROLL,
                () -> melfsAcidArrowAttack(source, assembly, target));
        assembly.attemptMany("melfs-acid-arrow-damage-branches-v1", MechanicFamily.DAMAGE,
                () -> melfsAcidArrowDamageBranches(source, assembly, target, attack));

        return assembly.finish(List.of(
                        root + "/spellAttack", root + "/damageInflict", root + "/range", root + "/entries",
                        root + "/entriesHigherLevel"),
                List.of(root + "/entries", root + "/entriesHigherLevel"));
    }

    private static MechanicRecord melfsAcidArrowAttack(
            SpellMechanicsEvidenceSource source,
            SpellMechanicsAssembly assembly,
            SpellTargetSubject target) {
        TextMechanicsEvidence attackText = source.textEvidence(BODY,
                "Make a ranged spell attack against the target.");
        TextMechanicsEvidence targetText = source.textEvidence(BODY, "a target within range");
        StructuredFieldMechanicsEvidence attackKind = source.fieldEvidence("/spellAttack/0");
        return assembly.claim("attack-roll", "melfs-acid-arrow-attack-roll-v1",
                new AttackRollMechanic(AttackKind.RANGED_SPELL, AttackActorRole.CASTER, 1, true,
                        AttackActivationTiming.CAST_TIME),
                new MechanicContext(target, List.of()),
                List.of(
                        MechanicEvidenceAssociation.of(attackText, "/payload", EvidenceRole.EFFECT_ROLE),
                        MechanicEvidenceAssociation.of(attackKind, "/payload/attackKind", EvidenceRole.VALUE),
                        MechanicEvidenceAssociation.of(targetText, "/context/subject", EvidenceRole.SUBJECT)),
                false);
    }

    private static List<MechanicRecord> melfsAcidArrowDamageBranches(
            SpellMechanicsEvidenceSource source,
            SpellMechanicsAssembly assembly,
            SpellTargetSubject target,
            MechanicRecord attack) {
        require(attack != null, BODY);
        List<MarkupTag> formulas = formulas(source, BODY, "damage", 2);
        require(formulas.get(0).arguments().equals(List.of("4d4"))
                        && formulas.get(1).arguments().equals(List.of("2d4")), BODY);
        String missPhrase = "On a miss, the arrow splashes the target with acid for half as much of the "
                + "initial damage and no damage at the end of its next turn.";
        TextMechanicsEvidence hitText = source.textEvidence(BODY, "On a hit");
        TextMechanicsEvidence immediateText = source.textEvidence(BODY,
                "the target takes {@damage 4d4} acid damage immediately");
        TextMechanicsEvidence immediateFormula = source.textEvidence(BODY, formulas.get(0).originalText());
        TextMechanicsEvidence delayedText = source.textEvidence(BODY,
                "{@damage 2d4} acid damage at the end of its next turn");
        TextMechanicsEvidence delayedFormula = source.textEvidence(BODY, formulas.get(1).originalText());
        TextMechanicsEvidence timingText = source.textEvidence(BODY,
                "and {@damage 2d4} acid damage at the end of its next turn");
        TextMechanicsEvidence missText = source.textEvidence(BODY, missPhrase);
        TextMechanicsEvidence missTriggerText = source.textEvidence(BODY, "On a miss");
        TextMechanicsEvidence initialDamageReferenceText = source.textEvidence(BODY, "initial damage");
        TextMechanicsEvidence noDelayedMissText = source.textEvidence(BODY,
                "no damage at the end of its next turn");
        TextMechanicsEvidence higherText = source.textEvidence(
                "/entriesHigherLevel/0/entries/0",
                "the damage (both initial and later) increases by {@scaledamage 4d4;2d4|2-9|1d4} "
                        + "for each slot level above 2nd");
        StructuredFieldMechanicsEvidence damageType = source.fieldEvidence("/damageInflict/0");
        TextMechanicsEvidence rangeTarget = source.textEvidence(BODY, "a target within range");

        SpellSlotDamageScaling scaling = new SpellSlotDamageScaling(new DiceAmount(1, 4, 0), 2, 3, 1);
        var hitBinding = new AppliesOnAttackRollOutcome(
                attack.revision().reference(attack.id()), AttackRollOutcome.HIT, List.of(hitText.id()));
        var missBinding = new AppliesOnAttackRollOutcome(
                attack.revision().reference(attack.id()), AttackRollOutcome.MISS,
                List.of(missTriggerText.id(), missText.id(), noDelayedMissText.id()));
        MechanicRecord immediate = assembly.claim("damage-on-hit", "melfs-acid-arrow-damage-on-hit-v1",
                new DamageMechanic(new DiceAmount(4, 4, 0), new FixedDamageType("acid"), null, scaling,
                        DamageTiming.IMMEDIATE),
                new MechanicContext(target, List.of(hitBinding)),
                List.of(
                        MechanicEvidenceAssociation.of(immediateText, "/payload", EvidenceRole.EFFECT_ROLE),
                        MechanicEvidenceAssociation.of(immediateFormula, "/payload/amount", EvidenceRole.VALUE),
                        MechanicEvidenceAssociation.of(damageType, "/payload/damageType", EvidenceRole.VALUE),
                        MechanicEvidenceAssociation.of(hitText, "/context/bindings/0", EvidenceRole.TRIGGER),
                        MechanicEvidenceAssociation.of(rangeTarget, "/context/subject", EvidenceRole.SUBJECT),
                        MechanicEvidenceAssociation.of(higherText, "/payload/spellSlotScaling", EvidenceRole.SCALING)),
                false);
        require(immediate != null, BODY);

        MechanicRecord delayed = assembly.claim("delayed-damage-on-hit",
                "melfs-acid-arrow-delayed-damage-on-hit-v1",
                new DamageMechanic(new DiceAmount(2, 4, 0), new FixedDamageType("acid"), null, scaling,
                        DamageTiming.END_OF_TARGET_NEXT_TURN),
                new MechanicContext(target, List.of(hitBinding)),
                List.of(
                        MechanicEvidenceAssociation.of(delayedText, "/payload", EvidenceRole.EFFECT_ROLE),
                        MechanicEvidenceAssociation.of(delayedFormula, "/payload/amount", EvidenceRole.VALUE),
                        MechanicEvidenceAssociation.of(damageType, "/payload/damageType", EvidenceRole.VALUE),
                        MechanicEvidenceAssociation.of(hitText, "/context/bindings/0", EvidenceRole.TRIGGER),
                        MechanicEvidenceAssociation.of(rangeTarget, "/context/subject", EvidenceRole.SUBJECT),
                        MechanicEvidenceAssociation.of(timingText, "/payload/timing", EvidenceRole.DURATION),
                        MechanicEvidenceAssociation.of(higherText, "/payload/spellSlotScaling", EvidenceRole.SCALING)),
                false);
        MechanicRevisionReference initialDamage = immediate.revision().reference(immediate.id());
        MechanicRecord miss = assembly.claim("damage-on-miss", "melfs-acid-arrow-damage-on-miss-v1",
                new DamageMechanic(new FractionOfAmount(1, 2, initialDamage), new FixedDamageType("acid"),
                        null, null, DamageTiming.IMMEDIATE),
                new MechanicContext(target, List.of(missBinding)),
                List.of(
                        MechanicEvidenceAssociation.of(missText, "/payload", EvidenceRole.EFFECT_ROLE),
                        MechanicEvidenceAssociation.of(missText, "/payload/amount", EvidenceRole.VALUE),
                        MechanicEvidenceAssociation.of(initialDamageReferenceText,
                                "/payload/amount/baseAmount", EvidenceRole.REFERENCE),
                        MechanicEvidenceAssociation.of(damageType, "/payload/damageType", EvidenceRole.VALUE),
                        MechanicEvidenceAssociation.of(missTriggerText,
                                "/context/bindings/0", EvidenceRole.TRIGGER),
                        MechanicEvidenceAssociation.of(missText, "/context/bindings/0", EvidenceRole.QUALIFIER),
                        MechanicEvidenceAssociation.of(noDelayedMissText,
                                "/context/bindings/0/outcome", EvidenceRole.COUNTEREVIDENCE),
                        MechanicEvidenceAssociation.of(rangeTarget, "/context/subject", EvidenceRole.SUBJECT)),
                false);
        return List.of(immediate, delayed, miss);
    }

    private static List<MarkupTag> formulas(
            SpellMechanicsEvidenceSource source, String pointer, String name, int expectedCount) {
        try {
            List<MarkupTag> tags = new BalancedMarkupParser().parse(source.text(pointer)).tags().stream()
                    .filter(tag -> tag.name().equals(name)).toList();
            require(tags.size() == expectedCount && tags.stream().allMatch(tag ->
                    tag.arguments().size() == 1 && tag.children().isEmpty()), pointer);
            for (MarkupTag tag : tags) {
                dice(tag, pointer);
            }
            return tags;
        } catch (MarkupSyntaxException exception) {
            throw new SpellPatternMismatch(UnsupportedReason.UNSUPPORTED_SYNTAX, pointer);
        }
    }

    private static MechanicRecord scorchingRayAttack(
            SpellMechanicsEvidenceSource source,
            SpellMechanicsAssembly assembly,
            UnknownSubject target,
            InstanceCountProgression progression,
            InstanceTargetAllocation allocation) {
        TextMechanicsEvidence baseCount = source.textEvidence(BODY, "three rays of fire");
        TextMechanicsEvidence casterText = source.textEvidence(BODY, "You create three rays of fire");
        TextMechanicsEvidence allocationText = source.textEvidence(BODY, "at one target or several");
        TextMechanicsEvidence rangeText = source.textEvidence(BODY, "targets within range");
        TextMechanicsEvidence independentAttack = source.textEvidence("/entries/1",
                "Make a ranged spell attack for each ray");
        TextMechanicsEvidence slotProgression = source.textEvidence("/entriesHigherLevel/0/entries/0",
                "one additional ray for each slot level above 2nd");
        TextMechanicsEvidence slotActivation = source.textEvidence("/entriesHigherLevel/0/entries/0",
                "When you cast this spell using a spell slot of 3rd level or higher");
        StructuredFieldMechanicsEvidence attackKind = source.fieldEvidence("/spellAttack/0");
        StructuredFieldMechanicsEvidence baseSpellLevel = source.fieldEvidence("/level");
        StructuredFieldMechanicsEvidence castTime = source.fieldEvidence("/time/0");
        List<MechanicEvidenceAssociation> associations = List.of(
                MechanicEvidenceAssociation.of(independentAttack, "/payload", EvidenceRole.EFFECT_ROLE),
                MechanicEvidenceAssociation.of(attackKind, "/payload/attackKind", EvidenceRole.VALUE),
                MechanicEvidenceAssociation.of(casterText, "/payload/actorRole", EvidenceRole.SUBJECT),
                MechanicEvidenceAssociation.of(baseCount, "/payload/attackCount", EvidenceRole.VALUE),
                MechanicEvidenceAssociation.of(independentAttack,
                        "/payload/attacksIndependent", EvidenceRole.VALUE),
                MechanicEvidenceAssociation.of(slotProgression,
                        "/payload/countProgression/baseSpellLevel", EvidenceRole.SCALING),
                MechanicEvidenceAssociation.of(baseSpellLevel,
                        "/payload/countProgression/baseSpellLevel", EvidenceRole.VALUE),
                MechanicEvidenceAssociation.of(baseCount,
                        "/payload/countProgression/baseInstanceCount", EvidenceRole.SCALING),
                MechanicEvidenceAssociation.of(slotProgression,
                        "/payload/countProgression/instancesAddedPerSlotLevel", EvidenceRole.SCALING),
                MechanicEvidenceAssociation.of(slotActivation,
                        "/payload/countProgression", EvidenceRole.TRIGGER),
                MechanicEvidenceAssociation.of(allocationText,
                        "/payload/targetAllocation/cardinality", EvidenceRole.VALUE),
                MechanicEvidenceAssociation.of(allocationText,
                        "/payload/targetAllocation/targetReusePolicy", EvidenceRole.QUALIFIER),
                MechanicEvidenceAssociation.of(rangeText,
                        "/payload/targetAllocation/withinSpellRange", EvidenceRole.QUALIFIER),
                MechanicEvidenceAssociation.of(rangeText,
                        "/payload/targetAllocation/targetCategoryEligibilityStatus", EvidenceRole.SUBJECT),
                MechanicEvidenceAssociation.of(castTime,
                        "/payload/activationTiming", EvidenceRole.VALUE),
                MechanicEvidenceAssociation.of(rangeText, "/context/subject", EvidenceRole.SUBJECT));
        return assembly.claim("attack-roll", "scorching-ray-attack-roll-v1",
                new AttackRollMechanic(AttackKind.RANGED_SPELL, AttackActorRole.CASTER, 3, true,
                        AttackActivationTiming.CAST_TIME, progression, allocation),
                new MechanicContext(target, List.of()), associations, false);
    }

    private static MechanicRecord scorchingRayDamage(
            SpellMechanicsEvidenceSource source,
            SpellMechanicsAssembly assembly,
            UnknownSubject target,
            MechanicRecord attack) {
        require(attack != null, BODY);
        MarkupTag damageTag = singleFormula(source, "/entries/1", "damage");
        TextMechanicsEvidence formulaText = source.textEvidence("/entries/1", damageTag.originalText());
        TextMechanicsEvidence effectText = source.textEvidence("/entries/1",
                "the target takes {@damage 2d6} fire damage");
        TextMechanicsEvidence hitText = source.textEvidence("/entries/1", "On a hit");
        StructuredFieldMechanicsEvidence damageType = source.fieldEvidence("/damageInflict/0");
        AppliesOnEachAttackRollOutcome hitBinding = new AppliesOnEachAttackRollOutcome(
                attack.revision().reference(attack.id()), AttackRollOutcome.HIT,
                AttackRollInstanceScope.CORRESPONDING_ATTACK_INSTANCE, List.of(hitText.id()));
        return assembly.claim("damage", "scorching-ray-hit-damage-v1",
                new DamageMechanic(dice(damageTag, "/entries/1"), new FixedDamageType("fire"), null),
                new MechanicContext(target, List.of(hitBinding)),
                List.of(
                        MechanicEvidenceAssociation.of(effectText, "/payload", EvidenceRole.EFFECT_ROLE),
                        MechanicEvidenceAssociation.of(formulaText, "/payload/amount", EvidenceRole.VALUE),
                        MechanicEvidenceAssociation.of(damageType, "/payload/damageType", EvidenceRole.VALUE),
                        MechanicEvidenceAssociation.of(hitText, "/context/bindings/0", EvidenceRole.TRIGGER),
                        MechanicEvidenceAssociation.of(effectText, "/context/subject", EvidenceRole.SUBJECT)),
                false);
    }

    private static MechanicsDocument extractEldritchBlast(
            SpellDefinition spell,
            SpellMechanicsEvidenceSource source,
            MechanicsDocument original) {
        require(original.owner().equals(source.owner())
                        && original.owner().id().equals(ELDRITCH_BLAST)
                        && original.sources().equals(List.of(source.source().revision())),
                BODY);
        require(original.processing().state() == ProcessingState.NOT_ATTEMPTED, BODY);
        require(spell.entries().size() == 2
                        && spell.entries().stream().allMatch(TextEntryDte.class::isInstance),
                "/entries");
        require(spell.mechanics().spellAttacks().equals(List.of("R")), "/spellAttack");
        require(spell.mechanics().damageInflicted().equals(List.of("force")), "/damageInflict");

        SpellTargetSubject target = new SpellTargetSubject(TargetScope.SINGLE_TARGET_CREATURE);
        InstanceCountProgression progression = new CharacterLevelInstanceCount(List.of(
                new CharacterLevelInstanceCountTier(5, 2),
                new CharacterLevelInstanceCountTier(11, 3),
                new CharacterLevelInstanceCountTier(17, 4)));
        InstanceTargetAllocation allocation = new InstanceTargetAllocation(
                TargetSelectionCardinality.EXACTLY_ONE_PER_INSTANCE,
                List.of(TargetCategory.CREATURE), TargetReusePolicy.REUSE_ALLOWED, true);
        SpellMechanicsAssembly assembly = new SpellMechanicsAssembly(source, original,
                "phb-eldritch-blast-multi-attack");
        MechanicRecord attack = assembly.attempt("eldritch-blast-attack-roll-v1", MechanicFamily.ATTACK_ROLL,
                () -> eldritchBlastAttack(source, assembly, target, progression, allocation));
        assembly.attempt("eldritch-blast-hit-damage-v1", MechanicFamily.DAMAGE,
                () -> eldritchBlastDamage(source, assembly, target, attack));
        String root = source.source().jsonPointer();
        return assembly.finish(List.of(root + "/spellAttack", root + "/damageInflict", root + "/entries",
                        root + "/scalingLevelDice"),
                List.of(root + "/entries"));
    }

    private static MechanicRecord eldritchBlastAttack(
            SpellMechanicsEvidenceSource source,
            SpellMechanicsAssembly assembly,
            SpellTargetSubject target,
            InstanceCountProgression progression,
            InstanceTargetAllocation allocation) {
        TextMechanicsEvidence attackText = source.textEvidence(BODY,
                "Make a ranged spell attack against the target");
        TextMechanicsEvidence baseBeam = source.textEvidence(BODY, "A beam");
        TextMechanicsEvidence targetText = source.textEvidence(BODY, "a creature within range");
        TextMechanicsEvidence progressionText = source.textEvidence("/entries/1",
                "two beams at 5th level, three beams at 11th level, and four beams at 17th level");
        TextMechanicsEvidence allocationText = source.textEvidence("/entries/1",
                "You can direct the beams at the same target or at different ones");
        TextMechanicsEvidence independentText = source.textEvidence("/entries/1",
                "Make a separate attack roll for each beam");
        StructuredFieldMechanicsEvidence attackKind = source.fieldEvidence("/spellAttack/0");
        List<MechanicEvidenceAssociation> associations = new java.util.ArrayList<>(List.of(
                MechanicEvidenceAssociation.of(attackText, "/payload", EvidenceRole.EFFECT_ROLE),
                MechanicEvidenceAssociation.of(attackKind, "/payload/attackKind", EvidenceRole.VALUE),
                MechanicEvidenceAssociation.of(baseBeam, "/payload/attackCount", EvidenceRole.VALUE),
                MechanicEvidenceAssociation.of(independentText, "/payload/attacksIndependent", EvidenceRole.VALUE),
                MechanicEvidenceAssociation.of(targetText, "/context/subject", EvidenceRole.SUBJECT),
                MechanicEvidenceAssociation.of(targetText,
                        "/payload/targetAllocation/eligibleTargetCategories/0", EvidenceRole.SUBJECT),
                MechanicEvidenceAssociation.of(targetText,
                        "/payload/targetAllocation/withinSpellRange", EvidenceRole.QUALIFIER),
                MechanicEvidenceAssociation.of(allocationText,
                        "/payload/targetAllocation/cardinality", EvidenceRole.VALUE),
                MechanicEvidenceAssociation.of(allocationText,
                        "/payload/targetAllocation/targetReusePolicy", EvidenceRole.QUALIFIER)));
        for (int index = 0; index < 3; index++) {
            associations.add(MechanicEvidenceAssociation.of(progressionText,
                    "/payload/countProgression/tiers/" + index, EvidenceRole.SCALING));
        }
        return assembly.claim("attack-roll", "eldritch-blast-attack-roll-v1",
                new AttackRollMechanic(AttackKind.RANGED_SPELL, AttackActorRole.CASTER, 1, true,
                        AttackActivationTiming.CAST_TIME, progression, allocation),
                new MechanicContext(target, List.of()), associations, false);
    }

    private static MechanicRecord eldritchBlastDamage(
            SpellMechanicsEvidenceSource source,
            SpellMechanicsAssembly assembly,
            SpellTargetSubject target,
            MechanicRecord attack) {
        require(attack != null, BODY);
        String hitPhrase = "On a hit";
        String damagePhrase = "the target takes {@damage 1d10} force damage";
        String body = source.text(BODY).text();
        require(body.contains(hitPhrase + ", " + damagePhrase), BODY);
        MarkupTag damageTag = singleFormula(source, BODY, "damage");
        TextMechanicsEvidence effectText = source.textEvidence(BODY, damagePhrase);
        TextMechanicsEvidence formulaText = source.textEvidence(BODY, damageTag.originalText());
        TextMechanicsEvidence hitText = source.textEvidence(BODY, hitPhrase);
        TextMechanicsEvidence targetText = source.textEvidence(BODY, "a creature within range");
        StructuredFieldMechanicsEvidence damageType = source.fieldEvidence("/damageInflict/0");
        var hitBinding = new AppliesOnEachAttackRollOutcome(
                attack.revision().reference(attack.id()), AttackRollOutcome.HIT,
                AttackRollInstanceScope.CORRESPONDING_ATTACK_INSTANCE, List.of(hitText.id()));
        return assembly.claim("damage", "eldritch-blast-hit-damage-v1",
                new DamageMechanic(dice(damageTag, BODY), new FixedDamageType("force"), null),
                new MechanicContext(target, List.of(hitBinding)),
                List.of(
                        MechanicEvidenceAssociation.of(effectText, "/payload", EvidenceRole.EFFECT_ROLE),
                        MechanicEvidenceAssociation.of(formulaText, "/payload/amount", EvidenceRole.VALUE),
                        MechanicEvidenceAssociation.of(damageType, "/payload/damageType", EvidenceRole.VALUE),
                        MechanicEvidenceAssociation.of(targetText, "/context/subject", EvidenceRole.SUBJECT),
                        MechanicEvidenceAssociation.of(hitText, "/context/bindings/0", EvidenceRole.TRIGGER)),
                false);
    }

    private static Rule ruleFor(String ownerId) {
        return switch (ownerId) {
            case INFLECT_WOUNDS -> INFLECT_WOUNDS_RULE;
            case RAY_OF_FROST -> RAY_OF_FROST_RULE;
            case FIRE_BOLT -> FIRE_BOLT_RULE;
            case CHILL_TOUCH -> CHILL_TOUCH_RULE;
            case SHOCKING_GRASP -> SHOCKING_GRASP_RULE;
            default -> null;
        };
    }

    private static MechanicRecord attackRoll(
            Rule rule,
            SpellMechanicsEvidenceSource source,
            SpellMechanicsAssembly assembly) {
        TextMechanicsEvidence attackText = source.textEvidence(BODY, rule.attackPhrase());
        StructuredFieldMechanicsEvidence attackKind = source.fieldEvidence("/spellAttack/0");
        TextMechanicsEvidence targetText = rule.attackTargetPhrase() == null
                ? attackText : source.textEvidence(BODY, rule.attackTargetPhrase());
        var associations = new java.util.ArrayList<>(List.of(
                MechanicEvidenceAssociation.of(attackText, "/payload", EvidenceRole.EFFECT_ROLE),
                MechanicEvidenceAssociation.of(attackKind, "/payload/attackKind", EvidenceRole.VALUE)));
        addTargetEvidence(associations, targetText, rule.targetSubject());
        return assembly.claim(rule.attackSlot(), rule.attackPattern(),
                new AttackRollMechanic(rule.attackKind(), AttackActorRole.CASTER, 1, true,
                        AttackActivationTiming.CAST_TIME),
                new MechanicContext(rule.targetSubject(), List.of()),
                associations,
                false);
    }

    private static MechanicRecord hitDamage(
            SpellDefinition spell,
            Rule rule,
            SpellMechanicsEvidenceSource source,
            SpellMechanicsAssembly assembly,
            MechanicRecord attackRoll) {
        require(attackRoll != null, BODY);
        String body = source.text(BODY).text();
        require(body.contains(rule.hitPhrase() + ", " + rule.damagePhrase()), BODY);
        MarkupTag damageTag = singleFormula(source, BODY, "damage");
        TextMechanicsEvidence effectText = source.textEvidence(BODY, rule.damagePhrase());
        TextMechanicsEvidence formulaText = source.textEvidence(BODY, damageTag.originalText());
        TextMechanicsEvidence hitText = source.textEvidence(BODY, rule.hitPhrase());
        TextMechanicsEvidence targetText = source.textEvidence(BODY, rule.targetPhrase());
        StructuredFieldMechanicsEvidence damageType = source.fieldEvidence("/damageInflict/0");
        var outcome = new AppliesOnAttackRollOutcome(
                attackRoll.revision().reference(attackRoll.id()), AttackRollOutcome.HIT, List.of(hitText.id()));
        DiceAmount baseAmount = dice(damageTag, BODY);
        CharacterLevelDamageScaling scaling = null;
        List<MechanicEvidenceAssociation> scalingAssociations = List.of();
        if (rule.scalingDiagnosticId() != null) {
            try {
                var extraction = PhbCantripDamageScaling.extract(
                        spell, rule.scalingEntryPointer(), rule.scalingLabel(), baseAmount, source);
                scaling = extraction.progression();
                scalingAssociations = extraction.evidenceAssociations();
            } catch (SpellPatternMismatch mismatch) {
                assembly.abstain(rule.scalingDiagnosticId(), MechanicFamily.DAMAGE,
                        mismatch.reason(), mismatch.relativePointer());
            }
        }
        var associations = new java.util.ArrayList<>(List.of(
                MechanicEvidenceAssociation.of(effectText, "/payload", EvidenceRole.EFFECT_ROLE),
                MechanicEvidenceAssociation.of(formulaText, "/payload/amount", EvidenceRole.VALUE),
                MechanicEvidenceAssociation.of(damageType, "/payload/damageType", EvidenceRole.VALUE)));
        addTargetEvidence(associations, targetText, rule.targetSubject());
        associations.add(MechanicEvidenceAssociation.of(hitText, "/context/bindings/0", EvidenceRole.TRIGGER));
        associations.addAll(scalingAssociations);
        DamageMechanic payload = new DamageMechanic(baseAmount,
                new FixedDamageType(spell.mechanics().damageInflicted().getFirst()), scaling);
        MechanicContext context = new MechanicContext(rule.targetSubject(),
                List.<MechanicContextBinding>of(outcome));
        if (spell.id().value().equals(RAY_OF_FROST)) {
            return assembly.claimSuperseding(rule.damageSlot(), rule.damagePattern(), payload, context,
                    associations, new MechanicRevisionReference(
                            com.thedndpub.data.mechanics.model.MechanicId.of(source.owner(), rule.damageSlot()),
                            1,
                            "126f7880b1895357a0a51913971f334e071754f18931be4266f0c1427d952d2a"), false);
        }
        return assembly.claim(rule.damageSlot(), rule.damagePattern(), payload, context,
                associations, false);
    }

    private static void addTargetEvidence(
            List<MechanicEvidenceAssociation> associations,
            TextMechanicsEvidence targetText,
            SpellTargetSubject targetSubject) {
        if (targetSubject.eligibleTargetCategories() == null) {
            associations.add(MechanicEvidenceAssociation.of(targetText, "/context/subject", EvidenceRole.SUBJECT));
            return;
        }
        for (int index = 0; index < targetSubject.eligibleTargetCategories().size(); index++) {
            associations.add(MechanicEvidenceAssociation.of(targetText,
                    "/context/subject/eligibleTargetCategories/" + index, EvidenceRole.SUBJECT));
        }
    }

    private static void fireBoltObjectIgnition(
            SpellMechanicsEvidenceSource source,
            SpellMechanicsAssembly assembly) {
        TextMechanicsEvidence ignition = source.textEvidence(BODY,
                "A flammable object hit by this spell ignites if it isn't being worn or carried.");
        assembly.unsupported("fire-bolt-object-ignition", MechanicFamily.UNDETERMINED,
                List.of(ignition.id()));
    }

    private static MechanicRecord rayOfFrostSpeedReduction(
            SpellMechanicsEvidenceSource source,
            SpellMechanicsAssembly assembly,
            MechanicRecord attackRoll) {
        require(attackRoll != null, BODY);
        String speedPhrase = "its speed is reduced by 10 feet";
        String durationPhrase = "until the start of your next turn";
        TextMechanicsEvidence speedText = source.textEvidence(BODY, speedPhrase);
        TextMechanicsEvidence hitText = source.textEvidence(BODY, "On a hit");
        TextMechanicsEvidence targetText = source.textEvidence(BODY, "a creature within range");
        TextMechanicsEvidence durationText = source.textEvidence(BODY, durationPhrase);
        var hitBinding = new AppliesOnAttackRollOutcome(
                attackRoll.revision().reference(attackRoll.id()), AttackRollOutcome.HIT, List.of(hitText.id()));
        var durationBinding = new AppliesForDuration(
                new UntilTurnBoundary(TurnActor.CASTER, TurnBoundary.START_OF_NEXT_TURN),
                List.of(durationText.id()));

        return assembly.claim("movement-speed", "ray-of-frost-hit-speed-reduction-v1",
                new StatisticModifierMechanic(List.of(ModifiedStatistic.MOVEMENT_SPEED),
                        ModifierOperation.SUBTRACT, new IntegerAmount(10)),
                new MechanicContext(new SpellTargetSubject(TargetScope.SINGLE_TARGET_CREATURE),
                        List.of(hitBinding, durationBinding)),
                List.of(
                        MechanicEvidenceAssociation.of(speedText, "/payload", EvidenceRole.EFFECT_ROLE),
                        MechanicEvidenceAssociation.of(targetText, "/context/subject", EvidenceRole.SUBJECT),
                        MechanicEvidenceAssociation.of(hitText, "/context/bindings/0", EvidenceRole.TRIGGER),
                        MechanicEvidenceAssociation.of(durationText, "/context/bindings/1", EvidenceRole.QUALIFIER)),
                false);
    }

    private static MechanicRecord chillTouchRecoveryPrevention(
            SpellMechanicsEvidenceSource source,
            SpellMechanicsAssembly assembly,
            MechanicRecord attackRoll) {
        TextMechanicsEvidence effectText = source.textEvidence(BODY, "it can't regain hit points");
        TextMechanicsEvidence hitText = source.textEvidence(BODY, "On a hit");
        TextMechanicsEvidence durationText = source.textEvidence(BODY,
                "until the start of your next turn");
        TextMechanicsEvidence targetText = source.textEvidence(BODY, "a creature within range");
        require(attackRoll != null, BODY);
        var hitBinding = new AppliesOnAttackRollOutcome(
                attackRoll.revision().reference(attackRoll.id()), AttackRollOutcome.HIT, List.of(hitText.id()));
        var durationBinding = new AppliesForDuration(
                new UntilTurnBoundary(TurnActor.CASTER, TurnBoundary.START_OF_NEXT_TURN),
                List.of(durationText.id()));
        return assembly.claim("hit-point-recovery-prevention", "chill-touch-recovery-prevention-v1",
                new HitPointRecoveryPreventionMechanic(),
                new MechanicContext(CHILL_TOUCH_RULE.targetSubject(), List.of(hitBinding, durationBinding)),
                List.of(
                        MechanicEvidenceAssociation.of(effectText, "/payload", EvidenceRole.EFFECT_ROLE),
                        MechanicEvidenceAssociation.of(targetText, "/context/subject", EvidenceRole.SUBJECT),
                        MechanicEvidenceAssociation.of(hitText, "/context/bindings/0", EvidenceRole.TRIGGER),
                        MechanicEvidenceAssociation.of(durationText, "/context/bindings/1", EvidenceRole.QUALIFIER)),
                false);
    }

    private static MechanicRecord shockingGraspReactionPrevention(
            SpellMechanicsEvidenceSource source,
            SpellMechanicsAssembly assembly,
            MechanicRecord attackRoll) {
        TextMechanicsEvidence effectText = source.textEvidence(BODY, "it can't take reactions");
        TextMechanicsEvidence hitText = source.textEvidence(BODY, "On a hit");
        TextMechanicsEvidence durationText = source.textEvidence(BODY,
                "until the start of its next turn");
        TextMechanicsEvidence targetText = source.textEvidence(BODY, "a creature you try to touch");
        require(attackRoll != null, BODY);
        var hitBinding = new AppliesOnAttackRollOutcome(
                attackRoll.revision().reference(attackRoll.id()), AttackRollOutcome.HIT, List.of(hitText.id()));
        var durationBinding = new AppliesForDuration(
                new UntilTurnBoundary(TurnActor.AFFECTED_SUBJECT, TurnBoundary.START_OF_NEXT_TURN),
                List.of(durationText.id()));
        return assembly.claim("reaction-prevention", "shocking-grasp-reaction-prevention-v1",
                new ReactionPreventionMechanic(),
                new MechanicContext(SHOCKING_GRASP_RULE.targetSubject(), List.of(hitBinding, durationBinding)),
                List.of(
                        MechanicEvidenceAssociation.of(effectText, "/payload", EvidenceRole.EFFECT_ROLE),
                        MechanicEvidenceAssociation.of(targetText, "/context/subject", EvidenceRole.SUBJECT),
                        MechanicEvidenceAssociation.of(hitText, "/context/bindings/0", EvidenceRole.TRIGGER),
                        MechanicEvidenceAssociation.of(durationText, "/context/bindings/1", EvidenceRole.QUALIFIER)),
                false);
    }

    private static MechanicRecord chillTouchUndeadDisadvantage(
            SpellMechanicsEvidenceSource source,
            SpellMechanicsAssembly assembly,
            MechanicRecord attackRoll) {
        require(attackRoll != null, "/entries/1");
        TextMechanicsEvidence modifierText = source.textEvidence(
                "/entries/1", "has disadvantage on attack rolls against you");
        TextMechanicsEvidence triggerText = source.textEvidence("/entries/1", "If you hit");
        TextMechanicsEvidence undeadText = source.textEvidence("/entries/1", "an undead target");
        TextMechanicsEvidence affectedRollText = source.textEvidence("/entries/1", "attack rolls against you");
        TextMechanicsEvidence durationText = source.textEvidence("/entries/1",
                "until the end of your next turn");
        var hitBinding = new AppliesOnAttackRollOutcome(
                attackRoll.revision().reference(attackRoll.id()), AttackRollOutcome.HIT, List.of(triggerText.id()));
        var undeadCondition = new AppliesWhen(ApplicabilityCondition.AFFECTED_SUBJECT_IS_UNDEAD,
                List.of(undeadText.id()));
        var durationBinding = new AppliesForDuration(
                new UntilTurnBoundary(TurnActor.CASTER, TurnBoundary.END_OF_NEXT_TURN), List.of(durationText.id()));
        return assembly.claim("undead-attack-roll-disadvantage", "chill-touch-undead-attack-disadvantage-v1",
                new AttackRollModifierMechanic(AttackRollModifier.DISADVANTAGE,
                        new FutureAttackRollSelector(AttackRollParticipant.AFFECTED_SUBJECT,
                                AttackRollParticipant.CASTER)),
                new MechanicContext(CHILL_TOUCH_RULE.targetSubject(),
                        List.of(hitBinding, undeadCondition, durationBinding)),
                List.of(
                        MechanicEvidenceAssociation.of(modifierText, "/payload", EvidenceRole.EFFECT_ROLE),
                        MechanicEvidenceAssociation.of(affectedRollText, "/payload/affectedRolls/actor",
                                EvidenceRole.SUBJECT),
                        MechanicEvidenceAssociation.of(affectedRollText, "/payload/affectedRolls/target",
                                EvidenceRole.REFERENCE),
                        MechanicEvidenceAssociation.of(undeadText, "/context/subject", EvidenceRole.SUBJECT),
                        MechanicEvidenceAssociation.of(triggerText, "/context/bindings/0", EvidenceRole.TRIGGER),
                        MechanicEvidenceAssociation.of(undeadText, "/context/bindings/1", EvidenceRole.QUALIFIER),
                        MechanicEvidenceAssociation.of(durationText, "/context/bindings/2", EvidenceRole.DURATION)),
                false);
    }

    private static MechanicRecord shockingGraspMetalArmorAdvantage(
            SpellMechanicsEvidenceSource source,
            SpellMechanicsAssembly assembly,
            MechanicRecord attackRoll) {
        require(attackRoll != null, BODY);
        TextMechanicsEvidence modifierText = source.textEvidence(BODY, "You have advantage on the attack roll");
        TextMechanicsEvidence qualifierText = source.textEvidence(BODY,
                "the target is wearing armor made of metal");
        TextMechanicsEvidence targetText = source.textEvidence(BODY, "a creature you try to touch");
        TextMechanicsEvidence attackText = source.textEvidence(BODY,
                "Make a melee spell attack against the target");
        var condition = new AppliesWhen(ApplicabilityCondition.ATTACK_ROLL_TARGET_WEARING_ARMOR_MADE_OF_METAL,
                List.of(qualifierText.id()));
        return assembly.claim("metal-armor-attack-roll-advantage", "shocking-grasp-metal-armor-advantage-v1",
                new AttackRollModifierMechanic(AttackRollModifier.ADVANTAGE,
                        new ReferencedAttackRollSelector(attackRoll.revision().reference(attackRoll.id()))),
                new MechanicContext(SHOCKING_GRASP_RULE.targetSubject(), List.of(condition)),
                List.of(
                        MechanicEvidenceAssociation.of(modifierText, "/payload", EvidenceRole.EFFECT_ROLE),
                        MechanicEvidenceAssociation.of(attackText, "/payload/affectedRolls/attackRoll",
                                EvidenceRole.REFERENCE),
                        MechanicEvidenceAssociation.of(targetText, "/context/subject", EvidenceRole.SUBJECT),
                        MechanicEvidenceAssociation.of(qualifierText, "/context/bindings/0", EvidenceRole.QUALIFIER)),
                false);
    }

    private static void require(boolean condition, String pointer) {
        if (!condition) {
            throw new SpellPatternMismatch(UnsupportedReason.NO_SUPPORTED_PATTERN, pointer);
        }
    }

    private record Rule(
            String adapterId,
            String attackSlot,
            String damageSlot,
            String attackPattern,
            String damagePattern,
            AttackKind attackKind,
            String attackMarker,
            String damageType,
            String attackPhrase,
            String damagePhrase,
            String targetPhrase,
            String attackTargetPhrase,
            String hitPhrase,
            SpellTargetSubject targetSubject,
            String scalingLabel,
            String scalingDiagnosticId,
            int expectedEntryCount,
            String scalingEntryPointer) {
    }

    // Existing PHB extraction helpers are shared with the orchestration extractor.
    private static MarkupTag singleFormula(SpellMechanicsEvidenceSource source, String pointer, String tag) {
        return PhbSpellMechanicsExtractor.singleFormula(source, pointer, tag);
    }

    private static com.thedndpub.data.mechanics.model.DiceAmount dice(MarkupTag tag, String pointer) {
        return PhbSpellMechanicsExtractor.dice(tag, pointer);
    }
}
