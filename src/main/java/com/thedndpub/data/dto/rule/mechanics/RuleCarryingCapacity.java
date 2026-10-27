package com.thedndpub.data.dto.rule.mechanics;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.thedndpub.data.dto.rule.enums.RuleMechanicKind;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record RuleCarryingCapacity(String componentId, RuleMechanicKind kind, RuleMechanicSubtype subtype, String weightUnit,
        String strengthBasis, List<RuleSizeCapacityMultiplier> sizeMultipliers,
        List<RuleEncumbranceThreshold> encumbranceThresholds, String heavyEncumbranceMaximum,
        Double dragLiftPushSpeedCapFeet, Boolean speedCapAppliesWhenAboveCarryCapacity,
        String thresholdComparison) implements RuleMechanic {
    public RuleCarryingCapacity {
        sizeMultipliers = sizeMultipliers == null ? List.of() : List.copyOf(sizeMultipliers);
        encumbranceThresholds = encumbranceThresholds == null ? List.of() : List.copyOf(encumbranceThresholds);
    }
}
