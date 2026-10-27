package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.enums.RuleMechanicKind;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleDowntime(RuleMechanicKind kind, String subtype, String procedure, RuleDuration duration,
        RuleAbilityCheck check, String outcome, String minimumLifestyle, List<RuleRecoveryOutcome> recoveryOutcomes,
        String sourceDefinedRecoveryCadence, Boolean excludeOngoingMagicalEffects, String componentId,
        RuleComponentApplicability appliesWhen, RuleSavingThrow savingThrow, String recoverySelection,
        List<RuleRecoveryOutcome> ongoingBenefits) implements RuleMechanic {
    public RuleDowntime {
        recoveryOutcomes = recoveryOutcomes == null ? List.of() : List.copyOf(recoveryOutcomes);
        ongoingBenefits = ongoingBenefits == null ? List.of() : List.copyOf(ongoingBenefits);
    }
}
