package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.RuleReference;
import com.thedndpub.data.dto.rule.enums.RuleMechanicKind;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleRest(RuleMechanicKind kind, String subtype, Double duration, String unit,
        String durationQualifier, Integer minimumHitPointsToStart, Double minimumHoursBetweenStarts,
        List<RuleRestActivityLimit> activityLimits, List<String> interruptions,
        Integer physicalExertionInterruptionHours, String interruptionOutcome,
        Double shortRestBenefitThresholdHours, RuleDuration addedDurationPerInterruption,
        List<RuleRecoveryOutcome> recoveryOutcomes, List<RuleRestRecoveryLimitation> recoveryLimitations,
        RuleDeprivationProcedure deprivationProcedure, String interruption, List<String> recovery,
        String componentId, RuleComponentApplicability appliesWhen, String variant,
        List<String> permittedActivities, RuleReference conditionDuringSleep) implements RuleMechanic {
    public RuleRest {
        activityLimits = activityLimits == null ? List.of() : List.copyOf(activityLimits);
        interruptions = interruptions == null ? List.of() : List.copyOf(interruptions);
        recoveryOutcomes = recoveryOutcomes == null ? List.of() : List.copyOf(recoveryOutcomes);
        recoveryLimitations = recoveryLimitations == null ? List.of() : List.copyOf(recoveryLimitations);
        recovery = recovery == null ? List.of() : List.copyOf(recovery);
        permittedActivities = permittedActivities == null ? List.of() : List.copyOf(permittedActivities);
    }
}
