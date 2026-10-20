package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.HashSet;
import java.util.List;

/** Source-backed target-selection constraints applied separately to each resolved instance. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record InstanceTargetAllocation(
        TargetSelectionCardinality cardinality,
        List<TargetCategory> eligibleTargetCategories,
        TargetReusePolicy targetReusePolicy,
        boolean withinSpellRange,
        TargetCategoryEligibilityStatus targetCategoryEligibilityStatus,
        List<InstanceTargetRequirement> requirements) {
    public InstanceTargetAllocation {
        ModelChecks.required(cardinality, "cardinality");
        if (eligibleTargetCategories != null) {
            eligibleTargetCategories = ModelChecks.list(eligibleTargetCategories);
            ModelChecks.require(!eligibleTargetCategories.isEmpty(), "Target categories cannot be empty");
            ModelChecks.require(eligibleTargetCategories.stream().allMatch(java.util.Objects::nonNull),
                    "Target categories cannot contain null values");
            ModelChecks.require(new HashSet<>(eligibleTargetCategories).size() == eligibleTargetCategories.size(),
                    "Target categories must be unique");
        }
        ModelChecks.required(targetReusePolicy, "targetReusePolicy");
        requirements = requirements == null ? List.of() : ModelChecks.list(requirements);
        ModelChecks.require(requirements.stream().allMatch(java.util.Objects::nonNull),
                "Instance target requirements cannot contain null values");
        ModelChecks.require(new HashSet<>(requirements).size() == requirements.size(),
                "Instance target requirements must be unique");
        if (targetCategoryEligibilityStatus == TargetCategoryEligibilityStatus.SOURCE_SPECIFIED) {
            ModelChecks.require(eligibleTargetCategories != null,
                    "Source-specified target categories must be listed");
        } else if (targetCategoryEligibilityStatus == TargetCategoryEligibilityStatus.UNSPECIFIED_IN_SOURCE
                || targetCategoryEligibilityStatus == TargetCategoryEligibilityStatus.NOT_ASSESSED) {
            ModelChecks.require(eligibleTargetCategories == null,
                    "Unspecified target categories cannot be treated as eligible categories");
        } else {
            ModelChecks.require(eligibleTargetCategories != null,
                    "Target categories require an explicit eligibility status");
        }
    }

    public InstanceTargetAllocation(TargetSelectionCardinality cardinality,
                                    List<TargetCategory> eligibleTargetCategories,
                                    TargetReusePolicy targetReusePolicy,
                                    boolean withinSpellRange) {
        this(cardinality, eligibleTargetCategories, targetReusePolicy, withinSpellRange,
                null, List.of());
    }

    public InstanceTargetAllocation(TargetSelectionCardinality cardinality,
                                    List<TargetCategory> eligibleTargetCategories,
                                    TargetReusePolicy targetReusePolicy,
                                    boolean withinSpellRange,
                                    TargetCategoryEligibilityStatus targetCategoryEligibilityStatus) {
        this(cardinality, eligibleTargetCategories, targetReusePolicy, withinSpellRange,
                targetCategoryEligibilityStatus, List.of());
    }
}
