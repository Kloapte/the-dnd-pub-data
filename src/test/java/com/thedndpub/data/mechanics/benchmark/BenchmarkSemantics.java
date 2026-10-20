package com.thedndpub.data.mechanics.benchmark;

import com.thedndpub.data.mechanics.model.ActivatedBy;
import com.thedndpub.data.mechanics.model.AllTriggers;
import com.thedndpub.data.mechanics.model.AnyTrigger;
import com.thedndpub.data.mechanics.model.AppliesForDuration;
import com.thedndpub.data.mechanics.model.AppliesOnSaveOutcome;
import com.thedndpub.data.mechanics.model.AppliesOnAttackRollOutcome;
import com.thedndpub.data.mechanics.model.AppliesOnEachAttackRollOutcome;
import com.thedndpub.data.mechanics.model.AttackRollMechanic;
import com.thedndpub.data.mechanics.model.ConditionOnSaveOutcome;
import com.thedndpub.data.mechanics.model.ConditionEndsOnSaveSuccess;
import com.thedndpub.data.mechanics.model.ConditionEndsWithSpell;
import com.thedndpub.data.mechanics.model.RequiresConcentration;
import com.thedndpub.data.mechanics.model.ArmorClassModifierMechanic;
import com.thedndpub.data.mechanics.model.DamageMechanic;
import com.thedndpub.data.mechanics.model.EffectDurationMechanic;
import com.thedndpub.data.mechanics.model.HealingMechanic;
import com.thedndpub.data.mechanics.model.HitByAttackTrigger;
import com.thedndpub.data.mechanics.model.IncludesTriggeringAttack;
import com.thedndpub.data.mechanics.model.MechanicContext;
import com.thedndpub.data.mechanics.model.MechanicContextBinding;
import com.thedndpub.data.mechanics.model.MechanicFamily;
import com.thedndpub.data.mechanics.model.StatisticModifierMechanic;
import com.thedndpub.data.mechanics.model.DamageResistanceMechanic;
import com.thedndpub.data.mechanics.model.ModifierOnSaveOutcome;
import com.thedndpub.data.mechanics.model.AppliesWhen;
import com.thedndpub.data.mechanics.model.MechanicPayload;
import com.thedndpub.data.mechanics.model.MechanicRecord;
import com.thedndpub.data.mechanics.model.MechanicRevisionReference;
import com.thedndpub.data.mechanics.model.MechanicsEvidence;
import com.thedndpub.data.mechanics.model.MechanicsReferenceDisposition;
import com.thedndpub.data.mechanics.model.ReactionActivationMechanic;
import com.thedndpub.data.mechanics.model.SavingThrowRequirementMechanic;
import com.thedndpub.data.mechanics.model.SpellDamagePreventionMechanic;
import com.thedndpub.data.mechanics.model.SpellSlotScalingMechanic;
import com.thedndpub.data.mechanics.model.SpellMentionReference;
import com.thedndpub.data.mechanics.model.StructuredFieldMechanicsEvidence;
import com.thedndpub.data.mechanics.model.TargetedBySpellTrigger;
import com.thedndpub.data.mechanics.model.TextMechanicsEvidence;
import com.thedndpub.data.mechanics.model.TriggerExpression;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Explicit structural equivalence rules. No text interpretation, formula evaluation or fuzzy matching. */
final class BenchmarkSemantics {
    private BenchmarkSemantics() {
    }

    static MechanicFamily family(MechanicPayload payload) {
        return switch (payload) {
            case ArmorClassModifierMechanic ignored -> MechanicFamily.ARMOR_CLASS_MODIFIER;
            case ReactionActivationMechanic ignored -> MechanicFamily.REACTION_ACTIVATION;
            case EffectDurationMechanic ignored -> MechanicFamily.DURATION;
            case SpellDamagePreventionMechanic ignored -> MechanicFamily.SPELL_DAMAGE_PREVENTION;
            case DamageMechanic ignored -> MechanicFamily.DAMAGE;
            case com.thedndpub.data.mechanics.model.DamageInstanceSeriesMechanic ignored ->
                    MechanicFamily.DAMAGE_INSTANCE_SERIES;
            case HealingMechanic ignored -> MechanicFamily.HEALING;
            case SavingThrowRequirementMechanic ignored -> MechanicFamily.SAVING_THROW_REQUIREMENT;
            case SpellSlotScalingMechanic ignored -> MechanicFamily.SPELL_SLOT_SCALING;
            case com.thedndpub.data.mechanics.model.ConditionChangeMechanic ignored -> MechanicFamily.CONDITION;
            case com.thedndpub.data.mechanics.model.ConditionChoiceMechanic ignored -> MechanicFamily.CONDITION;
            case StatisticModifierMechanic ignored -> MechanicFamily.MODIFIER;
            case DamageResistanceMechanic ignored -> MechanicFamily.DAMAGE_RESISTANCE;
            case AttackRollMechanic ignored -> MechanicFamily.ATTACK_ROLL;
            case com.thedndpub.data.mechanics.model.HitPointRecoveryPreventionMechanic ignored ->
                    MechanicFamily.HEALING_PREVENTION;
            case com.thedndpub.data.mechanics.model.ReactionPreventionMechanic ignored ->
                    MechanicFamily.REACTION_RESTRICTION;
            case com.thedndpub.data.mechanics.model.AttackRollModifierMechanic ignored ->
                    MechanicFamily.ATTACK_ROLL_MODIFIER;
        };
    }

    static boolean contextMatches(ExpectedContext expected, MechanicContext actual,
                                  Map<String, MechanicRecord> assigned) {
        if (!expected.subject().equals(actual.subject()) || expected.bindings().size() != actual.bindings().size()) {
            return false;
        }
        for (int index = 0; index < expected.bindings().size(); index++) {
            ExpectedBinding expectation = expected.bindings().get(index);
            MechanicContextBinding binding = actual.bindings().get(index);
            boolean matches = switch (expectation) {
                case ExpectedActivation activation -> binding instanceof ActivatedBy actualActivation
                        && exactReference(assigned.get(activation.claimKey()), actualActivation.activation());
                case ExpectedDuration duration -> binding instanceof AppliesForDuration actualDuration
                        && duration.duration().equals(actualDuration.duration());
                case ExpectedSaveOutcome outcome -> binding instanceof AppliesOnSaveOutcome actualOutcome
                        && outcome.outcome() == actualOutcome.outcome()
                        && (outcome.damageOutcome() == null
                                || outcome.damageOutcome() == actualOutcome.damageOutcome())
                        && exactReference(assigned.get(outcome.claimKey()), actualOutcome.savingThrow());
                case ExpectedTriggeringAttack ignored -> binding instanceof IncludesTriggeringAttack;
            };
            if (!matches) {
                return false;
            }
        }
        return true;
    }

    private static boolean exactReference(MechanicRecord actual, MechanicRevisionReference reference) {
        return actual != null && actual.revision().reference(actual.id()).equals(reference);
    }

    static boolean evidenceMatches(MechanicsEvidence expected, MechanicsEvidence actual) {
        if (expected instanceof TextMechanicsEvidence text && actual instanceof TextMechanicsEvidence other) {
            return text.source().equals(other.source()) && text.syntax().equals(other.syntax())
                    && text.textSha256().equals(other.textSha256()) && text.origin() == other.origin()
                    && Objects.equals(text.authoredOwner(), other.authoredOwner());
        }
        if (expected instanceof StructuredFieldMechanicsEvidence field
                && actual instanceof StructuredFieldMechanicsEvidence other) {
            return field.source().equals(other.source()) && field.owner().equals(other.owner())
                    && field.jsonPointer().equals(other.jsonPointer()) && field.hierarchy().equals(other.hierarchy())
                    && field.valueSha256().equals(other.valueSha256()) && field.origin() == other.origin()
                    && Objects.equals(field.authoredOwner(), other.authoredOwner());
        }
        return false;
    }

    static boolean equivalentContext(MechanicContext left, MechanicContext right) {
        if (!left.subject().equals(right.subject()) || left.bindings().size() != right.bindings().size()) {
            return false;
        }
        for (int index = 0; index < left.bindings().size(); index++) {
            MechanicContextBinding first = left.bindings().get(index);
            MechanicContextBinding second = right.bindings().get(index);
            boolean same = switch (first) {
                case ActivatedBy activation -> second instanceof ActivatedBy other
                        && activation.activation().equals(other.activation());
                case AppliesForDuration duration -> second instanceof AppliesForDuration other
                        && duration.duration().equals(other.duration());
                case AppliesOnSaveOutcome outcome -> second instanceof AppliesOnSaveOutcome other
                        && outcome.outcome() == other.outcome()
                        && outcome.damageOutcome() == other.damageOutcome()
                        && outcome.savingThrow().equals(other.savingThrow());
                case ConditionOnSaveOutcome outcome -> second instanceof ConditionOnSaveOutcome other
                        && outcome.outcome() == other.outcome()
                        && outcome.savingThrow().equals(other.savingThrow());
                case ConditionEndsOnSaveSuccess ending -> second instanceof ConditionEndsOnSaveSuccess other
                        && ending.timing() == other.timing()
                        && ending.savingThrow().equals(other.savingThrow());
                case ConditionEndsWithSpell ignored -> second instanceof ConditionEndsWithSpell;
                case RequiresConcentration ignored -> second instanceof RequiresConcentration;
                case IncludesTriggeringAttack ignored -> second instanceof IncludesTriggeringAttack;
                case ModifierOnSaveOutcome outcome -> second instanceof ModifierOnSaveOutcome other
                        && outcome.outcome() == other.outcome()
                        && outcome.savingThrow().equals(other.savingThrow());
                case AppliesWhen applies -> second instanceof AppliesWhen other
                        && applies.condition() == other.condition();
                case AppliesOnAttackRollOutcome outcome -> second instanceof AppliesOnAttackRollOutcome other
                        && outcome.outcome() == other.outcome()
                        && outcome.attackRoll().equals(other.attackRoll());
                case AppliesOnEachAttackRollOutcome outcome -> second instanceof AppliesOnEachAttackRollOutcome other
                        && outcome.outcome() == other.outcome()
                        && outcome.instanceScope() == other.instanceScope()
                        && outcome.attackRoll().equals(other.attackRoll());
            };
            if (!same) {
                return false;
            }
        }
        return true;
    }

    static boolean hasResolvedReference(MechanicPayload payload) {
        return switch (payload) {
            case SpellDamagePreventionMechanic prevention -> resolved(prevention.spell());
            case ReactionActivationMechanic reaction -> resolvedTrigger(reaction.trigger());
            default -> false;
        };
    }

    private static boolean resolved(SpellMentionReference reference) {
        return reference.resolution() == MechanicsReferenceDisposition.RESOLVED;
    }

    private static boolean resolvedTrigger(TriggerExpression trigger) {
        return switch (trigger) {
            case TargetedBySpellTrigger targeted -> resolved(targeted.spell());
            case AnyTrigger any -> any.alternatives().stream().anyMatch(BenchmarkSemantics::resolvedTrigger);
            case AllTriggers all -> all.requirements().stream().anyMatch(BenchmarkSemantics::resolvedTrigger);
            case HitByAttackTrigger ignored -> false;
        };
    }

    static List<String> bindingEvidence(MechanicContextBinding binding) {
        return switch (binding) {
            case ActivatedBy activation -> activation.evidenceIds();
            case AppliesForDuration duration -> duration.evidenceIds();
            case AppliesOnSaveOutcome outcome -> outcome.evidenceIds();
            case ConditionOnSaveOutcome outcome -> outcome.evidenceIds();
            case ConditionEndsOnSaveSuccess ending -> ending.evidenceIds();
            case ConditionEndsWithSpell ending -> ending.evidenceIds();
            case RequiresConcentration concentration -> concentration.evidenceIds();
            case IncludesTriggeringAttack attack -> attack.evidenceIds();
            case ModifierOnSaveOutcome outcome -> outcome.evidenceIds();
            case AppliesWhen applies -> applies.evidenceIds();
            case AppliesOnAttackRollOutcome outcome -> outcome.evidenceIds();
            case AppliesOnEachAttackRollOutcome outcome -> outcome.evidenceIds();
        };
    }
}
