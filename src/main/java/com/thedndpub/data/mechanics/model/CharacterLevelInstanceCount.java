package com.thedndpub.data.mechanics.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/** Character-level thresholds for total active instances, not damage amount scaling. */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record CharacterLevelInstanceCount(List<CharacterLevelInstanceCountTier> tiers)
        implements InstanceCountProgression {
    public CharacterLevelInstanceCount {
        tiers = ModelChecks.list(tiers);
        ModelChecks.require(!tiers.isEmpty(), "Character-level instance count requires at least one tier");
        int previousLevel = 1;
        int previousCount = 0;
        for (CharacterLevelInstanceCountTier tier : tiers) {
            ModelChecks.required(tier, "tier");
            ModelChecks.require(tier.minimumCharacterLevel() > previousLevel,
                    "Character-level instance thresholds must be strictly increasing");
            ModelChecks.require(tier.totalInstances() > previousCount,
                    "Total instance counts must increase at each threshold");
            previousLevel = tier.minimumCharacterLevel();
            previousCount = tier.totalInstances();
        }
    }
}
