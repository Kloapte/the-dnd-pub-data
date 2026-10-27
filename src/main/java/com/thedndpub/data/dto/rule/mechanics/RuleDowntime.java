package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.enums.RuleMechanicKind;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleDowntimeOutcome;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleDowntimeProcedure;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleDowntimeRecoveryCadence;
import com.thedndpub.data.dto.rule.mechanics.enums.RuleDowntimeRecoverySelection;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleDowntime(RuleMechanicKind kind, RuleMechanicSubtype subtype, RuleDowntimeProcedure procedure,
        RuleDowntimeDuration duration, RuleAbilityCheck check, RuleDowntimeOutcome outcome, String minimumLifestyle,
        List<RuleRecoveryOutcome> recoveryOutcomes, RuleDowntimeRecoveryCadence sourceDefinedRecoveryCadence,
        Boolean excludeOngoingMagicalEffects, String componentId,
        RuleComponentApplicability appliesWhen, RuleSavingThrow savingThrow, RuleDowntimeRecoverySelection recoverySelection,
        List<RuleRecoveryOutcome> ongoingBenefits) implements RuleMechanic {
    public RuleDowntime {
        recoveryOutcomes = recoveryOutcomes == null ? List.of() : List.copyOf(recoveryOutcomes);
        ongoingBenefits = ongoingBenefits == null ? List.of() : List.copyOf(ongoingBenefits);
    }
}
