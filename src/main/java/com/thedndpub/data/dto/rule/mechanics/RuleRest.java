package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.RuleReference;
import com.thedndpub.data.dto.rule.enums.RuleMechanicKind;
import com.thedndpub.data.dto.rule.mechanics.enums.RulePermittedRestActivity;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleRestDurationQualifier;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleRestInterruption;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleRestInterruptionKind;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleRestInterruptionOutcome;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleRestRecovery;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleRestUnit;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleRestVariant;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleRest(RuleMechanicKind kind, RuleMechanicSubtype subtype, Double duration, RuleRestUnit unit,
        RuleRestDurationQualifier durationQualifier, Integer minimumHitPointsToStart, Double minimumHoursBetweenStarts,
        List<RuleRestActivityLimit> activityLimits, List<RuleRestInterruption> interruptions,
        Integer physicalExertionInterruptionHours, RuleRestInterruptionOutcome interruptionOutcome,
        Double shortRestBenefitThresholdHours, RuleRestInterruptionDuration addedDurationPerInterruption,
        List<RuleRecoveryOutcome> recoveryOutcomes, List<RuleRestRecoveryLimitation> recoveryLimitations,
        RuleDeprivationProcedure deprivationProcedure, RuleRestInterruptionKind interruption,
        List<RuleRestRecovery> recovery,
        String componentId, RuleComponentApplicability appliesWhen, RuleRestVariant variant,
        List<RulePermittedRestActivity> permittedActivities, RuleReference conditionDuringSleep) implements RuleMechanic {
    public RuleRest {
        activityLimits = activityLimits == null ? List.of() : List.copyOf(activityLimits);
        interruptions = interruptions == null ? List.of() : List.copyOf(interruptions);
        recoveryOutcomes = recoveryOutcomes == null ? List.of() : List.copyOf(recoveryOutcomes);
        recoveryLimitations = recoveryLimitations == null ? List.of() : List.copyOf(recoveryLimitations);
        recovery = recovery == null ? List.of() : List.copyOf(recovery);
        permittedActivities = permittedActivities == null ? List.of() : List.copyOf(permittedActivities);
    }
}
