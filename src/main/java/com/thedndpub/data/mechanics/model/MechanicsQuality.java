package com.thedndpub.data.mechanics.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Deterministic model diagnostics only: it neither reads source prose nor evaluates rules. */
final class MechanicsQuality {
    private MechanicsQuality() {
    }

    static List<MechanicsFinding> findings(MechanicRevisionContent content) {
        List<MechanicsFinding> findings = new ArrayList<>(content.assessment().findings());
        if (content.context().subject() instanceof UnknownSubject) {
            add(findings, MechanicsFindingCode.UNKNOWN_SUBJECT, "/context/subject");
        }
        if (content.evidenceAssociations().stream().noneMatch(item -> item.role() == EvidenceRole.EFFECT_ROLE)) {
            add(findings, MechanicsFindingCode.MISSING_EVIDENCE, "/payload");
        }
        switch (content.payload()) {
            case DamageMechanic damage -> {
                amount(damage.amount(), "/payload/amount", findings);
                if (damage.damageType() instanceof UnknownDamageType) {
                    add(findings, MechanicsFindingCode.UNKNOWN_DAMAGE_TYPE, "/payload/damageType");
                }
                ModelChecks.require(!(damage.amount() instanceof UnknownAmount
                        && damage.damageType() instanceof UnknownDamageType),
                        "Wholly unknown damage belongs in unsupported constructs");
            }
            case DamageInstanceSeriesMechanic series ->
                    amount(series.amountPerInstance(), "/payload/amountPerInstance", findings);
            case HealingMechanic healing -> amount(healing.amount(), "/payload/amount", findings);
            case SpellSlotScalingMechanic scaling -> amount(scaling.increment(), "/payload/increment", findings);
            case SavingThrowRequirementMechanic save -> {
                if (save.ability() instanceof UnknownAbility) {
                    add(findings, MechanicsFindingCode.UNKNOWN_ABILITY, "/payload/ability");
                }
                if (save.difficulty() instanceof UnknownSaveDifficulty) {
                    add(findings, MechanicsFindingCode.UNKNOWN_SAVE_DIFFICULTY, "/payload/difficulty");
                }
            }
            case ReactionActivationMechanic reaction -> trigger(reaction.trigger(), "/payload/trigger", findings);
            case SpellDamagePreventionMechanic protection -> spell(protection.spell(), "/payload/spell", findings);
            case EffectDurationMechanic duration -> duration(duration.specification(), "/payload/specification", findings);
            case ConditionChangeMechanic ignored -> {
            }
            case ConditionChoiceMechanic ignored -> {
            }
            case ArmorClassModifierMechanic ignored -> {
                // A symbolic modifier is an identified variable, not an unresolved numeric calculation.
            }
            case StatisticModifierMechanic modifier -> amount(modifier.amount(), "/payload/amount", findings);
            case DamageResistanceMechanic ignored -> {
            }
            case AttackRollMechanic ignored -> {
            }
            case HitPointRecoveryPreventionMechanic ignored -> {
            }
            case ReactionPreventionMechanic ignored -> {
            }
            case AttackRollModifierMechanic ignored -> {
            }
        }
        Map<MechanicRevisionReference, Map<SaveOutcome, DamageOutcome>> saveOutcomes = new HashMap<>();
        DurationSpecification previousDuration = null;
        for (int index = 0; index < content.context().bindings().size(); index++) {
            MechanicContextBinding binding = content.context().bindings().get(index);
            String path = "/context/bindings/" + index;
            if (binding instanceof AppliesForDuration timing) {
                duration(timing.duration(), path + "/duration", findings);
                if (previousDuration != null && !previousDuration.equals(timing.duration())) {
                    add(findings, MechanicsFindingCode.CONTRADICTORY_RELATIONSHIPS, path);
                }
                previousDuration = timing.duration();
            } else if (binding instanceof AppliesOnSaveOutcome outcome) {
                DamageOutcome previous = saveOutcomes.computeIfAbsent(outcome.savingThrow(), ignored -> new HashMap<>())
                        .putIfAbsent(outcome.outcome(), outcome.damageOutcome());
                if (previous != null && previous != outcome.damageOutcome()) {
                    add(findings, MechanicsFindingCode.CONTRADICTORY_RELATIONSHIPS, path);
                }
            }
        }
        if (content.assessment().benchmarks().stream().anyMatch(result -> !result.passed())) {
            add(findings, MechanicsFindingCode.BENCHMARK_FAILURE, "/payload");
        }
        if (content.assessment().support() == MechanicsSupport.UNSUPPORTED) {
            add(findings, MechanicsFindingCode.UNSUPPORTED_SYNTAX, "/payload");
        }
        return List.copyOf(findings);
    }

    private static void amount(AmountExpression amount, String path, List<MechanicsFinding> findings) {
        if (amount instanceof UnknownAmount) {
            add(findings, MechanicsFindingCode.UNKNOWN_AMOUNT, path);
        } else if (amount instanceof SumAmount sum) {
            for (int index = 0; index < sum.terms().size(); index++) {
                amount(sum.terms().get(index), path + "/terms/" + index, findings);
            }
        }
    }

    private static void trigger(TriggerExpression trigger, String path, List<MechanicsFinding> findings) {
        switch (trigger) {
            case TargetedBySpellTrigger targeted -> spell(targeted.spell(), path + "/spell", findings);
            case AnyTrigger group -> {
                for (int index = 0; index < group.alternatives().size(); index++) {
                    trigger(group.alternatives().get(index), path + "/alternatives/" + index, findings);
                }
            }
            case AllTriggers group -> {
                for (int index = 0; index < group.requirements().size(); index++) {
                    trigger(group.requirements().get(index), path + "/requirements/" + index, findings);
                }
            }
            case HitByAttackTrigger ignored -> {
                // The event category is explicitly retained; no hit/attack equivalence is inferred.
            }
        }
    }

    private static void spell(SpellMentionReference spell, String path, List<MechanicsFinding> findings) {
        if (spell.resolution() != MechanicsReferenceDisposition.RESOLVED) {
            add(findings, MechanicsFindingCode.ESSENTIAL_REFERENCE_NOT_RESOLVED, path);
        }
    }

    private static void duration(DurationSpecification duration, String path, List<MechanicsFinding> findings) {
        if (duration instanceof UnknownDuration) {
            add(findings, MechanicsFindingCode.UNKNOWN_DURATION, path);
        }
    }

    static void add(List<MechanicsFinding> findings, MechanicsFindingCode code, String path) {
        MechanicsFinding finding = new MechanicsFinding(code, FindingSeverity.REQUIRES_REVIEW, path, List.of());
        if (!findings.contains(finding)) {
            findings.add(finding);
        }
    }
}
